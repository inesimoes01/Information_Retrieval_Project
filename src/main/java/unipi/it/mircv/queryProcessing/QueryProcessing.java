package unipi.it.mircv.queryProcessing;
import unipi.it.mircv.common.*;

import unipi.it.mircv.preprocessing.Preprocessing;

import java.util.*;

public class QueryProcessing {

    public static void mainQueryProcessing(){
        Scanner input = new Scanner(System.in);
        Preprocessing preprocessing = new Preprocessing();
        Scoring scoring = new Scoring();

        List<TermQP> termList = new ArrayList<>();
        List<DocumentQP> documentList = new ArrayList<>();

        System.out.println("Insert query: ");
        String query = input.nextLine();
        System.out.println("Conjunctive (c) or disjunctive (d) processing? ");
        Flags.setIsConjunctive_flag(input.nextLine().equals("c"));
        System.out.println("DAAT (d) or MaxScore (m) processing? ");
        Flags.setIsDAAT_flag(input.nextLine().equals("d"));
        System.out.println("TFIDF (d) or BM25 (m) processing? ");
        Flags.setIsTFIDF_flag(input.nextLine().equals("d"));
        long start_time = System.currentTimeMillis();

        // clean text by prepocessing
        preprocessing.clean(query);
        String[] queryPartsOriginal = query.split(" ");

        // conjunctive (all terms must appear) or disjunctive (at least one term must appear)
        List<DocumentQP> relevantDocs;
        List<String> termsToRemove = new ArrayList<>();

        // fills the termList
        if(Flags.isIsConjunctive_flag()) relevantDocs = conjunctiveProcessing(termList, queryPartsOriginal, termsToRemove);
        else relevantDocs = disjunctiveProcessing(termList, queryPartsOriginal, termsToRemove);

        if (relevantDocs != null){
            // remove terms that do not exist in the collection
            String[] queryPartsFiltered = removeTerms(queryPartsOriginal, termsToRemove);
            System.out.println("FINAL QUERY " + Arrays.toString(queryPartsFiltered));

            for (DocumentQP doc : relevantDocs){
                System.out.print(doc.getDocId() + " ");
            }

            // scoring results using DAAT or MaxScore and TFDIF or BM25
            ScoringStrategy strategy = new ScoringStrategy();
            strategy.scoringStrategy(termList, relevantDocs, queryPartsFiltered);
        }


        long end_time = System.currentTimeMillis();
        long processingTime = end_time - start_time;
        System.out.println("Query Processing took " + (double) processingTime/1000 + " seconds.");
    }



    // save only the documents that match all the query terms
    private static List<DocumentQP> conjunctiveProcessing(List<TermQP> termList, String[] query, List<String> termNonExistent){
        List<DocumentQP> docsWithTerms = new ArrayList<>();

        for (String term : query) {
            // fills the term list
            TermQP termInstance = OutputResultsReader.fillTermQP(termList, term);

            // if query term does not exist in the collection, return null
            if (termInstance == null){
                System.out.println("Term \"" + term + "\" not found");
                termNonExistent.add(term);
                docsWithTerms.clear();
                return null;
            } else {
                // keeps only documents with all query terms
                if (docsWithTerms.isEmpty()) {
                    docsWithTerms.addAll(termInstance.getDocumentsWithTerm());
                } else {
                    docsWithTerms.retainAll(termInstance.getDocumentsWithTerm());
                }
            }
        }
//        Set<DocumentQP> documentSet = new HashSet<>(documentList);
//        documentList = new ArrayList<>(documentSet);

        Set<DocumentQP> docsWithTermsSet = new HashSet<>(docsWithTerms);
        docsWithTerms = new ArrayList<>(docsWithTermsSet); // Convert Set back to List

        System.out.println("Finished");
        for (DocumentQP doc : docsWithTerms){
            System.out.println("Result: " + doc.getDocId());
        }

        return removeDuplicates(docsWithTerms);
    }


    private static List<DocumentQP> disjunctiveProcessing(List<TermQP> termList, String[] query, List<String> termNonExistent){
        List<DocumentQP> docsWithTerms = new ArrayList<>();
        for (String term : query) {
            // fills the term list
            TermQP termInstance = OutputResultsReader.fillTermQP(termList, term);

            // if query term does not exist in the collection, return null
            if (termInstance == null){
                System.out.println("Term \"" + term + "\" not found");
                termNonExistent.add(term);
            } else {
                // keeps all documents
                docsWithTerms.addAll(termInstance.getDocumentsWithTerm());
            }
        }

        return removeDuplicates(docsWithTerms);
    }

    private static String[] removeTerms(String[] original, List<String > termsToRemove){

        List<String> originalTerms = new ArrayList<>(Arrays.asList(original));
        //List<String> termsToRemoveList = Arrays.asList(termsToRemove);

        List<String> filteredList = new ArrayList<>();

        for (String term : originalTerms) {
            if (!termsToRemove.contains(term)) {
                filteredList.add(term);
            }
        }
        return filteredList.toArray(new String[0]);
    }

    public static List<DocumentQP> removeDuplicates(List<DocumentQP> list) {
        List<DocumentQP> uniqueList = new ArrayList<>();
        boolean exist = false;
        for (DocumentQP element : list) {
            exist = false;
            for (DocumentQP uniqueElement : uniqueList){
                if (uniqueElement.getDocId().equals(element.getDocId())){
                    exist = true;
                    break;
                }
            }
            if (!exist) uniqueList.add(element);
        }
        return uniqueList;
    }

//    private static List<Integer> saveDocIds(List<Set<Integer>> docsWithTerms){
//        List<Integer> selectedElements = new ArrayList<>();
//        for (int i = 0; i < docsWithTerms.size(); i++) {
//            if (i % 2 == 0) {
//                selectedElements.add(docsWithTerms.get(i));
//            }
//        }
//        return selectedElements;
//    }

    private static boolean termAlreadySaved(List<TermQP> termList, String queryTerm){
        boolean termExists = false;
        for (TermQP term : termList) {
            if (term.getTerm().equals(queryTerm)) {
                termExists = true;
                break;
            }
        }
        return termExists;
    }
}

//        // check if query terms are in the collection
//        for (String term : queryParts) {
//            if(documentLexicon.containsKey(term)){
//                System.out.println("None of the query terms are in the collection, please retry: ");
//                break;
//            }
//        }

//        if (Flags.isIsDAAT_flag()){
////            HashMap<Integer, Double> scoredDocs = new HashMap<>();
////            double scores;
////            for (Integer docId : relevantDocs){
////                scores = 0.0;
////                for (String term : queryParts) {
////                    scores += scoring.computeScoring(term, docId);
////                    System.out.println("score for docid " + docId + ": "+ scores + " with term \"" + term + "\"");
////                }
////                if(scores < 1) scoredDocs.put(docId, scores);
////            }
//            ScoringStrategy.
//        } else {
//
//        }


//    private static List<Integer> conjunctiveProcessing(List<TermQP> termList, String[] query){
//        //List<Integer> result = new ArrayList<>();
//        Set<Integer> docsWithTerms = new HashSet<>();
//        boolean termNonExistent = false;
//
//        for (String term : query) {
//            TermQP termInstance = OutputResultsReader.fillTermQP(term);
//
//            // if query term exists in the collection
//            if (!termInstance != null){
//                // save all docIds
//                docsWithTerms.addAll(termInstance.getTermDocidFreq().keySet());
//                System.out.println("Found term " + term + " with " + docsWithTerms);
//                //result.addAll(saveDocIds(docsWithTerms));
//            }else {
//                System.out.println("Term \"" + term + "\" not found");
//                docsWithTerms.clear();
//                break;
//            }
//            // System.out.println("IDs with term " + term + ": " + docsWithTerms);
//            // System.out.println("IDS: " + saveDocIds(docsWithTerms));
//            // keeps only documents with all query terms
//            if (docsWithTerms.isEmpty()) {
//                docsWithTerms.addAll(termInstance.getTermDocidFreq().keySet());
//            } else {
//                docsWithTerms.retainAll(termInstance.getTermDocidFreq().keySet());
//            }
//
//            if (termNonExistent){
//                result.clear();
//            }
//        }

//    private static List<Integer> disjunctiveProcessing(String[] query){
//        List<Integer> result = new ArrayList<>();
//        for (String term : query) {
//            OutputResultsReader outputResultsReader = new OutputResultsReader();
//
//            // save all documents that match the query terms
//            if (outputResultsReader.searchTermInInvertedIndex(term)) {
//                List<Integer> docsWithTerms = outputResultsReader.getTermDocidFreq().get(term);
//                result.addAll(saveDocIds(docsWithTerms));
//            }else {
//                System.out.println("Term \"" + term + "\" not found");
//                break;
//            }
//        }
//        System.out.println("Result: " + result);
//        return result;