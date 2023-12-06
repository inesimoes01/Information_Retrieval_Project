package unipi.it.mircv.queryProcessing;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class OutputResultsReader {


    public static int getnTotalDocuments() {
        return nTotalDocuments;
    }

    //    private Map<String, List<Integer>> termDocidFreq = new HashMap<>();
//    // Map<term, List<DocId Freq DocId Freq DocId Freq...>>
//    private static Map<String, Integer> collectionFrequency = new HashMap<>(); // not being used
//    // Map<term, TimesTermAppearsInCollection>
//    // how many times the term appear in the collection
//    private static Map<String, Integer> documentFrequency = new HashMap<>();
//    // Map<term, HowManyTimesTermAppearsInDocuments>
//    // how many documents the term appears in
//    private static Map<Integer, Integer> documentLens = new HashMap<>();
//    // Map<DocId, LengthOfDocument>
    private static int nTotalDocuments;

    public boolean isTermExistsInCollection() {
        return termExistsInCollection;
    }

    // number of total documents
    private static boolean termExistsInCollection;
    private static final Path PATH_LEXICON = Paths.get("data/output/LexiconMerged.txt");
    private static final Path PATH_INVERTED_INDEX = Paths.get("data/output/InvertedIndexMerged.txt");
    private static final Path PATH_DOCUMENT_INDEX = Paths.get("data/output/DocumentIndexMerged.txt");

    public static TermQP fillTermQP(List<TermQP> listToFill, String queryTerm){
        TermQP term = new TermQP();
        // verifies if term exists, saves it in the TermQP and saves the other values
        // return false if the value does not exist in collection
        if (!searchTermInLexicon(term, queryTerm)) return null;
        // saves other values
        if (!searchTermInInvertedIndex(term)) return null;

        // para cada docid, criar um DocumentQP
        for (Integer docid : term.getDocIdFreq().keySet()){
            DocumentQP doc = new DocumentQP();
            doc.setDocId(docid);
            searchDocIdInDocumentIndex(doc);
            term.setDocumentsWithTerm(doc);
        }


        // fill the list
        listToFill.add(term);
        return term;
    }
//    public static DocumentQP fillDocumentQP(Integer docId){
//
//        return doc;
//    }


    // saves Collection Frequency and Document Frequency
    private static boolean searchTermInLexicon(TermQP term, String queryTerm){
        try {
            List<String> lines = Files.readAllLines(PATH_LEXICON, StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split(" ");
                if (parts.length >= 2 && parts[0].equalsIgnoreCase(queryTerm)) {
                    termExistsInCollection = true;
                    term.setTerm(queryTerm);
                    term.setCollectionFrequency(Integer.parseInt(parts[1].trim()));
                    term.setDocumentFrequency(Integer.parseInt(parts[2].trim()));
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
            List<String> lines = Files.readAllLines(PATH_DOCUMENT_INDEX, StandardCharsets.UTF_8);
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

    // saves DocId and Freq
    private static boolean searchTermInInvertedIndex(TermQP term){
        try {
            List<String> lines = Files.readAllLines(PATH_INVERTED_INDEX, StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split("/");

                String[] docIdStr = parts[0].trim().split("\\s+");
                String[] frequenciesStr = parts[1].trim().split("\\s+");

                int[] frequencies = new int[frequenciesStr.length];
                for (int i = 0; i < frequenciesStr.length; i++) {
                    frequencies[i] = Integer.parseInt(frequenciesStr[i]);
                }

                int[] docIds = new int[docIdStr.length - 1]; // -1 to ignore the initial "field"
                for (int i = 1; i < docIdStr.length; i++) {
                    docIds[i - 1] = Integer.parseInt(docIdStr[i]);
                }

                for (int i = 0; i < docIds.length; i++) {
                    term.getDocIdFreq().put(docIds[i], frequencies[i]);
                }
                return true;

            }
            // term not found in collection
            return false;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



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
        List<String> allLines = Files.readAllLines(PATH_DOCUMENT_INDEX, StandardCharsets.UTF_8);

        // get last line from file
        if (!allLines.isEmpty()) {
            String lastLine = allLines.get(allLines.size() - 1);
            nTotalDocuments = Integer.parseInt(lastLine.split(" ")[0]);
        } else {
            System.out.println("The file is empty.");
        }
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

    public int getNTotalDocuments() {
        return nTotalDocuments;
    }

//    public Map<Integer, Integer> getDocumentLens() {
//        return documentLens;
//    }
}
