package unipi.it.mircv.indexing;
import unipi.it.mircv.common.*;

import java.util.HashMap;


public class Index {
    private static int blockNumber = 1;
    private static InvertedIndex invertedIndex = new InvertedIndex();

    public static void setInvertedIndex(InvertedIndex invertedIndex) {
        Index.invertedIndex = invertedIndex;
    }

    public static InvertedIndex getInvertedIndex() {
        return invertedIndex;
    }

    private static Lexicon lexicon = new Lexicon();
    public static void setLexicon(Lexicon lexicon) {
        Index.lexicon = lexicon;
    }

    public static Lexicon getLexicon() {
        return lexicon;
    }

    private static DocumentIndex documentIndex = new DocumentIndex();

    public static void setDocumentIndex(DocumentIndex documentIndex) {
        Index.documentIndex = documentIndex;
    }

    public static DocumentIndex getDocumentIndex() {
        return documentIndex;
    }

    public void setBlockNumber(int blockNumber) {
        this.blockNumber = blockNumber;
    }

    public int getBlockNumber() {
        return blockNumber;
    }

    public int createIndex(Doc doc) {
        Util util = new Util();
        //util.printUsage();
        HashMap<String, Integer> termcounter = new HashMap<>();

        for (String term : doc.getText()){
            termcounter.put(term, termcounter.containsKey(term) ? termcounter.get(term) + 1 : 1);
        }

        if (util.isMemoryFull(20)){
            //writeBlock(lexicon, lexicon.sortLexicon(), documentIndex.sortDocumentIndex()); //writes the current block to disk

            util.writeBlockToDisk(getBlockNumber(),documentIndex);
            util.writeBlockToDisk(getBlockNumber(),lexicon);
            util.writeBlockToDisk(getBlockNumber(),invertedIndex);
            //util.writeBlockToDisk(getBlockNumber(),lexicon,invertedIndex);


            lexicon = new Lexicon();
            invertedIndex = new InvertedIndex();
            documentIndex = new DocumentIndex();

            setBlockNumber(getBlockNumber() + 1);
            System.gc(); //calls the garbage collector to force to free memory.
            //Write to the disk
            //Increment blockNumber

        }
        documentIndex.updateDocumentIndex(doc.getId(),doc.getText().length);
        //read doc and do indexing
        for (String term : termcounter.keySet()) {
            lexicon.updateLexicon(term,termcounter.get(term));
            invertedIndex.addPosting(term, doc.getId(),termcounter.get(term) );
        }
        //System.out.println("This is the Document  "+ doc.getId() );
        //System.out.println(invertedIndex);
        //System.out.println(lexicon);
        return getBlockNumber();
    }

    }

