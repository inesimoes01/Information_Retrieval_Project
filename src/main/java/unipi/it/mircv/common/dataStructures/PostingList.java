package unipi.it.mircv.common.dataStructures;

import unipi.it.mircv.common.dataStructures.Posting;
import unipi.it.mircv.common.dataStructures.TermDictionary;

import java.util.ArrayList;
import java.util.List;

public class PostingList {
    private String term;
    private ArrayList<Posting> pl;
    private int currentPosition;


    // current posting of the iterator
    public Posting getCurrentPosting() {
        if (currentPosition == -1) {
            return null;
        }
        if (currentPosition < pl.size()) {
            return pl.get(currentPosition);
        } else {
            return null;
        }
    }

    // updates next posting of the iterator and updates the current position
    public Posting nextPosting() {
        if (currentPosition + 1 < pl.size()) {
            currentPosition++;
            return pl.get(currentPosition);
        }
        return null;
    }


    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public ArrayList<Posting> getPl() {
        return pl;
    }

    public void setPl(ArrayList<Posting> pl) {
        this.pl = pl;
    }



}
