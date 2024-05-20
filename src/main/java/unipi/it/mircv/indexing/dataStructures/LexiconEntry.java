package unipi.it.mircv.indexing.dataStructures;

public class LexiconEntry {


    private String term;
    private int cf;
    private int df;
    private int blockIndex;



    public LexiconEntry(String term, int cf, int df, int blockIndex) {
        this.term = term;
        this.cf = cf;
        this.df = df;
        this.blockIndex = blockIndex;

    }
    public int getBlockIndex() {
        return blockIndex;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public int getCf() {
        return cf;
    }

    public void setDf(int df) {
        this.df = df;
    }

    public int getDf() {
        return df;
    }

    public void setCf(int tf) {
        this.cf = tf;
    }
    
    
}
