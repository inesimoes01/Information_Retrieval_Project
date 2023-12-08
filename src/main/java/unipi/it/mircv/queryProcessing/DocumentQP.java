package unipi.it.mircv.queryProcessing;

public class DocumentQP implements Comparable<DocumentQP>{
    private Integer docId;
    private Integer length;
    private Integer upperBoundCase;



    private double score;

    public Integer getDocId() {
        return docId;
    }

    public void setDocId(Integer docId) {
        this.docId = docId;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public Integer getUpperBoundCase() {
        return upperBoundCase;
    }

    public void setUpperBoundCase(Integer upperBoundCase) {
        this.upperBoundCase = upperBoundCase;
    }


    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public int compareTo(DocumentQP other) {
        // Sort in descending order of scores
        return Double.compare(other.score, this.score);
    }
}
