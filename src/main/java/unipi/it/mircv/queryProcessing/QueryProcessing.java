package unipi.it.mircv.queryProcessing;
import unipi.it.mircv.common.*;

import unipi.it.mircv.common.Lexicon;
import unipi.it.mircv.common.TermStats;
import unipi.it.mircv.preprocessing.Preprocessing;

import java.util.*;

public class QueryProcessing {

    public static void mainQueryProcessing(){
        Scanner input = new Scanner(System.in);
        Preprocessing preprocessing = new Preprocessing();
        Lexicon lexicon = new Lexicon();
        Scoring score = new Scoring();

        System.out.println("Insert query: ");
        String query = input.nextLine();

        long start_time = System.currentTimeMillis();

        // clean text
        preprocessing.clean(query);
        String[] queryParts = query.split(" ");

        HashMap<String, TermStats> documentLexicon = lexicon.getLexicon();

//        // check if query terms are in the collection
//        for (String term : queryParts) {
//            if(documentLexicon.containsKey(term)){
//                System.out.println("None of the query terms are in the collection, please retry: ");
//                break;
//            }
//        }

        // conjunctive (all terms must appear) or disjunctive (at least one term must appear)
        if(Flags.isConjunctive()){
            conjunctiveProcessing(queryParts);
        }else{
            disjunctiveProcessing(queryParts);
        }


        long end_time = System.currentTimeMillis();
        long processingTime = end_time - start_time;
        System.out.println("Query Processing took " + processingTime + " milliseconds.");
    }

    private static void conjunctiveProcessing(String[] query){
        List<Integer> result = new ArrayList<>();

        for (String term : query) {
            FileReader fileReader = new FileReader();
            List<Integer> docsWithTerms = null;

            // save all documents that match the query terms
            if (fileReader.searchTermInFile(term)) {
                docsWithTerms = new ArrayList<>(fileReader.getFrequenciesByDocId().keySet());
            }else {
                System.out.println("Term \"" + term + "\" not found");
            }
            System.out.println("IDs with term " + term + ": " + docsWithTerms);

            // keep only documents with all query terms
            if (docsWithTerms != null){
                if (result.isEmpty()) {
                    result.addAll(docsWithTerms);
                } else {
                    // retains only common documents
                    result.retainAll(docsWithTerms);
                }
            }
        }
        System.out.println("Result: " + result);
    }
    private static void disjunctiveProcessing(String[] query){
        List<Integer> result = new ArrayList<>();
        for (String term : query) {
            FileReader fileReader = new FileReader();

            // save all documents that match the query terms
            if (fileReader.searchTermInFile(term)) {
                List<Integer> docsWithTerms;
                docsWithTerms = new ArrayList<>(fileReader.getFrequenciesByDocId().keySet());
                result.addAll(docsWithTerms);
            }else {
                System.out.println("Term \"" + term + "\" not found");
            }
            System.out.println("IDs with term " + term + ": " + result);
        }
        System.out.println("Result: " + result);
    }

}
