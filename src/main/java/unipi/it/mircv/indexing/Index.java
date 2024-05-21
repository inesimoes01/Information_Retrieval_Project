package unipi.it.mircv.indexing;
import unipi.it.mircv.indexing.dataStructures.Doc;
import unipi.it.mircv.indexing.dataStructures.DocumentIndex;
import unipi.it.mircv.indexing.dataStructures.InvertedIndex;
import unipi.it.mircv.indexing.dataStructures.Lexicon;
import unipi.it.mircv.common.MemoryUtil;

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

    public void createIndex(Doc doc) {
        MemoryUtil memoryUtil = new MemoryUtil();
        IndexUtil indexUtil = new IndexUtil();

        HashMap<String, Integer> termCounter = new HashMap<>();

        for (String term : doc.getText()){
            termCounter.put(term, termCounter.containsKey(term) ? termCounter.get(term) + 1 : 1);
        }

        // check if the memory is over a certain threshold to write the structures to the disk
        if (memoryUtil.isMemoryFull(20)){
            IndexUtil.writeBlockToDisk(getBlockNumber(), documentIndex, 0);
            IndexUtil.writeBlockToDisk(getBlockNumber(), lexicon, 0);
            indexUtil.writeBlockToDisk(getBlockNumber(), invertedIndex, 0);

            lexicon = new Lexicon();
            invertedIndex = new InvertedIndex();
            documentIndex = new DocumentIndex();

            setBlockNumber(getBlockNumber() + 1);
            System.gc();
        }

        // update structures as usual
        //documentIndex.updateDocumentIndex(doc.getId(), doc.getText().length);

        for (String term : termCounter.keySet()) {
            lexicon.updateLexicon(term, termCounter.get(term));
            invertedIndex.addPosting(term, doc.getId(),termCounter.get(term) );
        }

    }

}

