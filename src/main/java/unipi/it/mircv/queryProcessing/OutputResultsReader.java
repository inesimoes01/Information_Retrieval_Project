package unipi.it.mircv.queryProcessing;

import unipi.it.mircv.common.Paths;
import unipi.it.mircv.compression.UnaryInteger;
import unipi.it.mircv.compression.VariableByte;
import unipi.it.mircv.queryProcessing.dataStructures.DocumentQP;
import unipi.it.mircv.queryProcessing.dataStructures.PostingList;
import unipi.it.mircv.queryProcessing.dataStructures.TermDictionary;

import javax.sound.midi.SysexMessage;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class OutputResultsReader {

    private static int nTotalDocuments;

    public static int getnTotalDocuments() {
        return nTotalDocuments;
    }

    public static TermDictionary fillTermDictionary(List<TermDictionary> listToFill, String queryTerm){
        TermDictionary term = new TermDictionary();

        // verifies if term exists in collection
        // saves term, CollectionFrequency, DocumentFrequency, TermUpperBounds, Offset
        if (!searchTermInLexicon(term, queryTerm)) return null;

        // saves PostingList (DocId Freq)
        if (!searchTermInInvertedIndex(term)) return null;

        // para cada docid, criar um DocumentQP
        for (PostingList posting : term.getPostingList()) {
            DocumentQP doc = new DocumentQP();
            doc.setDocId(posting.getDocId());
            searchDocIdInDocumentIndex(doc);
            term.setDocumentsWithTerm(doc);
        }

        // fill the list
        listToFill.add(term);
        return term;
    }

    // saves Collection Frequency and Document Frequency
    private static boolean searchTermInLexicon(TermDictionary term, String queryTerm){
        try {
            List<String> lines = Files.readAllLines(Paths.PATH_LEXICON, StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split(" ");
                if (parts.length >= 2 && parts[0].equalsIgnoreCase(queryTerm)) {
                    //boolean termExistsInCollection = true;

                    term.setTerm(queryTerm);
                    term.setCollectionFrequency(Integer.parseInt(parts[1].trim()));
                    term.setDocumentFrequency(Integer.parseInt(parts[2].trim()));
                    term.setOffset(Integer.parseInt(parts[3].trim()));
                    term.setTermUpperBound(Double.parseDouble(parts[4]));
                    return true;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    // saves Document Length
    private static void searchDocIdInDocumentIndex(DocumentQP doc){
        try {
            List<String> lines = Files.readAllLines(Paths.PATH_DOCUMENT_INDEX, StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split(" ");
                if (parts.length >= 2 && parts[0].equalsIgnoreCase(String.valueOf(doc.getDocId()))) {
                    doc.setLength(Integer.parseInt(parts[1]));
                    return;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean searchTermInInvertedIndex(TermDictionary term){
        try {
            RandomAccessFile invertedIndexFile = new RandomAccessFile(String.valueOf(Paths.PATH_INVERTED_INDEX), "r");
            invertedIndexFile.seek(term.getOffset());
            String line = invertedIndexFile.readLine();
            byte[] lineBytes  = line.getBytes();

            String termFile = line.substring(0, term.getOffsetDocId()).trim();
            byte[] docIdBytes = line.substring(term.getOffsetDocId(), term.getOffsetFreq()).getBytes();
            byte[] freqString = line.substring(term.getOffsetFreq(), term.getEndOffset()).getBytes();

            List<Integer> docIdLine = VariableByte.decode(docIdBytes);
            List<Integer> frequencyLine = UnaryInteger.decodeFromUnary(freqString);


            String[] checkTerm = line.split(" ");
            if (checkTerm[0].equalsIgnoreCase(term.getTerm())){
                for (int j = 1; j < (checkTerm.length)/2; j++) {
                    //System.out.println("Values " + checkTerm[j] + " "+ checkTerm[j+(checkTerm.length-1)/2]);
                    PostingList pL = new PostingList(Integer.valueOf(checkTerm[j]), Integer.valueOf(checkTerm[j+(checkTerm.length-1)/2]));
                    term.getPostingList().add(pL);
                }
                return true;
            }

            invertedIndexFile.close();
            //System.out.println("Term");
            // term not found in collection
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    // saves DocId and Freq
//    private static boolean searchTermInInvertedIndex(TermDictionary term){
//        try {
//            List<String> lines = Files.readAllLines(PATH_INVERTED_INDEX, StandardCharsets.UTF_8);
//            for (String line : lines) {
//                String[] checkTerm = line.split(" ");
//                if (checkTerm[0].equalsIgnoreCase(term.getTerm())){
//                    for (int j = 1; j < (checkTerm.length)/2; j++) {
//                        //System.out.println("Values " + checkTerm[j] + " "+ checkTerm[j+(checkTerm.length-1)/2]);
//                        TermDictionary.Posting pL = new TermDictionary.Posting();
//                        pL.setDocId(Integer.valueOf(checkTerm[j]));
//                        pL.setFreq(Integer.valueOf(checkTerm[j+(checkTerm.length-1)/2]));
//                        term.getPostingList().add(pL);
//                    }
//                    return true;
//                }
//            }
//            // term not found in collection
//            return false;
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }



//    private static void saveLexiconValues(TermQP term, String line) {
//
//        //System.out.println("Saved values");
//    }
//    private static void saveInvertedIndexValues(String line) {
////        String[] parts = line.split(" ");
////        List<Integer> savingList = new ArrayList<>();
////        if (parts.length >= 2) {
////            for (int i = 1; i < parts.length; i++) {
////                savingList.add(Integer.valueOf(parts[i]));
////            }
////            termDocidFreq.put(parts[0], savingList);
////        }
//    }
//    private static void saveDocumentIndexValues(String line){
//        String[] parts = line.split(" ");
//        if (parts.length >= 2) {
//
//        }
//    }

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

    public static void main(String[] args){
        List<TermDictionary> listToFill = new ArrayList<>();
        fillTermDictionary(listToFill, "0");
        System.out.println("AHH");
    }

//    public Map<String, List<Integer>> getTermDocidFreq() {
//        return termDocidFreq;
//    }
//    public Map<String, Integer> getCollectionFrequency() {
//        return collectionFrequency;
//    }
//
//    public Map<String, Integer> getDocumentFrequency() {
//        return this.documentFrequency;
//    }

//    public int getNTotalDocuments() {
//        return nTotalDocuments;
//    }

//    public Map<Integer, Integer> getDocumentLens() {
//        return documentLens;
//    }
}
