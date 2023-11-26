package unipi.it.mircv.common;

public class TermStats {
        private int collectionFrequency;//how many times term appearn is collection
        private int documentFrequency;//in how many documents the term appears

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

    @Override
    public String toString() {
        return "TS{" +
                "cF=" + collectionFrequency +
                ", dF=" + documentFrequency +
                '}';
    }
}

