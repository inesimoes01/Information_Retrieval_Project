package unipi.it.mircv.common.dataStructures;

public class Posting {
    private int freq;
    private int docId;

    public Posting() {
    }

    public Posting(int docId, int freq) {
        this.docId = docId;
        this.freq = freq;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public int getDocId() {
        return docId;
    }

    public int getFreq() {
        return freq;
    }
    @Override
    public String toString() {
        return "(" + docId + ", " + freq + ")";
    }
}
