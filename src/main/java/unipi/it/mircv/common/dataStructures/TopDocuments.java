package unipi.it.mircv.common.dataStructures;


public class TopDocuments {
    private int docId;
    private double score;

    public TopDocuments(int docId, double score) {
        this.docId = docId;
        this.score = score;
    }


    public int getDocId() {
        return docId;
    }

    public void setDocId(int docId) {
        this.docId = docId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
