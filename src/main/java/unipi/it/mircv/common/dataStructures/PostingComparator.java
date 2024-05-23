package unipi.it.mircv.common.dataStructures;

import java.util.Comparator;

public class PostingComparator implements Comparator<Posting> {
    @Override
    public int compare(Posting p1, Posting p2) {
        return Integer.compare(p1.getDocId(), p2.getDocId());
    }
}