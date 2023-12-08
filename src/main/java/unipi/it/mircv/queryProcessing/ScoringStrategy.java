package unipi.it.mircv.queryProcessing;

import unipi.it.mircv.common.Flags;

import java.util.*;

public class ScoringStrategy {
    HashMap<Integer, Double> scoredDocs = new HashMap<>();
    LinkedHashMap<Integer, Double> scoredDocsSorted = new LinkedHashMap<>();
    public List<DocumentQP> scoringStrategy(List<TermDictionary> termList, List<DocumentQP> relevantDocs, String[] queryParts, int k){
        if (Flags.isIsDAAT_flag()) return DAAT(termList, relevantDocs, queryParts, k);
        //else maxScore(relevantDocs);
        return null;
    }
    private List<DocumentQP> DAAT(List<TermDictionary> termList, List<DocumentQP> relevantDocs, String[] queryParts, int k){

        List<DocumentQP> topResults = new ArrayList<>();

        // transverse the list of documents in the Posting List by smallest docId
        double score = 0;
        Scoring scoring = new Scoring();

        DocumentQP currentDoc;
        int i = 0;

        while (true){
            score = 0;
            currentDoc = relevantDocs.get(i);

            for (TermDictionary dictionaryTerm : termList) {
                for (TermDictionary.Posting pL : dictionaryTerm.getPostingList()){
                    if (currentDoc.getDocId().equals(pL.getDocId())){
                        score += scoring.computeScoring(dictionaryTerm, relevantDocs.get(i));
                    }
                }
            }
            currentDoc.setScore(score);

            if (topResults.size() < k && topResults.size() < relevantDocs.size()) {
                topResults.add(currentDoc);
                topResults.sort(Collections.reverseOrder());
            } else {
                //System.out.println("Is it bigger? " + currentDoc.getScore() + " " + topResults.get(0).getScore());
                if (currentDoc.getScore() > topResults.get(0).getScore()) {
                    //System.out.println("It was");
                    topResults.remove(0);
                    topResults.add(currentDoc);
                    topResults.sort(Collections.reverseOrder());
                }
            }
//            for (DocumentQP doc : topResults) System.out.print(doc.getDocId() + " " + doc.getScore() + " ");
//            System.out.println();

            if (i < relevantDocs.size()-1) i++;
            else break;
        }
        System.out.println("Finished ");
        return topResults;

            // transverse postingList docIds to

        // maybe the termList only has the terms in the query so I dont need to seach for it
        // TODO: make the termList reset everytime a new query is entered


    }


    private PriorityQueue<DocumentQP> insertResults(PriorityQueue<DocumentQP> topResults){


        return topResults;
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

    private TermDictionary findTerm(List<TermDictionary> list, String termToCheck) {
        for (TermDictionary term : list) {
            if (term.getTerm().equals(termToCheck)) {
                return term;
            }
        }
        return null;
    }

//    private TermQP findTerm(List<TermQP> list, String termToCheck) {
//        for (TermQP term : list) {
//            if (term.getTerm().equals(termToCheck)) {
//                return term;
//            }
//        }
//        return null;
//    }



    //DAAT
//        try {
//            Scoring scoring = new Scoring();
//            double scores;
//            for (DocumentQP doc : relevantDocs){
//                scores = 0.0;
//                for (String term : queryParts) {
//                    TermQP singleTerm = findTerm(termList, term);
//                    if (singleTerm.getDocumentsWithTerm().contains(doc)) scores += scoring.computeScoring(singleTerm, doc);
//                    System.out.println("Score for DocId " + doc.getDocId() + ": "+ scores + " with term \"" + term + "\"");
//                }
//                if(scores < 1) scoredDocs.put(doc.getDocId(), scores);
//            }
//
//            // sorting results
//            scoredDocsSorted = getScoredDocsSorted(scoredDocs);
//            System.out.println(scoredDocsSorted);
//        }catch (NullPointerException e) {
//            throw new IllegalArgumentException();
//        }
    private void maxScore(Set<Integer> relevantDocs){
        return;
    }
}
