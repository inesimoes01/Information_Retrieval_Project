package unipi.it.mircv.queryProcessing;

import unipi.it.mircv.common.Flags;
import unipi.it.mircv.indexing.dataStructures.Doc;
import unipi.it.mircv.indexing.dataStructures.Posting;
import unipi.it.mircv.queryProcessing.dataStructures.DocumentQP;
import unipi.it.mircv.queryProcessing.dataStructures.PostingList;
import unipi.it.mircv.queryProcessing.dataStructures.TermDictionary;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class ScoringStrategy {

    public static Map<Integer, Double>  scoringStrategy(List<TermDictionary> termList, List<Integer> relevantDocs, int k) throws IOException {


        Map<Integer, Double> topKResults;

        if (Flags.isIsDAAT_flag()) topKResults = DAAT(termList, relevantDocs, k);
        else topKResults = maxScore(termList, relevantDocs, k);

        return topKResults;
    }

    private static Map<Integer, Double> DAAT(List<TermDictionary> termList, List<Integer> relevantDocs, int k) throws IOException {
        Map<Integer, Double> topResults = new TreeMap<>();

        double documentUpperBound;
        Ranking ranking = new Ranking();

        Integer currentDoc;
        int i = 0;

        while (true){
            documentUpperBound = 0;
            currentDoc = relevantDocs.get(i);

            for (TermDictionary dictionaryTerm : termList) {
                if (dictionaryTerm.getPostingList().containsKey(currentDoc)){
                    documentUpperBound += ranking.computeRanking_QP(dictionaryTerm, relevantDocs.get(i));
                }
            }

            topResults = saveTopKDocuments(relevantDocs, k, topResults, currentDoc, documentUpperBound);

            if (i < relevantDocs.size()-1) i++;
            else break;
        }

        return topResults;

        // TODO: make the termList reset everytime a new query is entered
    }

    private static Map<Integer, Double> maxScore(List<TermDictionary> termList, List<Integer> relevantDocs, int k) throws IOException {
        Map<Integer, Double>  topResults = new TreeMap<>();
        Ranking ranking = new Ranking();
        Integer currentDoc;

        int i = 0;
        double score;
        double maxScoreNonEssential = 0;
        double threshold = 4;

        // order termList by termUpperBound
        termList.sort(Comparator.comparingDouble(TermDictionary::getTermUpperBound));
        Collections.reverse(termList);
        //for (TermDictionary term : termList) System.out.println("Term " + term.getTerm() + " with UpperBound " + term.getTermUpperBound());

        // separate terms in essential and nonEssential
        List<TermDictionary> essentialTerms = new ArrayList<>();
        List<TermDictionary> nonEssentialTerms = new ArrayList<>();
        separateNonEssentialPostingList(termList, essentialTerms, nonEssentialTerms, threshold);

        // if threshold is too high, adjust to the highest of the Term Upper Bounds
        if (essentialTerms.isEmpty()) {
            threshold = nonEssentialTerms.get(0).getTermUpperBound();
            separateNonEssentialPostingList(termList, essentialTerms,nonEssentialTerms,threshold);
        }

        // get the sum of all TermUpperBounds of nonEssentialTerms
        for (TermDictionary term : nonEssentialTerms) maxScoreNonEssential += term.getTermUpperBound();


        while (true){
            score = 0;
            currentDoc = relevantDocs.get(i);

            // ESSENTIAL POSTING LIST
            for (TermDictionary dictionaryTerm : essentialTerms) {
                for (Map.Entry<Integer, Integer> pL : dictionaryTerm.getPostingList().entrySet()){
                    if (currentDoc.equals(pL.getKey())){
                        score += ranking.computeRanking_QP(dictionaryTerm, relevantDocs.get(i));
                    }
                }
            }

            // update top k
            if (topResults.size() < k) {
                topResults = saveTopKDocuments(relevantDocs, k, topResults, currentDoc, score);
            }

            // check if we should score the rest of the terms
            if (score + maxScoreNonEssential < threshold) {
                if (i < relevantDocs.size()-1) i++;
                else break;
                continue;
            }


            // NON ESSENTIAL POSTING LIST
            for (TermDictionary dictionaryTerm : nonEssentialTerms) {
                for (Map.Entry<Integer, Integer> pL : dictionaryTerm.getPostingList().entrySet()){
                    if (currentDoc.equals(pL.getKey())){
                        score += ranking.computeRanking_QP(dictionaryTerm, relevantDocs.get(i));
                    }
                }
            }

            // update top k
            topResults = saveTopKDocuments(relevantDocs, k, topResults, currentDoc, score);

            // update threshold
            // TODO check if right
            List<Map.Entry<Integer, Double>> entryList = new ArrayList<>(topResults.entrySet());
            Double lastValue = entryList.getLast().getValue();
            Integer lastKey = entryList.getLast().getKey();

            threshold = lastValue;

            // update nonEssentialPostingList
            separateNonEssentialPostingList(termList, essentialTerms, nonEssentialTerms, threshold);
            for (TermDictionary term : nonEssentialTerms) maxScoreNonEssential += term.getTermUpperBound();

            if (i < relevantDocs.size()-1) i++;
            else break;
        }
        return topResults;
    }

    private static Map<Integer, Double> saveTopKDocuments(List<Integer> relevantDocs, int k, Map<Integer, Double> topResults, Integer currentDoc, Double score) {
        if(topResults.get(currentDoc) == null) {
            if (topResults.size() < k && topResults.size() < relevantDocs.size()) {
                topResults.put(currentDoc, score);
            } else {
                List<Map.Entry<Integer, Double>> entryList = new ArrayList<>(topResults.entrySet());
                Double lastValue = entryList.getLast().getValue();
                Integer lastKey = entryList.getLast().getKey();

                if (score > lastValue) {
                    topResults.remove(lastKey);
                    topResults.put(currentDoc, score);
                }
            }
        }
        Map<Integer, Double> sorted = sortByValue(topResults);
        return sorted;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }



    private static void separateNonEssentialPostingList(List<TermDictionary> allTerms, List<TermDictionary> essentialTerms, List<TermDictionary> nonEssentialTerms, Double threshold){
        essentialTerms.clear();
        nonEssentialTerms.clear();
        for (TermDictionary term : allTerms) {
            if (term.getTermUpperBound() < threshold){
                nonEssentialTerms.add(term);
            } else {
                //System.out.println("This is a essential term " + term.getTerm());
                essentialTerms.add(term);
            }
        }

    }
}
