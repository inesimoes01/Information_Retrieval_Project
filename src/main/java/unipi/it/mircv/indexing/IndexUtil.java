package unipi.it.mircv.indexing;

import unipi.it.mircv.common.Paths;
import unipi.it.mircv.common.dataStructures.DocumentIndex;
import unipi.it.mircv.common.dataStructures.InvertedIndex;
import unipi.it.mircv.common.dataStructures.Lexicon;

import java.io.*;
import java.util.*;

public class IndexUtil {

    public static void writeBlockToDisk(int blockCounter, DocumentIndex documentIndex) {
        String directoryPath = Paths.PATH_OUTPUT_FOLDER;
        String filePath = Paths.PATH_DOCUMENT_INDEX + blockCounter + ".txt";

        File directory = new File(directoryPath);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Directories created successfully.");
            } else {
                System.err.println("Failed to create directories.");
                return;
            }
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath))) {
            for (Integer i : documentIndex.getDocumentIndex().keySet()) {
                bufferedWriter.write(i + " " + documentIndex.getDocumentIndex().get(i));
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to the file:");
            e.printStackTrace();
        }
    }


    public static void writeBlockToDisk(int blockCounter, Lexicon lexicon) {
        String directoryPath = Paths.PATH_OUTPUT_FOLDER;
        String filePath = Paths.PATH_LEXICON + blockCounter + ".txt";

        // Create the directories if they don't exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Directories created successfully.");
            } else {
                System.err.println("Failed to create directories.");
                return;
            }
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath))) {
            ArrayList<String> docLexiconKey = lexicon.sortLexicon();
            for (String i : docLexiconKey) {
                bufferedWriter.write(i + " " + lexicon.getLexicon().get(i).getCollectionFrequency() + " " + lexicon.getLexicon().get(i).getDocumentFrequency());
                bufferedWriter.newLine();
            }

        } catch (IOException e) {
            System.err.println("Error writing to the file:");
            e.printStackTrace();
        }
    }

    public static void writeBlockToDisk(int blockCounter, InvertedIndex invertedIndex) {
        String directoryPath = Paths.PATH_OUTPUT_FOLDER;
        String filePath = Paths.PATH_INVERTED_INDEX + blockCounter + ".txt";

        // Create the directories if they don't exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            if (directory.mkdirs()) {
                System.out.println("Directories created successfully.");
            } else {
                System.err.println("Failed to create directories.");
                return;  // Exit the method if directories cannot be created
            }
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath))) {
            // Writing to the file
            invertedIndex.sortPostingList();
            ArrayList<String> docLexiconKey = invertedIndex.sortInvertedIndexByTerm();
            for (String i : docLexiconKey) {
                // Write information to the bufferedWriter
                bufferedWriter.write(i + " " + invertedIndex.getInvertedIndex().get(i).toString().replaceAll("[^a-zA-Z0-9\\s]", ""));
                bufferedWriter.newLine();  // Move to the next line
            }

        } catch (IOException e) {
            System.err.println("Error writing to the file:");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        //NewIndex.deleteFilesInFolder("data/output/merged");
        long start = System.currentTimeMillis();
        //mergeDocumentIndex(97);
//        System.gc();
        //Merging.mergeInvertedIndex(95);
//        System.gc();
        Merging.mergeLexicon(90);
        System.out.println("Merging took " + (System.currentTimeMillis() - start) / 1000 + "s");

    }
}