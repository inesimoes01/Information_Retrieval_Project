package unipi.it.mircv.common;

import junit.framework.TestCase;
import unipi.it.mircv.indexing.dataStructures.InvertedIndex;


public class InvertedIndexTest extends TestCase {

    public void testAddPosting_whenAllCorrect() {
        InvertedIndex invertedIndex = new InvertedIndex();

        String term = "apple";
        int docId = 1;
        int freq = 3;
        invertedIndex.addPosting(term, docId, freq);

        assertTrue(invertedIndex.getInvertedIndex().containsKey(term));
        assertEquals(1, invertedIndex.getInvertedIndex().get(term).size());
        assertEquals(docId, invertedIndex.getInvertedIndex().get(term).get(0).getDocId());
        assertEquals(freq, invertedIndex.getInvertedIndex().get(term).get(0).getFreq());

    }

    public void testAddPosting_whenDocIdMissing() {
        InvertedIndex invertedIndex = new InvertedIndex();

        String term = "apple";
        int docId = 0;
        int freq = 3;
        invertedIndex.addPosting(term, docId, freq);

        assertTrue(invertedIndex.getInvertedIndex().containsKey(term));
        assertEquals(1, invertedIndex.getInvertedIndex().get(term).size());
        assertEquals(docId, invertedIndex.getInvertedIndex().get(term).get(0).getDocId());
        assertEquals(freq, invertedIndex.getInvertedIndex().get(term).get(0).getFreq());

    }

    public void testSortInvertedIndexByDocId() {
    }

    public void testTestToString() {
        InvertedIndex invertedIndex = new InvertedIndex();

        invertedIndex.addPosting("apple", 1, 3);
        invertedIndex.addPosting("banana", 2, 2);

        // sorts in this way for unknown reason
        String expectedOutput = "banana: (2, 2)\napple: (1, 3)\n";

        String toStringResult = invertedIndex.toString();

        assertEquals(expectedOutput, toStringResult);
    }
}