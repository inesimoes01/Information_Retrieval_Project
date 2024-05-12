package unipi.it.mircv.queryProcessing;

import org.apache.lucene.index.Term;
import unipi.it.mircv.common.Flags;
import unipi.it.mircv.indexing.dataStructures.Doc;
import unipi.it.mircv.queryProcessing.dataStructures.DocumentQP;
import unipi.it.mircv.queryProcessing.dataStructures.Posting;
import unipi.it.mircv.queryProcessing.dataStructures.PostingList;
import unipi.it.mircv.queryProcessing.dataStructures.TermDictionary;

import java.io.IOException;
import java.util.*;

public class ScoringStrategy {

    public HashMap<Integer, Double> scoringStrategy(List<TermDictionary> termList, List<PostingList> relevantPL, int k) throws IOException {
        OutputResultsReader.saveTotalNumberDocs();
        if (Flags.isIsDAAT_flag()) return DAAT(termList, relevantPL, k);
        else return maxScore(termList, relevantPL, k);
    }

    private HashMap<Integer, Double> DAAT(List<TermDictionary> termList, List<PostingList> relevantPL, int k) throws IOException {

        HashMap<Integer, Double> topResults = new HashMap<>();

        // transverse the list of documents in the Posting List by smallest docId
        double documentUpperBound;
        Ranking ranking = new Ranking();

        List<DocumentQP> docsWithTerm;



        int currentDocId;
        int i = 0;

        while (true){
            documentUpperBound = 0;
            DocumentQP currentDoc;
            TermDictionary currentTerm;

            for (PostingList pl : relevantPL){
                for (Posting p : pl.getPl()){

                    for (TermDictionary term: termList){
                        if (term.getTerm().equals(pl.getTerm())){
                            currentTerm = term;
                            docsWithTerm = currentTerm.getDocumentsWithTerm();
                            for (DocumentQP doc: docsWithTerm){
                                if (doc.getDocId().equals(p.getDocId())){
                                    currentDoc = doc;
                                }
                            }
                            documentUpperBound += ranking.computeRanking_QP(term, currentDoc.getDocId(), currentDoc.getLength());
                            break;
                        }
                    }

                    OutputResultsReader.


                }
            }

            saveTopKDocuments(relevantPL.size(), k, topResults, currentDocId, documentUpperBound);
            //System.out.println("Saved files");
            if (i < relevantDocs.size()-1) i++;
            else break;
        }
        return topResults;

        // TODO: make the termList reset everytime a new query is entered
    }

    private HashMap<Integer, Double> maxScore(List<TermDictionary> termList, List<PostingList> relevantPL, int k) throws IOException {
        List<DocumentQP> topResults = new ArrayList<>();
        Ranking ranking = new Ranking();
        DocumentQP currentDoc;
        int i = 0;
        double score;
        double maxScoreNonEssential = 0;
        double threshold = 0;

        // order termList by termUpperBound
        termList.sort(Comparator.comparingDouble(TermDictionary::getTermUpperBound));
        Collections.reverse(termList);
        //for (TermDictionary term : termList) System.out.println("Term " + term.getTerm() + " with UpperBound " + term.getTermUpperBound());

        // separate terms in essential and nonEssential
        List<TermDictionary> essentialTerms = new ArrayList<>();
        List<TermDictionary> nonEssentialTerms = new ArrayList<>();
        separateNonEssentialPostingList(termList, essentialTerms, nonEssentialTerms, threshold);

        // get the sum of all TermUpperBounds of nonEssentialTerms
        for (TermDictionary term : nonEssentialTerms) {
            System.out.println(term.getTerm());
            maxScoreNonEssential += term.getTermUpperBound();
        }

//        while (true){
//            score = 0;
//            currentDoc = getMinDocId(essentialTerms);

//            // ESSENTIAL POSTING LIST
//            // find all the terms that appear in the query and in the document and add the ranking
//            for (TermDictionary dictionaryTerm : essentialTerms) {
//                for (Posting pL : dictionaryTerm.getPostingList()){
//                    if (currentDoc.getDocId().equals(pL.getDocId())){
//                        score += ranking.computeRanking_QP(dictionaryTerm, currentDoc);
//                    }
//                }
//            }
//
//            // save score and update top k documents
//            // if there is already k documents, update threshold to the minimum score
//            // and update the essential posting list
//            currentDoc.setScore(score);
//            if (topResults.size() < k) {
//                saveTopKDocuments(relevantDocs, k, topResults, currentDoc);
//            }
//
//            // check if we should score the rest of the terms
//            if (score + maxScoreNonEssential < threshold) {
//                if (i < relevantDocs.size()-1) i++;
//                else break;
//                continue;
//            }
//
//            // NON ESSENTIAL POSTING LIST
//            for (TermDictionary dictionaryTerm : nonEssentialTerms) {
//                for (Posting pL : dictionaryTerm.getPostingList()){
//                    if (currentDoc.getDocId().equals(pL.getDocId())){
//                        score += ranking.computeRanking_QP(dictionaryTerm, currentDoc);
//                    }
//                }
//            }
//
//            // rewrite score and update top k
//            currentDoc.setScore(score);
//            saveTopKDocuments(relevantDocs, k, topResults, currentDoc);
//
//            // update threshold and update nonEssentialPostingList
                //TODO only update threshold if the top k is already full
//            threshold = topResults.get(topResults.size()-1).getScore();
//            separateNonEssentialPostingList(termList, essentialTerms, nonEssentialTerms, threshold);
//            for (TermDictionary term : nonEssentialTerms) maxScoreNonEssential += term.getTermUpperBound();
//
//            if (i < relevantDocs.size()-1) i++;
//            else break;
//        }
        return topResults;
    }

//    private static ArrayList<Map.Entry<PostingList, Double>> sortPostingListsByTermUpperBound(List<DocumentQP> queryPostings, List<TermDictionary> termList){
//        PriorityQueue<Map.Entry<PostingList, Double>> sortedPostingLists = new PriorityQueue<>(queryPostings.size(), Map.Entry.comparingByValue());
//
//        for (PostingList postingList : queryPostings) {
//            // retrieve document upper bound
//            termUpperBound = termList.
//
//            sortedPostingLists.add(new AbstractMap.SimpleEntry<>(postingList, termUpperBound));
//        }
//
//        return new ArrayList<>(sortedPostingLists.stream().toList());
//    }

    private void saveTopKDocuments(Integer totalDocs, int k, List<DocumentQP> topResults, DocumentQP currentDoc) {
        //if (!topResults.contains(currentDoc)){
            if (topResults.size() < k && topResults.size() < totalDocs) {
                topResults.add(currentDoc);
                topResults.sort(Collections.reverseOrder());
            } else {
                if (currentDoc.getScore() > topResults.get(0).getScore()) {
                    topResults.remove(0);
                    topResults.add(currentDoc);
                    topResults.sort(Collections.reverseOrder());
                }
            }
        //}

    }

    private DocumentQP getMinDocId(List<DocumentQP> docs){
        DocumentQP minDoc = docs.get(0);
        for (DocumentQP doc : docs){
            if (doc.getDocId() < minDoc.getDocId()){
                minDoc = doc;
            }
        }
        return minDoc;
    }


    // save only the documents that match all the query terms
    public static List<PostingList> conjunctiveProcessing(List<TermDictionary> termList, String[] query, List<String> termNonExistent){
        List<DocumentQP> docsWithTerms = new ArrayList<>();

        for (String term : query) {
            // fills the term list
            TermDictionary termInstance = OutputResultsReader.fillTermDictionary(termList, term);

            // if query term does not exist in the collection, return null
            if (termInstance == null){
                System.out.println("Term \"" + term + "\" not found");
                termNonExistent.add(term);
                docsWithTerms.clear();
                return null;
            } else {
                docsWithTerms.addAll(termInstance.getDocumentsWithTerm());
            }
        }

        List<Integer> docIds = keepDuplicates(docsWithTerms);

        List<PostingList> plList = new ArrayList<>();
        List<Posting> pl = new ArrayList<>();

        for (TermDictionary t : termList) {
            pl.clear();
            for (Integer id : docIds) {
                System.out.println("this is the docid with term " + id + " " + t.getTerm());
                for (Posting p : t.getPostingList()) {
                    if (id.equals(p.getDocId())) {
                        pl.add(p);
                    }
                }
            }
            plList.add(new PostingList(t.getTerm(), pl, t.getTermUpperBound()));
        }

        return plList;
    }


    public static List<PostingList> disjunctiveProcessing(List<TermDictionary> termList, String[] query, List<String> termNonExistent){
        List<PostingList> pl = new ArrayList<>();

        for (String term : query) {
            // fills the term list
            TermDictionary termInstance = OutputResultsReader.fillTermDictionary(termList, term);

            // if query term does not exist in the collection, return null
            if (termInstance == null){
                System.out.println("Term \"" + term + "\" not found");
                termNonExistent.add(term);
            } else {
                // keeps all documents
                pl.add(new PostingList(termInstance.getTerm(), termInstance.getPostingList(), termInstance.getTermUpperBound()));
            }
        }


        return pl;
    }

    private static List<DocumentQP> removeDuplicates(List<DocumentQP> list) {
        List<DocumentQP> uniqueList = new ArrayList<>();
        boolean exist;
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


//    private static DocumentQP minDocId (List<DocumentQP> documentList){
//        DocumentQP min = new DocumentQP();
//        min.setDocId(Integer.MAX_VALUE);
//        for (DocumentQP doc : documentList){
//            if (min.getDocId() > doc.getDocId()) {
//                min = doc;
//            }
//        }
//        return min;
//    }

    private static List<Integer> keepDuplicates(List<DocumentQP> docsWithTerms){
        List<Integer> docId = new ArrayList<>();
        for (DocumentQP doc : docsWithTerms) {
            docId.add(doc.getDocId());
        }
        Map<Integer, Integer> countMap = new HashMap<>();
        for (int num : docId) {
            countMap.put(num, countMap.getOrDefault(num, 0) + 1);
        }

        // Create a new list to store the duplicated values
        List<Integer> duplicates = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : countMap.entrySet()) {
            if (entry.getValue() > 1) { // If the count is greater than 1, it's a duplicate
                duplicates.add(entry.getKey());
            }
        }
        System.out.println("Duplicated values: " + duplicates);
        return duplicates;
    }

    private static void separateNonEssentialPostingList(List<TermDictionary> allTerms, List<TermDictionary> essentialTerms, List<TermDictionary> nonEssentialTerms, Double threshold){
        essentialTerms.clear();
        nonEssentialTerms.clear();
        for (TermDictionary term : allTerms) {
            if (term.getTermUpperBound() < threshold){
                nonEssentialTerms.add(term);
            } else {
                essentialTerms.add(term);
            }
        }

    }
}





//    private TermDictionary findTerm(List<TermDictionary> list, String termToCheck) {
//        for (TermDictionary term : list) {
//            if (term.getTerm().equals(termToCheck)) {
//                return term;
//            }
//        }
//        return null;
//    }

    //private static LinkedHashMap<Integer, Double> getScoredDocsSorted(HashMap<Integer, Double> scoredDocs) {
//        List<Map.Entry<Integer, Double>> scoredDocsSortedList = new ArrayList<>(scoredDocs.entrySet());
//        scoredDocsSortedList.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
//
//        LinkedHashMap<Integer, Double> scoredDocsSorted = new LinkedHashMap<>();
//        for (Map.Entry<Integer, Double> entry : scoredDocsSortedList) {
//            scoredDocsSorted.put(entry.getKey(), entry.getValue());
//        }
//        return scoredDocsSorted;
//    }

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

