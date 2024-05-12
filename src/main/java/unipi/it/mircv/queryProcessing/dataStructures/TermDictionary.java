package unipi.it.mircv.queryProcessing.dataStructures;

import unipi.it.mircv.common.Flags;

import java.util.*;

public class TermDictionary {
    private String term;

    public Posting getPostingByDocId(PostingList postingList, Integer docId){
        for (Posting post : postingList.getPl()){
            if (post.getDocId().equals(docId)) return post;
        }
        return null;
    }
    private PostingList posting;
    // DocId Freq
    // public Iterator<Posting> iterator = posting.iterator();
    // iterator to help transverse the postingList
    private Integer collectionFrequency; // not being used
    // how many times the term appear in the collection
    private Integer documentFrequency;
    // how many documents the term appears in

    private Double termUpperBoundTFIDF;
    private Double termUpperBoundBM25;
    private int offset;

    public void addPostingToPostingList(Posting post){
        posting.getPl().add(post);
    }


    private List<DocumentQP> documentsWithTerm = new ArrayList<>();

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public PostingList getPostingList() {
        return posting;
    }

//    public void setPostingList(List<Posting> posting) {
//        this.posting = posting;
//    }

    public List<DocumentQP> getDocumentsWithTerm() {
        return documentsWithTerm;
    }

//    public List<Integer> getDocumentsWithTermDocIDs() {
//        List<Integer> list = new ArrayList<>();
//        for (DocumentQP doc : documentsWithTerm){
//            list.add(doc.getDocId());
//        }
//        return list;
//    }

    public void setDocumentsWithTerm(DocumentQP documentsWithTerm) {
        this.documentsWithTerm.add(documentsWithTerm);
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }



//    public Integer getCollectionFrequency() {
//        return collectionFrequency;
//    }

    public void setCollectionFrequency(Integer collectionFrequency) {
        this.collectionFrequency = collectionFrequency;
    }

    public Integer getDocumentFrequency() {
        return documentFrequency;
    }

    public void setDocumentFrequency(Integer documentFrequency) {
        this.documentFrequency = documentFrequency;
    }

    public Double getTermUpperBound() {
        if (Flags.isIsTFIDF_flag()) return termUpperBoundTFIDF;
        else return termUpperBoundBM25;
    }

    public void setTermUpperBound(Double termUpperBound) {
        if (Flags.isIsTFIDF_flag()) this.termUpperBoundTFIDF = termUpperBound;
        else this.termUpperBoundBM25 = termUpperBound;
    }

//    @Override
//    public double compareTo(TermDictionary other) {
//        return Double.compare(this.termUpperBoundBM25, other.termUpperBoundBM25);
//    }

//    public Double getTermUpperBoundBM25() {
//        return termUpperBoundBM25;
//    }

//    public void setTermUpperBoundBM25(Double termUpperBoundBM25) {
//        this.termUpperBoundBM25 = termUpperBoundBM25;
//    }
}
