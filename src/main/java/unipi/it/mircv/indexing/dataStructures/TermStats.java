package unipi.it.mircv.indexing.dataStructures;

public class TermStats {
        private int collectionFrequency;//how many times term appearn is collection
        private int documentFrequency;//in how many documents the term appears
        private long invertedIndexOffset;

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

    public void addToCollectionFrequency(int value) {
        this.collectionFrequency += value;
    }

    public TermStats(){}
    public TermStats(int collectionFrequency, int documentFrequency,long invertedIndexOffset){

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
                " " + documentFrequency;
    }
}

