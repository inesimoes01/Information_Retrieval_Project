package unipi.it.mircv.indexing;

import unipi.it.mircv.common.Paths;
import unipi.it.mircv.indexing.dataStructures.*;

import java.io.*;
import java.util.*;

import static unipi.it.mircv.common.Util.*;

public class IndexUtil {

    private static BufferedReader[] lexiconScanners;
    private static BufferedReader[] documentIndexReaders;
    private static BufferedWriter myWriterDocumentIndex;
    private static ArrayList<String> documentIndexEntries;

    public static void writeBlockToDisk(int blockCounter, DocumentIndex documentIndex, int lastDocId) {
        String directoryPath = "data/output/";
        String filePath = directoryPath + "DocumentIndex" + blockCounter + ".txt";

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
            for (Integer i : documentIndex.getDocumentIndex().keySet()){
                bufferedWriter.write(i + " " + documentIndex.getDocumentIndex().get(i));
                bufferedWriter.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to the file:");
            e.printStackTrace();
        }
    }


    public static void writeBlockToDisk(int blockCounter, Lexicon lexicon, int lastDocId) {
        String directoryPath = "data/output/";
        String filePath = directoryPath + "Lexicon" + blockCounter + ".txt";

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

    public static void writeBlockToDisk(int blockCounter, InvertedIndex invertedIndex, int lastDocId) {
        String directoryPath = "data/output/";
        String filePath = directoryPath + "InvertedIndex" + blockCounter + ".txt";

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

    public static void readBlockFromDisk(int blockCounter) {
        lexiconScanners = new BufferedReader[blockCounter];
        documentIndexReaders = new BufferedReader[blockCounter];

        // Open lexicon scanners
        for (int i = 1; i < blockCounter; i++) {
            try {
                lexiconScanners[i] = new BufferedReader(new FileReader("data/output/Lexicon" + i + ".txt"));
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
        }

        // Open document index scanners
        for (int i = 1; i < blockCounter; i++) {
            try {
                documentIndexReaders[i] = new BufferedReader(new FileReader("data/output/DocumentIndex" + i + ".txt"));
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
        }
    }

    public static void mergeDocumentIndex(int blockCounter) {
        myWriterDocumentIndex = null;
        documentIndexEntries = new ArrayList<>();
        double avgLen = 0.00;
        int count=0;
        int totLen=0;

        documentIndexReaders = new BufferedReader[blockCounter];
        // initialize document index reader
        for (int i = 0; i < blockCounter; i++) {
            try {
                documentIndexReaders[i] = new BufferedReader(new FileReader("data/output/DocumentIndex" + i + ".txt"));
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
        }


        // Read from document index files and merge
        try(BufferedWriter myWriterDocumentIndex = new BufferedWriter(new FileWriter(Paths.PATH_DOCUMENT_INDEX_MERGED))) {
            for (int i = 0; i < blockCounter; i++) {
                String line = documentIndexReaders[i].readLine(); // read the first line
                while (line != null) {
                    documentIndexEntries.add(line);
                    // Add logging statements to print document details
                    for (int j = 0; j < 2; j++) {
                        line = documentIndexReaders[i].readLine();
                        documentIndexEntries.add(line);
                        // Add logging statements to print additional lines of the document
                    }
                    line = documentIndexReaders[i].readLine();
                }
            }

            // Write the merged entries to the output file
            for (String entry : documentIndexEntries) {
                if (entry != null) {
                    String[] parts = entry.split("\\s+");
                    count++;
                    totLen += Integer.parseInt(parts[1]);
                    myWriterDocumentIndex.write(entry);
                    myWriterDocumentIndex.newLine();
                }
            }
            avgLen= (double) totLen /count;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/output/avgDocLen.txt"))) {
                // Scrivi la stringa nel file
                writer.write(avgLen +"");
                writer.newLine();
                writer.write(count +"");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }



    public static void main(String[] args){
        //NewIndex.deleteFilesInFolder("data/output/merged");
        long start = System.currentTimeMillis();
        mergeDocumentIndex(97);
//        System.gc();
        //Merging.mergeInvertedIndex(95);
//        System.gc();
       // Merging.mergeLexicon(95);
        System.out.println("Merging took " + (System.currentTimeMillis() - start)/1000 + "s");

        int offsetDoc = 0;
        int offsetFreq = 0;

//        try (BufferedReader reader = new BufferedReader(new FileReader(Paths.PATH_OFFSETS))) {
//            String line;
//
//            while ((line = reader.readLine()) != null) {
//                String[] parts = line.split(" ");
//                if (parts[0].equals("0")){
//                    offsetDoc = Integer.parseInt(parts[1]);
//                    offsetFreq = Integer.parseInt(parts[2]);
//                }
//            }
//        }catch (IOException e) {
//            System.err.println("An error occurred while reading from the file: " + e.getMessage());
//        }

//        try (BufferedReader reader = new BufferedReader(new FileReader("data/output/merged/InvertedIndexMerged.txt"))) {
//            String line;
//
//            while ((line = reader.readLine()) != null) {
//                byte[] encodedBytes = encodedData.getBytes(StandardCharsets.ISO_8859_1); // Use ISO_8859_1 to preserve byte values
//                List<Integer> docIds = VariableByte.decode(encodedBytes);
//                List<Integer> termFrequencies = UnaryInteger.decodeFromUnary(encodedBytes, docIds.size());
//
//                System.out.println("Term: " + term);
//                System.out.println("Doc IDs: " + docIds);
//                System.out.println("Term Frequencies: " + termFrequencies);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        try {byte[] data = readFromFile("data/output/merged/InvertedIndexMerged.txt");
//            String content = new String(data);
//            System.out.println("Content read from file:");
//            String[] parts = content.split("///");
//
//            System.out.println("term" + parts[0]);
//            System.out.println(parts[1]);
//            System.out.println("docid" + VariableByte.decode(parts[1].getBytes()));
//
//            //System.out.println(content);
//        } catch (IOException e) {
//            System.err.println("An error occurred while reading from the file: " + e.getMessage());
//        }
    }

}