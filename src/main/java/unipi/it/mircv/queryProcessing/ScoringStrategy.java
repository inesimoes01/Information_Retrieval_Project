package unipi.it.mircv.queryProcessing;

import unipi.it.mircv.common.Flags;
import unipi.it.mircv.indexing.dataStructures.Doc;
import unipi.it.mircv.queryProcessing.dataStructures.DocumentQP;
import unipi.it.mircv.queryProcessing.dataStructures.PostingList;
import unipi.it.mircv.queryProcessing.dataStructures.TermDictionary;

import java.io.IOException;
import java.util.*;

public class ScoringStrategy {

    public List<DocumentQP> scoringStrategy(List<TermDictionary> termList, List<DocumentQP> relevantDocs, int k) throws IOException {
        OutputResultsReader.saveTotalNumberDocs();
        if (Flags.isIsDAAT_flag()) return DAAT(termList, relevantDocs, k);
        else return maxScore(termList, relevantDocs, k);
    }

    private List<DocumentQP> DAAT(List<TermDictionary> termList, List<DocumentQP> relevantDocs, int k) throws IOException {

        List<DocumentQP> topResults = new ArrayList<>();

        // transverse the list of documents in the Posting List by smallest docId
        double documentUpperBound;
        Ranking ranking = new Ranking();

        DocumentQP currentDoc;
        int i = 0;

        while (true){
            documentUpperBound = 0;
            currentDoc = relevantDocs.get(i);

            for (TermDictionary dictionaryTerm : termList) {
                for (PostingList pL : dictionaryTerm.getPostingList()){
                    if (currentDoc.getDocId().equals(pL.getDocId())){
                        documentUpperBound += ranking.computeRanking_QP(dictionaryTerm, relevantDocs.get(i));

                    }
                }
            }
            currentDoc.setScore(documentUpperBound);

            saveTopKDocuments(relevantDocs, k, topResults, currentDoc);
            //System.out.println("Saved files");
            if (i < relevantDocs.size()-1) i++;
            else break;
        }
        return topResults;

        // TODO: make the termList reset everytime a new query is entered
    }

    private List<DocumentQP> maxScore(List<TermDictionary> termList, List<DocumentQP> relevantDocs, int k) throws IOException {
        List<DocumentQP> topResults = new ArrayList<>();
        Ranking ranking = new Ranking();
        DocumentQP currentDoc;
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
                for (PostingList pL : dictionaryTerm.getPostingList()){
                    if (currentDoc.getDocId().equals(pL.getDocId())){
                        score += ranking.computeRanking_QP(dictionaryTerm, currentDoc);
                    }
                }
            }

            // save score
            currentDoc.setScore(score);
            // update top k
            if (topResults.size() < k) {
                saveTopKDocuments(relevantDocs, k, topResults, currentDoc);
            }

            // check if we should score the rest of the terms
            if (score + maxScoreNonEssential < threshold) {
                if (i < relevantDocs.size()-1) i++;
                else break;
                continue;
            }


            // NON ESSENTIAL POSTING LIST
            for (TermDictionary dictionaryTerm : nonEssentialTerms) {
                for (PostingList pL : dictionaryTerm.getPostingList()){
                    if (currentDoc.getDocId().equals(pL.getDocId())){
                        score += ranking.computeRanking_QP(dictionaryTerm, currentDoc);
                    }
                }
            }

            // rewrite score
            currentDoc.setScore(score);

            // update top k
            saveTopKDocuments(relevantDocs, k, topResults, currentDoc);

            // update threshold
            // TODO check if right
            threshold = topResults.get(topResults.size()-1).getScore();

            // update nonEssentialPostingList
            separateNonEssentialPostingList(termList, essentialTerms, nonEssentialTerms, threshold);
            for (TermDictionary term : nonEssentialTerms) maxScoreNonEssential += term.getTermUpperBound();

            if (i < relevantDocs.size()-1) i++;
            else break;
        }
        return topResults;
    }

    private void saveTopKDocuments(List<DocumentQP> relevantDocs, int k, List<DocumentQP> topResults, DocumentQP currentDoc) {
        List<Integer> docIdsFinal = new ArrayList<>();
        for (DocumentQP doc : topResults){
            docIdsFinal.add(doc.getDocId());
        }

        if(!docIdsFinal.contains(currentDoc.getDocId())) {

            if (topResults.size() < k && topResults.size() < relevantDocs.size()) {
                topResults.add(currentDoc);
                topResults.sort(Collections.reverseOrder());
            } else {
                if (currentDoc.getScore() > topResults.get(0).getScore() && !topResults.contains(currentDoc)) {
                    topResults.remove(0);
                    topResults.add(currentDoc);
                    topResults.sort(Collections.reverseOrder());
                }
            }
        }
    }


    // save only the documents that match all the query terms
    public static List<DocumentQP> conjunctiveProcessing(List<TermDictionary> termList, String[] query, List<String> termNonExistent) {
        ArrayList<DocumentQP> docsWithTerms = new ArrayList<>();
        ArrayList<Integer> docsIDSWithTerms = new ArrayList<>();

        for (String term : query) {
            // fills the term list
            TermDictionary termInstance = OutputResultsReader.fillTermDictionary(termList, term);

            // if query term does not exist in the collection, return null
            if (termInstance == null) {
                System.out.println("Term \"" + term + "\" not found");
                termNonExistent.add(term);
                //docsWithTerms.clear();
                return null;
            } else {
                // save all the IDs from the first term
                if (docsIDSWithTerms.isEmpty()) {
                    List<DocumentQP> docs = termInstance.getDocumentsWithTerm();
                    ArrayList<Integer> aux = new ArrayList<>();
                    for (DocumentQP doc : docs) {
                        aux.add(doc.getDocId());
                    }
                    docsIDSWithTerms.addAll(aux);
                } else {
                    List<DocumentQP> docs = termInstance.getDocumentsWithTerm();
                    ArrayList<Integer> aux = new ArrayList<>();
                    for (DocumentQP doc : docs) {
                        aux.add(doc.getDocId());
                    }
                    docsIDSWithTerms.retainAll(aux);
                }
            }
        }



            List<DocumentQP> finalList = new ArrayList<>();

            //
            for (TermDictionary term_aux : termList){
                List<DocumentQP> doc_aux = term_aux.getDocumentsWithTerm();
                for (DocumentQP doc : doc_aux){
                    if (docsIDSWithTerms.contains(doc.getDocId())){
                        finalList.add(doc);
                    }
                }
            }
            System.out.println(finalList);


//                } else {
//                    List<DocumentQP> docss = termInstance.getDocumentsWithTerm();
//                    for (DocumentQP doc: docss){
//                        System.out.print(doc + " ");
//                    }
//                    System.out.println();
//                    List<DocumentQP> compareTerms = termInstance.getDocumentsWithTerm();
//                    for (DocumentQP doc: compareTerms){
//                        if
//                    }
//                    docsWithTerms.retainAll(termInstance.getDocumentsWithTerm());
//                }


//
//        System.out.println(docsWithTerms);
//        Set<DocumentQP> docsWithTermsSet = new HashSet<>(docsWithTerms);
//        docsWithTerms = new ArrayList<>(docsWithTermsSet); // Convert Set back to List

        return removeDuplicates(finalList);
    }



    public static List<DocumentQP> disjunctiveProcessing(List<TermDictionary> termList, String[] query, List<String> termNonExistent){
        List<DocumentQP> docsWithTerms = new ArrayList<>();

        for (String term : query) {
            // fills the term list
            TermDictionary termInstance = OutputResultsReader.fillTermDictionary(termList, term);
            //System.out.println("Term \"" + term + "\"");
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