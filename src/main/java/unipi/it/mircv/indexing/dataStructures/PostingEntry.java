package unipi.it.mircv.indexing.dataStructures;

public class PostingEntry {
    private String term;
    private int blockIndex;

    public PostingEntry(String term, int blockIndex) {
        this.term = term;
        this.blockIndex = blockIndex;
    }

    public String getTerm() {
        return term;
    }

    public int getBlockIndex() {
        return blockIndex;
    }
}
