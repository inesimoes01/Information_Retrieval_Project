package unipi.it.mircv.indexing;
import unipi.it.mircv.common.*;

import java.util.HashMap;


public class Index {
    private static int blockNumber = 1;
    private static InvertedIndex invertedIndex = new InvertedIndex();
    private static Lexicon lexicon = new Lexicon();
    private static DocumentIndex documentIndex = new DocumentIndex();
    public void setBlockNumber(int blockNumber) {
        this.blockNumber = blockNumber;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public void createIndex(Doc doc) {
        Util util = new Util();
        util.printUsage();
        HashMap<String, Integer> termcounter = new HashMap<>();

        documentIndex.updateDocumentIndex(doc.getId(),doc.getText().length);

        for (String term : doc.getText()){
            termcounter.put(term, termcounter.containsKey(term) ? termcounter.get(term) + 1 : 1);
        }

        if (util.isMemoryFull(50.0)){
            //Write to the disk
            //Increment blockNumber
            setBlockNumber(blockNumber + 1);
        }
        //read doc and do indexing
        for (String term : termcounter.keySet()) {
            lexicon.updateLexicon(term,termcounter.get(term));
            invertedIndex.addPosting(term, doc.getId(),termcounter.get(term) );
        }
        System.out.println("This is the Document  "+ doc.getId() );
        //System.out.println(invertedIndex);
        //System.out.println(lexicon);
        System.out.println(documentIndex);
    }

    }

