package unipi.it.mircv.queryProcessing;

import unipi.it.mircv.common.Flags;

import java.util.*;

public class ScoringStrategy {
    HashMap<Integer, Double> scoredDocs = new HashMap<>();
    LinkedHashMap<Integer, Double> scoredDocsSorted = new LinkedHashMap<>();
    public void scoringStrategy(List<TermQP> termList, List<DocumentQP> relevantDocs, String[] queryParts){
        if (Flags.isIsDAAT_flag()) DAAT(termList, relevantDocs, queryParts);
        //else maxScore(relevantDocs);
    }
    private void DAAT(List<TermQP> termList, List<DocumentQP> relevantDocs, String[] queryParts){
        try {
            Scoring scoring = new Scoring();
            double scores;
            for (DocumentQP doc : relevantDocs){
                scores = 0.0;
                for (String term : queryParts) {
                    TermQP singleTerm = findTerm(termList, term);
                    if (singleTerm.getDocumentsWithTerm().contains(doc)) scores += scoring.computeScoring(singleTerm, doc);
                    System.out.println("Score for DocId " + doc.getDocId() + ": "+ scores + " with term \"" + term + "\"");
                }
                if(scores < 1) scoredDocs.put(doc.getDocId(), scores);
            }

            // sorting results
            scoredDocsSorted = getScoredDocsSorted(scoredDocs);
            System.out.println(scoredDocsSorted);
        }catch (NullPointerException e) {
            throw new IllegalArgumentException();
        }

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

    private TermQP findTerm(List<TermQP> list, String termToCheck) {
        for (TermQP term : list) {
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

    private void maxScore(Set<Integer> relevantDocs){
        return;
    }
}
