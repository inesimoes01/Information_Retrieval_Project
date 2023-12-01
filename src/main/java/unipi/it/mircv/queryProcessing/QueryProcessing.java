package unipi.it.mircv.queryProcessing;
import unipi.it.mircv.common.*;

import unipi.it.mircv.preprocessing.Preprocessing;

import java.util.*;

public class QueryProcessing {

    public static void mainQueryProcessing(){
        Scanner input = new Scanner(System.in);
        Preprocessing preprocessing = new Preprocessing();
        Scoring scoring = new Scoring();

        System.out.println("Insert query: ");
        String query = input.nextLine();

        System.out.println("Conjunctive (c) or disjunctive (d) processing? ");

        Flags.setFlagIsConjunctive(input.nextLine().equals("c"));

        long start_time = System.currentTimeMillis();

        // clean text
        preprocessing.clean(query);
        String[] queryParts = query.split(" ");

        List<Integer> relevantDocs;
        // conjunctive (all terms must appear) or disjunctive (at least one term must appear)
        if(Flags.isConjunctive()){
            relevantDocs = conjunctiveProcessing(queryParts);
        }else{
            relevantDocs = disjunctiveProcessing(queryParts);
        }


        // scoring results
        HashMap<Integer, Double> scoredDocs = new HashMap<>();
        double scores;
        for (Integer docId : relevantDocs){
            scores = 0.0;
            for (String term : queryParts) {
                scores += scoring.computeTFIDF(term, docId);
                System.out.println("score for docid " + docId + ": "+ scores + " with term \"" + term + "\"");
            }
            if(scores < 1) scoredDocs.put(docId, scores);
        }

        // sorting results
        LinkedHashMap<Integer, Double> scoredDocsSorted = getScoredDocsSorted(scoredDocs);
        System.out.println(scoredDocsSorted);


        long end_time = System.currentTimeMillis();
        long processingTime = end_time - start_time;
        System.out.println("Query Processing took " + (double) processingTime/1000 + " seconds.");
    }



    private static LinkedHashMap<Integer, Double> getScoredDocsSorted(HashMap<Integer, Double> scoredDocs) {
        List<Map.Entry<Integer, Double>> scoredDocsSortedList = new ArrayList<>(scoredDocs.entrySet());
        scoredDocsSortedList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        LinkedHashMap<Integer, Double> scoredDocsSorted = new LinkedHashMap<>();
        for (Map.Entry<Integer, Double> entry : scoredDocsSortedList) {
            scoredDocsSorted.put(entry.getKey(), entry.getValue());
        }
        return scoredDocsSorted;
    }

    private static List<Integer> conjunctiveProcessing(String[] query){
        List<Integer> result = new ArrayList<>();
        boolean termNonExistent = false;

        for (String term : query) {
            OutputResultsReader outputResultsReader = new OutputResultsReader();
            List<Integer> docsWithTerms;

            // save all documents that match the query terms
            if (outputResultsReader.searchTermInInvertedIndex(term)) {
                docsWithTerms = outputResultsReader.getTermDocidFreq().get(term);
                System.out.println("Found term " + term + " with " + docsWithTerms);
                //result.addAll(saveDocIds(docsWithTerms));
            }else {
                System.out.println("Term \"" + term + "\" not found");
                termNonExistent = true;
                break;
            }
            // System.out.println("IDs with term " + term + ": " + docsWithTerms);
            // System.out.println("IDS: " + saveDocIds(docsWithTerms));
            // keeps only documents with all query terms
            if (result.isEmpty()) {

                result.addAll(saveDocIds(docsWithTerms));
            } else {
                result.retainAll(saveDocIds(docsWithTerms));
            }
        }

        // if one of the terms does not exist, result should be empty
        if (termNonExistent){
            result.clear();
        }

        System.out.println("Result: " + result);
        return result;
    }


    private static List<Integer> disjunctiveProcessing(String[] query){
        List<Integer> result = new ArrayList<>();
        for (String term : query) {
            OutputResultsReader outputResultsReader = new OutputResultsReader();

            // save all documents that match the query terms
            if (outputResultsReader.searchTermInInvertedIndex(term)) {
                List<Integer> docsWithTerms = outputResultsReader.getTermDocidFreq().get(term);
                result.addAll(saveDocIds(docsWithTerms));
            }else {
                System.out.println("Term \"" + term + "\" not found");
                break;
            }
        }
        System.out.println("Result: " + result);
        return result;
    }

    private static List<Integer> saveDocIds(List<Integer> docsWithTerms){
        List<Integer> selectedElements = new ArrayList<>();
        for (int i = 0; i < docsWithTerms.size(); i++) {
            if (i % 2 == 0) {
                selectedElements.add(docsWithTerms.get(i));
            }
        }
        return selectedElements;
    }
}

//        // check if query terms are in the collection
//        for (String term : queryParts) {
//            if(documentLexicon.containsKey(term)){
//                System.out.println("None of the query terms are in the collection, please retry: ");
//                break;
//            }
//        }