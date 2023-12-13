package unipi.it.mircv.queryProcessing;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class OutputResultsReader {

    private static int nTotalDocuments;

    public static int getnTotalDocuments() {
        return nTotalDocuments;
    }

    private static final Path PATH_LEXICON = Paths.get("data/output/LexiconMerged.txt");
    private static final Path PATH_INVERTED_INDEX = Paths.get("data/output/InvertedIndexMerged.txt");
    private static final Path PATH_DOCUMENT_INDEX = Paths.get("data/output/DocumentIndexMerged.txt");

    public static TermDictionary fillTermDictionary(List<TermDictionary> listToFill, String queryTerm){
        TermDictionary term = new TermDictionary();

        // verifies if term exists in collection
        // saves term, CollectionFrequency, DocumentFrequency, TermUpperBounds, Offset
        if (!searchTermInLexicon(term, queryTerm)) return null;

        // saves PostingList (DocId Freq)
        if (!searchTermInInvertedIndex(term)) return null;

        // para cada docid, criar um DocumentQP
        for (TermDictionary.Posting posting : term.getPostingList()) {
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
            List<String> lines = Files.readAllLines(PATH_LEXICON, StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split(" ");
                if (parts.length >= 2 && parts[0].equalsIgnoreCase(queryTerm)) {
                    //boolean termExistsInCollection = true;
                    term.setTerm(queryTerm);
                    term.setCollectionFrequency(Integer.parseInt(parts[1].trim()));
                    term.setDocumentFrequency(Integer.parseInt(parts[2].trim()));
                    //System.out.println("Found term in Lexicon");
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
    private static boolean searchTermInInvertedIndex(TermDictionary term){
        try {
            List<String> lines = Files.readAllLines(PATH_INVERTED_INDEX, StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] checkTerm = line.split(" ");
                if (checkTerm[0].equalsIgnoreCase(term.getTerm())){
                    String[] parts = line.split("  ");

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
                        TermDictionary.Posting pL = new TermDictionary.Posting();
                        pL.setFreq(frequencies[i]);
                        pL.setDocId(docIds[i]);
                        term.getPostingList().add(pL);
                        //System.out.println("Posting list " + term.getTerm() + " " + frequencies[i] + " " + docIds[i]);
                    }
                    return true;
                }


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

//    public int getNTotalDocuments() {
//        return nTotalDocuments;
//    }

//    public Map<Integer, Integer> getDocumentLens() {
//        return documentLens;
//    }
}
