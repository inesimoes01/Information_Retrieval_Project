package unipi.it.mircv.common.dataStructures;

import unipi.it.mircv.common.Flags;
import unipi.it.mircv.queryProcessing.dataStructures.PostingList;

import java.util.*;

public class TermDictionary {
    private String term;

    //private ArrayList<Posting> postingList = new ArrayList<>();
    private PostingList postingList;

    private int collectionFrequency;
    private int documentFrequency;
    private Double termUpperBoundTFIDF;
    private Double termUpperBoundBM25;
    private int offsetDocId;
    private int offsetFreq;
    private int endOffset;
    private int startOffset;

    public TermDictionary(String term, int collectionFrequency, int documentFrequency, int offsetDocId, int offsetFreq, int startOffset, int endOffset, Double termUpperBoundTFIDF) {
        this.term = term;
        this.collectionFrequency = collectionFrequency;
        this.documentFrequency = documentFrequency;
        this.offsetDocId = offsetDocId;
        this.offsetFreq = offsetFreq;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.termUpperBoundTFIDF = termUpperBoundTFIDF;
    }

    public TermDictionary(){}

//    public PostingList getPostingByDocId(List<PostingList> postingList, Integer docId){
//        for (PostingList post : postingList){
//            if (post.getDocId().equals(docId)) return post;
//        }
//        return null;
//    }


    public int getEndOffset() {
        return endOffset;
    }

    public void setEndOffset(int endOffset) {
        this.endOffset = endOffset;
    }

    public int getOffsetDocId() {
        return offsetDocId;
    }

    public void setOffsetDocId(int offsetDocId) {
        this.offsetDocId = offsetDocId;
    }

    public int getOffsetFreq() {
        return offsetFreq;
    }

    public void setOffsetFreq(int offsetFreq) {
        this.offsetFreq = offsetFreq;
    }

    private ArrayList<Integer> documentsWithTerm = new ArrayList<>();


    public ArrayList<Integer> getDocumentsWithTerm() {
        return documentsWithTerm;
    }

//    public List<Integer> getDocumentsWithTermDocIDs() {
//        List<Integer> list = new ArrayList<>();
//        for (DocumentQP doc : documentsWithTerm){
//            list.add(doc.getDocId());
//        }
//        return list;
//    }

    public void setDocumentsWithTerm(int docId) {
        this.documentsWithTerm.add(docId);
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
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

    public Double getTermUpperBound() {
        return termUpperBoundTFIDF;

    }

    public void setTermUpperBound(Double termUpperBound) {
        if (Flags.isIsTFIDF_flag()) this.termUpperBoundTFIDF = termUpperBound;
        else this.termUpperBoundBM25 = termUpperBound;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public void setStartOffset(int startOffset) {
        this.startOffset = startOffset;
    }

    public PostingList getPostingList() {
        return postingList;
    }

    public void setPostingList(PostingList postingList) {
        this.postingList = postingList;
    }

    public void remove(int i) {

    }
}
