package unipi.it.mircv.queryProcessing;

import junit.framework.TestCase;
import unipi.it.mircv.common.dataStructures.TermDictionary;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import static unipi.it.mircv.queryProcessing.QueryProcessing.loadDocumentIndex;
import static unipi.it.mircv.queryProcessing.QueryProcessing.loadLexicon;

public class OutputResultsReaderTest extends TestCase {
    private static TreeMap<String, TermDictionary> lexicon = new TreeMap<>();
    private static TreeMap<Integer, Integer> documentIndex = new TreeMap<>();

    public void loadStructures(){
        loadLexicon();
        loadDocumentIndex();
    }
    public void testCorrectFillTermDictionary_TermExists() throws FileNotFoundException {
        loadStructures();

        List<TermDictionary> listToFill = new ArrayList<>();
        String queryTerm = "smartclean";

        TermDictionary finalTermExpected = new TermDictionary(queryTerm, 2, 2, 10, 18, 628053394, 19, 6.64);
        finalTermExpected.getPostingList().put(7568166, 1);
        finalTermExpected.getPostingList().put(8572375, 1);

        TermDictionary finalTermCalculated = OutputResultsReader.fillTermDictionary(listToFill, queryTerm);
        assert finalTermCalculated != null;

        assertEquals(finalTermExpected.getTerm(), finalTermCalculated.getTerm());
        assertEquals(finalTermExpected.getDocumentFrequency(), finalTermCalculated.getDocumentFrequency());
        assertEquals(finalTermExpected.getOffsetDocId(), finalTermCalculated.getOffsetDocId());
        assertEquals(finalTermExpected.getOffsetFreq(), finalTermCalculated.getOffsetFreq());
        assertEquals(finalTermExpected.getEndOffset(), finalTermCalculated.getEndOffset());
        assertEquals(finalTermExpected.getStartOffset(), finalTermCalculated.getStartOffset());
        assertEquals(finalTermExpected.getPostingList(), finalTermCalculated.getPostingList());
        assertEquals(finalTermExpected.getTermUpperBound(), finalTermCalculated.getTermUpperBound());
    }

    public void testFillTermDictionary_TermDoesntExist() throws FileNotFoundException {
        loadStructures();

        List<TermDictionary> listToFill = new ArrayList<>();
        String queryTerm = "smartcleanclean";

        TermDictionary finalTermCalculated = OutputResultsReader.fillTermDictionary(listToFill, queryTerm);

        assertNull(finalTermCalculated);

    }
}