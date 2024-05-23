package unipi.it.mircv.queryProcessing;

import unipi.it.mircv.common.Paths;
import unipi.it.mircv.common.dataStructures.Posting;
import unipi.it.mircv.compression.UnaryInteger;
import unipi.it.mircv.compression.VariableByte;
import unipi.it.mircv.common.dataStructures.TermDictionary;
import unipi.it.mircv.queryProcessing.dataStructures.PostingList;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class OutputResultsReader {

    private static int nTotalDocuments;


    private static double averageDocLen;

    public static TermDictionary fillTermDictionary(List<TermDictionary> listToFill, String queryTerm) throws FileNotFoundException {
        try (RandomAccessFile invertedIndexFile = new RandomAccessFile(Paths.PATH_INVERTED_INDEX_MERGED, "r")) {
            TermDictionary currentTerm = new TermDictionary();

//            // verifies if term exists in collection
//            // saves term, CollectionFrequency, DocumentFrequency, TermUpperBounds, Offsets
//            if (QueryProcessing.getLexicon().containsKey(queryTerm)) {
//                currentTerm = QueryProcessing.getLexicon().get(queryTerm);
//            } else return null;
//
//            // saves PostingList (DocId Freq)
//            searchTermInInvertedIndex(invertedIndexFile, currentTerm);
//
//            // para cada docid, guardar docId
//            for (Map.Entry<Integer, Integer> pL : currentTerm.getPostingList().entrySet()){
//                if (QueryProcessing.getDocumentIndex().containsKey(pL.getKey())) {
//                    currentTerm.setDocumentsWithTerm(pL.getKey());
//                } else {
//                    return null;
//                }
//            }
//
//            // fill the list
//            listToFill.add(currentTerm);
            return currentTerm;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PostingList searchTermInInvertedIndex(RandomAccessFile rafInvertedIndex, TermDictionary term) throws IOException {
        PostingList pl = new PostingList();

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

        ArrayList<Posting> newPL = new ArrayList<>();
        pl.setTerm(term.getTerm());
        pl.setPl(new ArrayList<Posting>());
        for (int i = 0; i < docIdDecoded.size(); i++) {
            pl.getPl().add(new Posting(docIdDecoded.get(i), frequencyDecoded.get(i)));
        }
        return pl;
    }

    public static void saveTotalNumberDocs() {
        try (BufferedReader br = new BufferedReader(new FileReader(Paths.PATH_AVGDOCLEN))) {
            averageDocLen = Double.parseDouble(br.readLine());
            nTotalDocuments = Integer.parseInt(br.readLine());
        } catch (IOException e) {
            e.printStackTrace();
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

    public static double getAverageDocLen() {
        return averageDocLen;
    }

}
