package unipi.it.mircv.queryProcessing;

import unipi.it.mircv.common.*;

import unipi.it.mircv.preprocessing.Preprocessing;

import java.util.*;

public class QueryProcessing {

    public void mainQueryProcessing(){
        Scanner input = new Scanner(System.in);
        Preprocessing preprocessing = new Preprocessing();
        ScoringStrategy strategy = new ScoringStrategy();
        List<TermDictionary> termList = new ArrayList<>();

        System.out.println("Insert query: ");
        String query = input.nextLine();
        System.out.println("DAAT (d) or MaxScore (m) processing? ");
        Flags.setIsDAAT_flag(input.nextLine().equals("d"));
        System.out.println("TFIDF (t) or BM25 (b) processing? ");
        Flags.setIsTFIDF_flag(input.nextLine().equals("t"));
        System.out.println("How many documents?");
        Flags.setNumberOfDocuments(Integer.parseInt(input.nextLine()));
        long start_time = System.currentTimeMillis();

        // clean text by prepocessing
        query = preprocessing.clean(query);
        String[] queryPartsOriginal = query.split(" ");

        // save relevant docs
        List<DocumentQP> relevantDocs;
        List<String> termsToRemove = new ArrayList<>();
        relevantDocs = ScoringStrategy.disjunctiveProcessing(termList, queryPartsOriginal, termsToRemove);

        // TODO remove later, only temporary
        calculateTermUpperBounds(termList, relevantDocs);

        if (!relevantDocs.isEmpty()){
            // remove terms that do not exist in the collection from query
//            String[] queryPartsFiltered = removeTerms(queryPartsOriginal, termsToRemove);
//            System.out.println("Final Query " + Arrays.toString(queryPartsFiltered));

            // scoring results using DAAT or MaxScore and TFDIF or BM25
            List<DocumentQP> scoredResults = strategy.scoringStrategy(termList, relevantDocs, Flags.getNumberOfDocuments());

            // print results
            for (DocumentQP doc : scoredResults) System.out.println(doc.getDocId() + " " + doc.getScore());
        }

        long end_time = System.currentTimeMillis();
        long processingTime = end_time - start_time;
        System.out.println("Query Processing took " + (double) processingTime/1000 + " seconds.");
    }


//    private String[] removeTerms(String[] original, List<String > termsToRemove){
//
//        List<String> originalTerms = new ArrayList<>(Arrays.asList(original));
//
//
//        List<String> filteredList = new ArrayList<>();
//
//        for (String term : originalTerms) {
//            if (!termsToRemove.contains(term)) {
//                filteredList.add(term);
//            }
//        }
//        return filteredList.toArray(new String[0]);
//    }

    private void calculateTermUpperBounds(List<TermDictionary> termList, List<DocumentQP> docList){
        Ranking ranking = new Ranking();
        double scoreValue;
        boolean first;

        for (TermDictionary term : termList){
            for (TermDictionary.Posting doc : term.getPostingList()){
                first = true;
                for (DocumentQP doc2 : docList){
                    if (doc2.getDocId().equals(doc.getDocId())){
                        scoreValue = ranking.computeTermUpperBound(term, doc2);
                        if (first) {
                            term.setTermUpperBound(scoreValue);
                            first = false;
                        }
                        if (scoreValue > term.getTermUpperBound()) term.setTermUpperBound(scoreValue);
                    }
                }
            }
        }

    }

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

//    private static boolean termAlreadySaved(List<TermDictionary> termList, String queryTerm){
//        boolean termExists = false;
//        for (TermDictionary term : termList) {
//            if (term.getTerm().equals(queryTerm)) {
//                termExists = true;
//                break;
//            }
//        }
//        return termExists;
//    }

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