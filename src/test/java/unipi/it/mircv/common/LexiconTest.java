package unipi.it.mircv.common;

import junit.framework.TestCase;
import unipi.it.mircv.indexing.dataStructures.Lexicon;


import java.util.ArrayList;


public class LexiconTest extends TestCase {


    public void testUpdateLexicon_whenDocIdNotPresent() {
        Lexicon lexicon = new Lexicon();

        String term = "apple";
        int freq = 5;
        lexicon.updateLexicon(term, freq);

        // check if exists
        assertTrue(lexicon.getLexicon().containsKey(term));

        // check if it saved the right freq
        assertEquals(freq, lexicon.getLexicon().get(term).getCollectionFrequency());

        // check if only one document has this term
        assertEquals(1, lexicon.getLexicon().get(term).getDocumentFrequency());

    }

    public void testUpdateLexicon_whenDocIdAlreadyPresent() {
        Lexicon lexicon = new Lexicon();

        String term = "apple";
        int initialFreq = 5;
        int updatedFreq = 10;

        lexicon.updateLexicon(term, initialFreq);
        lexicon.updateLexicon(term, updatedFreq);

        // check if exists
        assertTrue(lexicon.getLexicon().containsKey(term));

        // check if freq was updated in the collection
        assertEquals(updatedFreq+initialFreq, lexicon.getLexicon().get(term).getCollectionFrequency());

        // check if only two documents have this term
        assertEquals(2, lexicon.getLexicon().get(term).getDocumentFrequency());
    }

//    public void testSortLexiconByKey() {
//        Lexicon lexicon = new Lexicon();
//        lexicon.updateLexicon("banana", 3);
//        lexicon.updateLexicon("apple", 5);
//        String expectedOutput = "{banana=TS{cF=3, dF=1}, apple=TS{cF=5, dF=1}}";
//
//        lexicon.sortLexicon();
//
//        assertEquals(expectedOutput, lexicon.getLexicon().toString());
//
//    }

    public void testSortLexicon() {
        Lexicon lexicon = new Lexicon();

        lexicon.updateLexicon("banana", 3);
        lexicon.updateLexicon("apple", 5);
        String expectedOutput = "[apple, banana]";

        ArrayList<String> sortedLexicon = lexicon.sortLexicon();

        assertEquals(expectedOutput, sortedLexicon.toString());

    }

    public void testToString() {
        Lexicon lexicon = new Lexicon();
        lexicon.updateLexicon("apple", 5);
        lexicon.updateLexicon("banana", 3);
        String expectedOutput = "Term: banana\nCollection Frequency: 3\nDocument Frequency: 1\n\nTerm: apple\nCollection Frequency: 5\nDocument Frequency: 1\n\n";

        String toStringResult = lexicon.toString();

        assertEquals(expectedOutput, toStringResult);
    }
}