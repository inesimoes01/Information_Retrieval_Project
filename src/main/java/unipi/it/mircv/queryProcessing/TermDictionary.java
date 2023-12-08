package unipi.it.mircv.queryProcessing;

import java.util.*;

public class TermDictionary {
    private String term;
    private List<Posting> posting = new ArrayList<>();
    // DocId Freq
    public Iterator<Posting> iterator = posting.iterator();
    // iterator to help transverse the postingList
    private Integer collectionFrequency; // not being used
    // how many times the term appear in the collection
    private Integer documentFrequency;

    // how many documents the term appears in
    private Double termUpperBoundTFIDF;
    private Double termUpperBoundBM25;

    public static class Posting {
        private Integer docId;
        private Integer freq;
        public Integer getDocId() {
            return docId;
        }
        public void setDocId(Integer docId) {
            this.docId = docId;
        }
        public Integer getFreq() {
            return freq;
        }
        public void setFreq(Integer freq) {
            this.freq = freq;
        }
    }

    public Posting getPostingByDocId(List<Posting> postingList, Integer docId){
        for (Posting post : postingList){
            if (post.docId.equals(docId)) return post;
        }
        return null;
    }

    private List<DocumentQP> documentsWithTerm = new ArrayList<>();

    public List<Posting> getPostingList() {
        return posting;
    }

    public void setPostingList(List<Posting> posting) {
        this.posting = posting;
    }

    public List<DocumentQP> getDocumentsWithTerm() {
        return documentsWithTerm;
    }

    public List<Integer> getDocumentsWithTermDocIDs() {
        List<Integer> list = new ArrayList<>();
        for (DocumentQP doc : documentsWithTerm){
            list.add(doc.getDocId());
        }
        return list;
    }

    public void setDocumentsWithTerm(DocumentQP documentsWithTerm) {
        this.documentsWithTerm.add(documentsWithTerm);
    }



    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }



    public Integer getCollectionFrequency() {
        return collectionFrequency;
    }

    public void setCollectionFrequency(Integer collectionFrequency) {
        this.collectionFrequency = collectionFrequency;
    }

    public Integer getDocumentFrequency() {
        return documentFrequency;
    }

    public void setDocumentFrequency(Integer documentFrequency) {
        this.documentFrequency = documentFrequency;
    }

    public Double getTermUpperBoundTFIDF() {
        return termUpperBoundTFIDF;
    }

    public void setTermUpperBoundTFIDF(Double termUpperBoundTFIDF) {
        this.termUpperBoundTFIDF = termUpperBoundTFIDF;
    }

    public Double getTermUpperBoundBM25() {
        return termUpperBoundBM25;
    }

    public void setTermUpperBoundBM25(Double termUpperBoundBM25) {
        this.termUpperBoundBM25 = termUpperBoundBM25;
    }
}
