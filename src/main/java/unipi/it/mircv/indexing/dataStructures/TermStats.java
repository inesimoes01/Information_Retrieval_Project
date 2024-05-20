package unipi.it.mircv.indexing.dataStructures;

public class TermStats {
    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    private String term;
    //how many times term appears in the collection
    private int collectionFrequency;
    //number of documents the term appears in
    private int documentFrequency;
    private long invertedIndexOffset;

    private int lastDocIdInserted;



    public void updateCollectionFrequency(int freq){
        this.collectionFrequency += freq;
    }
    public void updateDocumentFrequency(){
        this.documentFrequency++;
    }
    public void updateLastDocIdInserted(int docId){
        this.lastDocIdInserted = docId;
    }


    public void addToCollectionFrequency(int value) {
        this.collectionFrequency += value;
    }

    public TermStats(){}

    public TermStats(String term, int collectionFrequency, int documentFrequency,long invertedIndexOffset){
        this.term = term;
        this.collectionFrequency = collectionFrequency;
        this.documentFrequency = documentFrequency;
        this.invertedIndexOffset = invertedIndexOffset;
    }
    public TermStats(int collectionFrequency, int documentFrequency) {
        this.collectionFrequency = collectionFrequency;
        this.documentFrequency = documentFrequency;
    }
    public void addToDocumentFrequency(int value) {
        this.documentFrequency += value;
    }
    @Override
    public String toString() {
        return collectionFrequency +
                " " + documentFrequency + " " + invertedIndexOffset;
    }

    public void setCollectionFrequency(int collectionFrequency) {
        this.collectionFrequency = collectionFrequency;
    }

    public int getCollectionFrequency() {
        return collectionFrequency;
    }

    public void setDocumentFrequency(int documentFrequency) {
        this.documentFrequency = documentFrequency;
    }

    public int getDocumentFrequency() {
        return documentFrequency;
    }

    public void setInvertedIndexOffset(long invertedIndexOffset) {this.invertedIndexOffset = invertedIndexOffset;}

    public long getInvertedIndexOffset() {return invertedIndexOffset;}

    public int getLastDocIdInserted() {
        return lastDocIdInserted;
    }

}

