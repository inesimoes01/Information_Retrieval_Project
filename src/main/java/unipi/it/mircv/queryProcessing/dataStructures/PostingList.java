package unipi.it.mircv.queryProcessing.dataStructures;

<<<<<<< Updated upstream
import java.util.ArrayList;
import java.util.Iterator;
=======
>>>>>>> Stashed changes
import java.util.List;

public class PostingList {
    private String term;
    private List<Posting> pl;
<<<<<<< Updated upstream
    private Double maxTermUpperBound;

    public Posting getCurrentPosting() {
        return currentPosting;
    }

    public void setCurrentPosting(Posting currentPosting) {
        this.currentPosting = currentPosting;
    }

    private Posting currentPosting;
    public Iterator<Posting> postingIterator;
    public Iterator<Posting> skippingPostingIterator;
    public Posting currentSkippingElem;

    public PostingList(String term, List<Posting> pl, Double maxTermUpperBound) {
        this.term = term;
        this.pl = pl;
        this.maxTermUpperBound = maxTermUpperBound;
    }

    public String getTerm() {
        return term;
    }

    public List<Posting> getPl() {
        return pl;
    }

    public Double getMaxTermUpperBound() {
        return maxTermUpperBound;
=======


    public PostingList(String term, List<Posting> pl) {
        this.term = term;
        this.pl = pl;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
>>>>>>> Stashed changes
    }

    public List<Posting> getPl() {
        return pl;
    }

    public void setPl(List<Posting> pl) {
        this.pl = pl;
    }
}
