package unipi.it.mircv.queryProcessing;

import unipi.it.mircv.common.Paths;
import unipi.it.mircv.compression.UnaryInteger;
import unipi.it.mircv.compression.VariableByte;
import unipi.it.mircv.indexing.dataStructures.Posting;
import unipi.it.mircv.queryProcessing.dataStructures.DocumentQP;
import unipi.it.mircv.queryProcessing.dataStructures.PostingList;
import unipi.it.mircv.queryProcessing.dataStructures.TermDictionary;

import javax.sound.midi.SysexMessage;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class OutputResultsReader {

    private static int nTotalDocuments;

    public static TermDictionary fillTermDictionary(List<TermDictionary> listToFill, String queryTerm) throws FileNotFoundException {
        try (RandomAccessFile invertedIndexFile = new RandomAccessFile(String.valueOf(Paths.PATH_INVERTED_INDEX), "r")) {
            TermDictionary currentTerm;

            // verifies if term exists in collection
            // saves term, CollectionFrequency, DocumentFrequency, TermUpperBounds, Offsets
            if (QueryProcessing.getLexicon().containsKey(queryTerm)) {
                currentTerm = QueryProcessing.getLexicon().get(queryTerm);
            } else return null;

            // saves PostingList (DocId Freq)
            searchTermInInvertedIndex(invertedIndexFile, currentTerm);

            // para cada docid, guardar docId
            for (Map.Entry<Integer, Integer> pL : currentTerm.getPostingList().entrySet()){
                if (QueryProcessing.getDocumentIndex().containsKey(pL.getKey())) {
                    currentTerm.setDocumentsWithTerm(pL.getKey());
                } else {
                    return null;
                }
            }

            // fill the list
            listToFill.add(currentTerm);
            return currentTerm;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void searchTermInInvertedIndex(RandomAccessFile rafInvertedIndex, TermDictionary term) throws IOException {
        long len1 = term.getOffsetFreq() - term.getOffsetDocId();
        long len2 = term.getEndOffset() - term.getOffsetFreq();
        long currentOffset = term.getStartOffset();

        byte[] docIdBytes = new byte[(int) len1];
        byte[] freqBytes = new byte[(int) len2];

        rafInvertedIndex.seek(0);
        rafInvertedIndex.seek(currentOffset + term.getTerm().getBytes().length);
        rafInvertedIndex.readFully(docIdBytes);
        rafInvertedIndex.seek(0);
        rafInvertedIndex.seek(currentOffset + term.getOffsetFreq());
        rafInvertedIndex.readFully(freqBytes);
        rafInvertedIndex.seek(0);

        List<Integer> docIdDecoded = VariableByte.decode(docIdBytes);
        List<Integer> frequencyDecoded = UnaryInteger.decodeFromUnary(freqBytes);

        for (int j = 0; j < docIdDecoded.size(); j++) {
            term.getPostingList().put(docIdDecoded.get(j), frequencyDecoded.get(j));
        }

    }

    public static void saveTotalNumberDocs() throws IOException {
        List<String> allLines = Files.readAllLines(Paths.PATH_DOCUMENT_INDEX, StandardCharsets.UTF_8);

        // get last line from file
        if (!allLines.isEmpty()) {
            String lastLine = allLines.get(allLines.size() - 1);
            nTotalDocuments = Integer.parseInt(lastLine.split(" ")[0]);
        } else {
            System.out.println("The file is empty.");
        }
    }

    public static void main(String[] args) throws FileNotFoundException {
        List<TermDictionary> listToFill = new ArrayList<>();
        fillTermDictionary(listToFill, "0");
        System.out.println("AHH");
    }

    public static int getnTotalDocuments() {
        return nTotalDocuments;
    }
}
