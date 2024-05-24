package unipi.it.mircv.queryProcessing;

import junit.framework.TestCase;
import unipi.it.mircv.common.dataStructures.Posting;
import unipi.it.mircv.common.dataStructures.PostingList;

import java.util.ArrayList;

public class ScoringStrategyTest extends TestCase {
    public void testCheckPL() {
        ArrayList<PostingList> postingLists = new ArrayList<>();

        PostingList pl2 = new PostingList();
        pl2.setTerm("no");
        pl2.setPl(new ArrayList<Posting>());

        pl2.getPl().add(new Posting(5, 1));
        pl2.getPl().add(new Posting(6, 1));
        pl2.getPl().add(new Posting(7, 1));
        pl2.getPl().add(new Posting(8, 1));
        pl2.getPl().add(new Posting(9, 1));
        pl2.getPl().add(new Posting(10, 1));
        pl2.getPl().add(new Posting(11, 1));
        pl2.getPl().add(new Posting(12, 1));

        //postingLists.add(pl1);
        postingLists.add(pl2);

        boolean result = ScoringStrategy.checkPL(postingLists, 5);

        assertTrue(result);
    }



}