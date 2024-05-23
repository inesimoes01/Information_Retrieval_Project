package unipi.it.mircv.queryProcessing;

import junit.framework.TestCase;
import unipi.it.mircv.common.Flags;
import unipi.it.mircv.common.dataStructures.TermDictionary;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class ConjuntiveDisjunctiveTest extends TestCase {

    public void testProcessingStrategyConjunctive() throws FileNotFoundException {
//        Flags.setIsConjunctive_flag(true);
//
//        List<TermDictionary> termDictionaryList = new ArrayList<>();
//        String queryFull = "get a car for my son";
//        List<String> termNonExistent = new ArrayList<>();
//
//        OutputResultsReader.fillTermDictionary(termDictionaryList, query);
//        ConjuntiveDisjunctive.processingStrategy(termDictionaryList, query, termNonExistent);

    }
    public void testProcessingStrategyDisjunctive() {
        Flags.setIsConjunctive_flag(false);


    }
}