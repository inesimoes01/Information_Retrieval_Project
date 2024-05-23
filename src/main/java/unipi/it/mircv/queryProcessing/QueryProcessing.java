package unipi.it.mircv.queryProcessing;

import unipi.it.mircv.common.*;

import unipi.it.mircv.common.dataStructures.*;
import unipi.it.mircv.evalution.Evaluation;
import unipi.it.mircv.evalution.QueryStructure;
import unipi.it.mircv.preprocessing.Preprocessing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class QueryProcessing {
    private static long timeForQueryProcessing;
    private static TreeMap<String, TermDictionary> lexicon = new TreeMap<>();
    private static TreeMap<Integer, Integer> documentIndex = new TreeMap<>();

    private static List<Integer> queriesIds = new ArrayList<>();

    public static void mainQueryProcessing() throws IOException {
        TerminalDemo terminal = new TerminalDemo();
        Evaluation evaluation = new Evaluation();

        Scanner scanner = new Scanner(System.in);
        String userInput;

        // load document index and lexicon into memory
        loadLexicon();
        loadDocumentIndex();
        OutputResultsReader.saveTotalNumberDocs();

        if (!Flags.isIsEvaluation()){
            do {
                processing(terminal.runTerminal(), null);
                System.out.print("Do you want to another query? (y/n): ");
                userInput = scanner.nextLine();
            } while (userInput.equalsIgnoreCase("y"));


        } else {

            //queriesIds = saveQueriesToCompare();

//            Flags.setIsDAAT_flag(true);
//            Flags.setIsTFIDF_flag(true);
//            Flags.setIsConjunctive_flag(true);
//            Flags.setNumberOfDocuments(500);
//            evaluation.mainEvaluation(Paths.PATH_EVALUATION_INPUT);
//
//            Flags.setIsDAAT_flag(true);
//            Flags.setIsTFIDF_flag(false);
//            Flags.setIsConjunctive_flag(true);
//            Flags.setNumberOfDocuments(500);
//            evaluation.mainEvaluation(Paths.PATH_EVALUATION_INPUT);

//            Flags.setIsDAAT_flag(false);
//            Flags.setIsTFIDF_flag(true);
//            Flags.setIsConjunctive_flag(true);
//            Flags.setNumberOfDocuments(500);
//            evaluation.mainEvaluation(Paths.PATH_EVALUATION_INPUT);

//            Flags.setIsDAAT_flag(false);
//            Flags.setIsTFIDF_flag(false);
//            Flags.setIsConjunctive_flag(true);
//            Flags.setNumberOfDocuments(500);
//            evaluation.mainEvaluation(Paths.PATH_EVALUATION_INPUT);

            Flags.setIsDAAT_flag(false);
            Flags.setIsTFIDF_flag(true);
            Flags.setIsConjunctive_flag(false);
            Flags.setNumberOfDocuments(500);
            evaluation.mainEvaluation(Paths.PATH_EVALUATION_INPUT);
//
//            Flags.setIsDAAT_flag(false);
//            Flags.setIsTFIDF_flag(false);
//            Flags.setIsConjunctive_flag(false);
//            Flags.setNumberOfDocuments(500);
//            evaluation.mainEvaluation(Paths.PATH_EVALUATION_INPUT);
//
//            Flags.setIsDAAT_flag(true);
//            Flags.setIsTFIDF_flag(true);
//            Flags.setIsConjunctive_flag(false);
//            Flags.setNumberOfDocuments(500);
//            evaluation.mainEvaluation(Paths.PATH_EVALUATION_INPUT);
//
//            Flags.setIsDAAT_flag(true);
//            Flags.setIsTFIDF_flag(true);
//            Flags.setIsConjunctive_flag(false);
//            Flags.setNumberOfDocuments(500);
//            evaluation.mainEvaluation(Paths.PATH_EVALUATION_INPUT);

        }

    }
    public static void processing(String query, QueryStructure struct) throws IOException {
        List<TermDictionary> termList = new ArrayList<>();
        long start_time = System.currentTimeMillis();

        // clean text by pre-processing
        query = Preprocessing.clean(query);
        String[] queryPartsOriginal = query.split(" ");

        PriorityQueue<TopDocuments> scoredResults = new PriorityQueue<>(new TopDocumentsComparator());
        scoredResults = ScoringStrategy.scoringStrategy(queryPartsOriginal, Flags.getNumberOfDocuments());

        // save relevant docs with disjunctive or conjunctive querying
        List<Integer> relevantDocs;
        List<String> termsToRemove = new ArrayList<>();
        relevantDocs = ConjuntiveDisjunctive.processingStrategy(termList, queryPartsOriginal, termsToRemove);
        assert relevantDocs != null;

        // scoring results using DAAT or MaxScore and TFDIF or BM25
//        if (relevantDocs != null) {
//            if (!relevantDocs.isEmpty()) {
//                //Map<Integer, Double> scoredResults = ScoringStrategy.scoringStrategy(termList, relevantDocs, Flags.getNumberOfDocuments());
//
//                if (Flags.isIsEvaluation()) {
//                    struct.setDocumentEval(scoredResults);
//
//                } else {
//                    for (Map.Entry<Integer, Double> doc : scoredResults.entrySet()) {
//                        System.out.println(doc.getKey() + " " + doc.getValue());
//                    }
//                }
//            }
//        }
        if (!Flags.isIsEvaluation()) {
            for (int i = 0; i < scoredResults.size(); i++) {
                TopDocuments doc = scoredResults.poll();
                System.out.println(doc.getDocId() + " " + doc.getScore());
            }
        } else struct.setDocumentEval(scoredResults);
        long end_time = System.currentTimeMillis();
        timeForQueryProcessing = end_time - start_time;
        if(!Flags.isIsEvaluation()){
            System.out.println("Query Processing took " + (double) timeForQueryProcessing + " m seconds.");
        }

    }



    public static long getTimeForQueryProcessing() {
        return timeForQueryProcessing;
    }

    static void loadLexicon(){
        try (BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(Paths.PATH_LEXICON_MERGED)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                TermDictionary currentTerm = new TermDictionary(parts[0],
                        Integer.parseInt(parts[1].trim()), Integer.parseInt(parts[2].trim()),
                        Integer.parseInt(parts[5].trim()), Integer.parseInt(parts[6].trim()),
                        Integer.parseInt(parts[3].trim()), Integer.parseInt(parts[7].trim()),
                        Double.parseDouble(parts[4].replace(',', '.')));

                lexicon.put(parts[0], currentTerm);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void loadDocumentIndex(){
        try (BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(Paths.PATH_DOCUMENT_INDEX_MERGED)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");

                documentIndex.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Integer> saveQueriesToCompare(){
        List<Integer> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(String.valueOf(Paths.PATH_EVALUATION_GT)))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (!list.contains(Integer.parseInt(parts[0]))){
                    list.add(Integer.parseInt(parts[0]));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static TreeMap<String, TermDictionary> getLexicon() {
        return lexicon;
    }

    public static TreeMap<Integer, Integer> getDocumentIndex() {
        return documentIndex;
    }
    public static List<Integer> getQueriesIds() {
        return queriesIds;
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

//    private void calculateTermUpperBounds(List<TermDictionary> termList, List<DocumentQP> docList){
//        Ranking ranking = new Ranking();
//        double scoreValue;
//        boolean first;
//
//        for (TermDictionary term : termList){
//            for (TermDictionary.Posting doc : term.getPostingList()){
//                first = true;
//                for (DocumentQP doc2 : docList){
//                    if (doc2.getDocId().equals(doc.getDocId())){
//                        scoreValue = ranking.computeTermUpperBound(term, doc2);
//                        if (first) {
//                            term.setTermUpperBound(scoreValue);
//                            first = false;
//                        }
//                        if (scoreValue > term.getTermUpperBound()) term.setTermUpperBound(scoreValue);
//                    }
//                }
//            }
//        }

    //}

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