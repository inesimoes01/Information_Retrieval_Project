package unipi.it.mircv.queryProcessing;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.DoubleValues;
import unipi.it.mircv.common.Flags;
import unipi.it.mircv.common.Paths;
import unipi.it.mircv.common.dataStructures.*;

import unipi.it.mircv.queryProcessing.dataStructures.PostingList;

import javax.swing.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.PseudoColumnUsage;
import java.util.*;

public class ScoringStrategy {
    public static PriorityQueue<TopDocuments> scoringStrategy(String[] query, int k) throws IOException {
        PriorityQueue<TopDocuments> topKResults = new PriorityQueue<>(new TopDocumentsComparator());

        if (Flags.isIsDAAT_flag() && !Flags.isIsConjunctive_flag()) topKResults = DAAT_Disjunctive(query, k);
        else if (Flags.isIsDAAT_flag() && Flags.isIsConjunctive_flag()) topKResults = DAAT_Conjunctive(query, k);
        else if (!Flags.isIsDAAT_flag()) topKResults = MaxScore(query, k);

        return topKResults;
    }

    private static PriorityQueue<TopDocuments> MaxScore(String[] query, int k) throws IOException {
        ArrayList<TermDictionary> termDictionaryList = new ArrayList<>();
        ArrayList<PostingList> postingLists = new ArrayList<>();
        getAndSortPostingListsByTUB(query, termDictionaryList, postingLists);

        PriorityQueue<TopDocuments> topKResults = new PriorityQueue<>(new TopDocumentsComparator());

        ArrayList<Double> cummulativeUpperBounds = computeTermUpperBounds(termDictionaryList);
        int pivot = 0;
        int next;
        double threshold = 0;
        int currentDocId = 0;

        while (pivot < postingLists.size() && currentDocId != -1) {
            double score = 0.0;
            next = OutputResultsReader.getnTotalDocuments();
            currentDocId = getMinDocID(postingLists);
            int docLength = QueryProcessing.getDocumentIndex().get(currentDocId);


            // DAAT for the essential posting lists
            for (int i = pivot; i < postingLists.size(); i++) {
                PostingList pl = postingLists.get(i);
                Posting p = pl.getCurrentPosting();

                // find term in the term dictionary
                TermDictionary termDictionary = findTermInList(termDictionaryList, pl);

                if (p != null && p.getDocId() == currentDocId) {
                    score += Ranking.computeRanking_QP(termDictionary, p.getFreq(), docLength);
                    p = pl.nextPosting();
                }

                // move to next posting in the list; if the posting list is exhausted, remove it from the list
                if (pl.nextPosting() == null) {
                    postingLists.remove(i);
                    i--;
                }
                if (p != null && p.getDocId() < next) {
                   next = p.getDocId();
                }
            }

            // non-essential lists
            for (int i = pivot - 1; i >= 0; i--) {
                if (score + cummulativeUpperBounds.get(i) <= threshold) {
                    break;
                }

                PostingList pl = postingLists.get(i);
                Posting p = pl.nextPosting();

                // find term in the term dictionary
                TermDictionary termDictionary = findTermInList(termDictionaryList, pl);

                if (p != null && p.getDocId() == currentDocId) {
                    score += Ranking.computeRanking_QP(termDictionary, p.getFreq(), docLength);
                }
            }

            // update the threshold and pivot
            if (topKResults.size() < k || score > topKResults.peek().getScore()) {
                topKResults.add(new TopDocuments(currentDocId, score));

                if (topKResults.size() > k) {
                    topKResults.poll();
                }

                threshold = topKResults.peek().getScore();
                while (pivot < postingLists.size() && cummulativeUpperBounds.get(pivot) <= threshold) {
                    pivot++;
                }
            }


            if (next == OutputResultsReader.getnTotalDocuments())
                currentDocId = -1;
        }
        return topKResults;

    }

    private static PriorityQueue<TopDocuments> DAAT_Conjunctive(String[] query, int k) throws IOException {
        ArrayList<TermDictionary> termDictionaryList = new ArrayList<>();
        ArrayList<PostingList> postingLists = new ArrayList<>();
        getAndSortPostingListsBySize(query, termDictionaryList, postingLists);
        PriorityQueue<TopDocuments> topKResults = new PriorityQueue<>(new TopDocumentsComparator());

        Posting currPost = postingLists.get(0).getCurrentPosting();

        while(currPost != null){
            double score = 0.0;
            int currDocId = currPost.getDocId();

            if (checkPL(postingLists, currDocId)){

                int docLength = QueryProcessing.getDocumentIndex().get(currDocId);

                for (int i = 0; i < postingLists.size(); i++) {
                    PostingList pl = postingLists.get(i);
                    Posting p = pl.getCurrentPosting();

                    // find term in the term dictionary
                    TermDictionary termDictionary = findTermInList(termDictionaryList, pl);

                    if (p.getDocId() == currDocId) {
                        score += Ranking.computeRanking_QP(termDictionary, p.getFreq(), docLength);

                        // move to next posting in the list; if the posting list is exhausted, remove it from the list
//                        if (pl.nextPosting() == null) {
//                            postingLists.remove(i);
//                            i--;
//                        }
                    }
                }

                // check if top k results are not full
                if (topKResults.size() < k) {
                    topKResults.add(new TopDocuments(currDocId, score));
                }
                // check if score is higher than the smallest score already in the top k
                else if (score > topKResults.peek().getScore() && topKResults.size() == k) {
                    topKResults.poll();
                    topKResults.add(new TopDocuments(currDocId, score));
                }

            }

            currPost = postingLists.get(0).nextPosting();
        }

        return topKResults;

    }

    private static PriorityQueue<TopDocuments> DAAT_Disjunctive(String[] query, int k) throws IOException {
        ArrayList<TermDictionary> termDictionaryList = new ArrayList<>();
        ArrayList<PostingList> postingLists = new ArrayList<>();
        getPostingLists(query, termDictionaryList, postingLists);
        //Map<Integer, Double> topKResults = new HashMap<>();

        PriorityQueue<TopDocuments> topKResults = new PriorityQueue<>(new TopDocumentsComparator());

        int minDocID;
        double lastScore = 0;

        while(!postingLists.isEmpty()){
            double score = 0.0;

            minDocID = getMinDocID(postingLists);
            int docLength = QueryProcessing.getDocumentIndex().get(minDocID);

            for (int i = 0; i < postingLists.size(); i++) {
                PostingList pl = postingLists.get(i);
                Posting p = pl.getCurrentPosting();

                // find term in the term dictionary
                TermDictionary termDictionary = findTermInList(termDictionaryList, pl);

                if (p.getDocId() == minDocID) {
                    score += Ranking.computeRanking_QP(termDictionary, p.getFreq(), docLength);

                    // move to next posting in the list; if the posting list is exhausted, remove it from the list
                    if (pl.nextPosting() == null) {
                        postingLists.remove(i);
                        i--;
                    }
                }
            }

            // check if top k results are not full
            if (topKResults.size() < k) {
                topKResults.add(new TopDocuments(minDocID, score));
            }
            // check if score is higher than the smallest score already in the top k
            else if (score > lastScore && topKResults.size() == k) {
                topKResults.poll();
                topKResults.add(new TopDocuments(minDocID, score));
            }

            assert topKResults.peek() != null;
            lastScore = topKResults.peek().getScore();
        }

        return topKResults;
    }

    public static boolean checkPL(ArrayList<PostingList> postingLists, int docId){
        for (PostingList pl : postingLists){
            int index = -1;
            Posting key = new Posting(docId, 0);

            index = Collections.binarySearch(pl.getPl(), key, new PostingComparator());
            if (index <= 0) return false;
        }
        return true;
    }


    private static void getAndSortPostingListsBySize(String[] query, ArrayList<TermDictionary> termDictionaryList, ArrayList<PostingList> postingLists) throws FileNotFoundException {
        try (RandomAccessFile invertedIndexFile = new RandomAccessFile(Paths.PATH_INVERTED_INDEX_MERGED, "r")) {
            for (String term : query) {
                TermDictionary currentTermDictionary = QueryProcessing.getLexicon().get(term);
                if (currentTermDictionary == null) return;

                termDictionaryList.add(currentTermDictionary);
                PostingList pl = OutputResultsReader.searchTermInInvertedIndex(invertedIndexFile, currentTermDictionary);
                Collections.sort(pl.getPl(), new PostingComparator());
                postingLists.add(pl);
            }

            // sort term dictionary
            Collections.sort(postingLists, new Comparator<PostingList>() {
                @Override
                public int compare(PostingList o1, PostingList o2) {
                    return Double.compare(o1.getPl().size(), o2.getPl().size());
                }
            });


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static ArrayList<Double> computeTermUpperBounds(ArrayList<TermDictionary> termDictionaryList){
        ArrayList<Double> upperBoundsList = new ArrayList<>();
        double cumulativeUpperBound = 0.0;

        for (TermDictionary term : termDictionaryList) {
            cumulativeUpperBound += term.getTermUpperBound();
            upperBoundsList.add(cumulativeUpperBound);
        }
        return upperBoundsList;
    }

    private static TermDictionary findTermInList(ArrayList<TermDictionary> termDictionaryArrayList, PostingList pl){
        TermDictionary termDictionary = new TermDictionary();
        for (TermDictionary dictionary : termDictionaryArrayList) {
            String term = dictionary.getTerm();
            if (term.equals(pl.getTerm())) termDictionary = dictionary;
        }
        return termDictionary;
    }

    private static void getAndSortPostingListsByTUB(String[] query, ArrayList<TermDictionary> termDictionaryList, ArrayList<PostingList> postingLists){
        try (RandomAccessFile invertedIndexFile = new RandomAccessFile(Paths.PATH_INVERTED_INDEX_MERGED, "r")) {
            for (String term : query) {
                TermDictionary currentTermDictionary = QueryProcessing.getLexicon().get(term);
                if (currentTermDictionary == null) return;

                termDictionaryList.add(currentTermDictionary);

            }

            // sort term dictionary
            Collections.sort(termDictionaryList, new Comparator<TermDictionary>() {
                @Override
                public int compare(TermDictionary o1, TermDictionary o2) {
                    return Double.compare(o1.getTermUpperBound(), o2.getTermUpperBound());
                }

            });

            // get posting lists
            for (TermDictionary elem : termDictionaryList) {
                postingLists.add(OutputResultsReader.searchTermInInvertedIndex(invertedIndexFile, elem));
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
    private static int getMinDocID(ArrayList<PostingList> pl){
        int minDocId = Integer.MAX_VALUE;
        for (PostingList postingList : pl) {
            Posting p = postingList.getCurrentPosting();
            if (p.getDocId() < minDocId) {
                minDocId = p.getDocId();
            }
        }
        return minDocId;
    }

    private static void getPostingLists(String[] query, ArrayList<TermDictionary> termDictionaryList, ArrayList<PostingList> postingLists) throws FileNotFoundException {
        try (RandomAccessFile invertedIndexFile = new RandomAccessFile(Paths.PATH_INVERTED_INDEX_MERGED, "r")) {
            for (String term : query) {
                TermDictionary currentTermDictionary = QueryProcessing.getLexicon().get(term);
                if (currentTermDictionary == null) return;

                termDictionaryList.add(currentTermDictionary);

                postingLists.add(OutputResultsReader.searchTermInInvertedIndex(invertedIndexFile, currentTermDictionary));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    private static Map<Integer, Double> DAAT(List<TermDictionary> termList, List<Integer> relevantDocs, int k) throws IOException {
//        Map<Integer, Double> topResults = new TreeMap<>();
//
//        double documentUpperBound;
//        Ranking ranking = new Ranking();
//
//        Integer currentDoc;
//        int i = 0;
//
//        while (true){
//            documentUpperBound = 0;
//            currentDoc = relevantDocs.get(i);
//
//            for (TermDictionary dictionaryTerm : termList) {
//                if (dictionaryTerm.getPostingList().containsKey(currentDoc)){
//                    documentUpperBound += ranking.computeRanking_QP(dictionaryTerm, relevantDocs.get(i));
//                }
//            }
//
//            topResults = saveTopKDocuments(relevantDocs, k, topResults, currentDoc, documentUpperBound);
//
//            if (i < relevantDocs.size()-1) i++;
//            else break;
//        }
//
//        return topResults;
//
//        // TODO: make the termList reset everytime a new query is entered
//    }

//    private static Map<Integer, Double> maxScore(List<TermDictionary> termList, List<Integer> relevantDocs, int k) throws IOException {
//        Map<Integer, Double>  topResults = new TreeMap<>();
//        Ranking ranking = new Ranking();
//        Integer currentDoc;
//
//        int i = 0;
//        double score;
//        double maxScoreNonEssential = 0;
//        double threshold = 4;
//
//        // order termList by termUpperBound
//        termList.sort(Comparator.comparingDouble(TermDictionary::getTermUpperBound));
//        Collections.reverse(termList);
//        //for (TermDictionary term : termList) System.out.println("Term " + term.getTerm() + " with UpperBound " + term.getTermUpperBound());
//
//        // separate terms in essential and nonEssential
//        List<TermDictionary> essentialTerms = new ArrayList<>();
//        List<TermDictionary> nonEssentialTerms = new ArrayList<>();
//        separateNonEssentialPostingList(termList, essentialTerms, nonEssentialTerms, threshold);
//
//        // if threshold is too high, adjust to the highest of the Term Upper Bounds
//        if (essentialTerms.isEmpty()) {
//            threshold = nonEssentialTerms.get(0).getTermUpperBound();
//            separateNonEssentialPostingList(termList, essentialTerms,nonEssentialTerms,threshold);
//        }
//
//        // get the sum of all TermUpperBounds of nonEssentialTerms
//        for (TermDictionary term : nonEssentialTerms) maxScoreNonEssential += term.getTermUpperBound();
//
//
//        while (true){
//            score = 0;
//            currentDoc = relevantDocs.get(i);
//
//            // ESSENTIAL POSTING LIST
//            for (TermDictionary dictionaryTerm : essentialTerms) {
//                for (Map.Entry<Integer, Integer> pL : dictionaryTerm.getPostingList().entrySet()){
//                    if (currentDoc.equals(pL.getKey())){
//                        score += ranking.computeRanking_QP(dictionaryTerm, relevantDocs.get(i));
//                    }
//                }
//            }
//
//            // update top k
//            if (topResults.size() < k) {
//                topResults = saveTopKDocuments(relevantDocs, k, topResults, currentDoc, score);
//            }
//
//            // check if we should score the rest of the terms
//            if (score + maxScoreNonEssential < threshold) {
//                if (i < relevantDocs.size()-1) i++;
//                else break;
//                continue;
//            }
//
//
//            // NON ESSENTIAL POSTING LIST
//            for (TermDictionary dictionaryTerm : nonEssentialTerms) {
//                for (Map.Entry<Integer, Integer> pL : dictionaryTerm.getPostingList().entrySet()){
//                    if (currentDoc.equals(pL.getKey())){
//                        score += ranking.computeRanking_QP(dictionaryTerm, relevantDocs.get(i));
//                    }
//                }
//            }
//
//            // update top k
//            topResults = saveTopKDocuments(relevantDocs, k, topResults, currentDoc, score);
//
//            // update threshold
//            // TODO check if right
//            List<Map.Entry<Integer, Double>> entryList = new ArrayList<>(topResults.entrySet());
//            Double lastValue = entryList.getLast().getValue();
//            Integer lastKey = entryList.getLast().getKey();
//
//            threshold = lastValue;
//
//            // update nonEssentialPostingList
//            separateNonEssentialPostingList(termList, essentialTerms, nonEssentialTerms, threshold);
//            for (TermDictionary term : nonEssentialTerms) maxScoreNonEssential += term.getTermUpperBound();
//
//            if (i < relevantDocs.size()-1) i++;
//            else break;
//        }
//        return topResults;
//    }
//
//    private static Map<Integer, Double> saveTopKDocuments(List<Integer> relevantDocs, int k, Map<Integer, Double> topResults, Integer currentDoc, Double score) {
//        if(topResults.get(currentDoc) == null) {
//            if (topResults.size() < k && topResults.size() < relevantDocs.size()) {
//                topResults.put(currentDoc, score);
//            } else {
//                List<Map.Entry<Integer, Double>> entryList = new ArrayList<>(topResults.entrySet());
//                Double lastValue = entryList.getLast().getValue();
//                Integer lastKey = entryList.getLast().getKey();
//
//                if (score > lastValue) {
//                    topResults.remove(lastKey);
//                    topResults.put(currentDoc, score);
//                }
//            }
//        }
//        Map<Integer, Double> sorted = sortByValue(topResults);
//        return sorted;
//    }
//
//    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map, Double lastScore, Integer lastDocId) {
//        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
//        list.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));
//        lastDocId = (Integer) list.getLast().getKey();
//        lastScore = (Double) list.getLast().getValue();
//
//        Map<K, V> result = new LinkedHashMap<>();
//        for (Map.Entry<K, V> entry : list) {
//            result.put(entry.getKey(), entry.getValue());
//        }
//
//        return result;
//    }

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
