package unipi.it.mircv.queryProcessing;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class OutputResultsReader {
    private Map<String, List<Integer>> termDocidFreq = new HashMap<>();
    private static Map<String, Integer> collectionFrequency = new HashMap<>(); // not being used
    private static Map<String, Integer> documentFrequency = new HashMap<>();

    private static Map<Integer, Integer> documentLens = new HashMap<>();
    private int nTotalDocuments;

    // number of total documents

    private final Path PATH_LEXICON = Paths.get("data/output/LexiconMerged.txt");
    private final Path PATH_INVERTED_INDEX = Paths.get("data/output/InvertedIndexMerged.txt");
    private final Path PATH_DOCUMENT_INDEX = Paths.get("data/output/DocumentIndexMerged.txt");

    public boolean searchTermInLexicon(String term, boolean saveValues){
        try {
            List<String> lines = Files.readAllLines(PATH_LEXICON, StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split(" ");
                if (parts.length >= 2 && parts[0].equalsIgnoreCase(term)) {
                    //System.out.println("Line: " + i);
                    if (saveValues) saveLexiconValues(line);
                    return true; // found the term in this file
                }
            }
            return false; // term not found in this file

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void searchDocIdInDocumentIndex(Integer docid){
        try {
            List<String> lines = Files.readAllLines(PATH_DOCUMENT_INDEX, StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split(" ");
                if (parts.length >= 2 && parts[0].equalsIgnoreCase(String.valueOf(docid))) {
                    //System.out.println("Line: " + i);
                    saveDocumentIndexValues(line);
                    return; // found the term in this file
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean searchTermInInvertedIndex(String term){
        try {
            List<String> lines = Files.readAllLines(PATH_INVERTED_INDEX, StandardCharsets.UTF_8);
            for (String line : lines) {
                String[] parts = line.split(" ");

                // term found in collection
                if (parts.length >= 2 && parts[0].equalsIgnoreCase(term)) {
                    saveInvertedIndexValues(line);
                    return true;
                }
            }
            // term not found in collection
            return false;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    private void saveLexiconValues(String line) {
        String[] parts = line.split(" ");
        if (parts.length >= 2) {
            collectionFrequency.put(parts[0], Integer.parseInt(parts[1].trim()));
            documentFrequency.put(parts[0], Integer.parseInt(parts[2].trim()));
        }
        //System.out.println("Saved values");
    }
    private void saveInvertedIndexValues(String line) {
        String[] parts = line.split(" ");
        List<Integer> savingList = new ArrayList<>();
        if (parts.length >= 2) {
            for (int i = 1; i < parts.length; i++) {
                savingList.add(Integer.valueOf(parts[i]));
            }
            termDocidFreq.put(parts[0], savingList);
        }
    }

    private void saveDocumentIndexValues(String line){
        String[] parts = line.split(" ");
        if (parts.length >= 2) {
            documentLens.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }
    }

    public void saveTotalNumberDocs() throws IOException {
        List<String> allLines = Files.readAllLines(PATH_DOCUMENT_INDEX, StandardCharsets.UTF_8);

        // get last line from file
        if (!allLines.isEmpty()) {
            String lastLine = allLines.get(allLines.size() - 1);
            nTotalDocuments = Integer.parseInt(lastLine.split(" ")[0]);
        } else {
            System.out.println("The file is empty.");
        }
    }

    public Map<String, List<Integer>> getTermDocidFreq() {
        return termDocidFreq;
    }
    public Map<String, Integer> getCollectionFrequency() {
        return collectionFrequency;
    }

    public Map<String, Integer> getDocumentFrequency() {
        return documentFrequency;
    }

    public int getNTotalDocuments() {
        return nTotalDocuments;
    }

    public Map<Integer, Integer> getDocumentLens() {
        return documentLens;
    }
}
