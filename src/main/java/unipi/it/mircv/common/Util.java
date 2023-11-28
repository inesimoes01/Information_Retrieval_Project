package unipi.it.mircv.common;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;


public class Util {
    private BufferedReader[] lexiconScanners;
    private BufferedReader[] documentIndexReaders;
    private BufferedWriter myWriterDocumentIndex;
    private ArrayList<String> documentIndexEntries;
    public BufferedReader[] getLexiconScanners() {
        return lexiconScanners;
    }
    public void setLexiconScanners(BufferedReader[] lexiconScanners) {
        this.lexiconScanners = lexiconScanners;
    }

    private BufferedReader[] documentIndexScanners;
    public BufferedReader[] getDocumentIndexScanners() {
        return documentIndexScanners;
    }
    public void setDocumentIndexScanners(BufferedReader[] documentIndexScanners) {
        this.documentIndexScanners = documentIndexScanners;
    }

    private double threshold;

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    public double getThreshold() {
        return threshold;
    }

    public static boolean isMemoryFull(double threshold) {

        // Ottieni le informazioni sulla memoria
        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            MemoryUsage usage = pool.getUsage();
            long usedMemory = usage.getUsed();
            long maxMemory = usage.getMax();

            // Calcola la percentuale di memoria utilizzata
            double percentageUsed = (double) usedMemory / maxMemory * 100;

            // Controlla se la percentuale supera il 75%
            if (percentageUsed >= threshold) {
                return true;
            }

        }
        return false;
    }

    public void printUsage() {
        // Get the runtime object
        Runtime runtime = Runtime.getRuntime();

        // Calculate memory usage in bytes
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;

        // Calculate and print the percentage of used memory
        double percentageUsed = ((double) usedMemory / totalMemory) * 100;
        System.out.println("Total Memory: " + totalMemory + " bytes");
        System.out.println("Used Memory: " + usedMemory + " bytes");
        System.out.println("Free Memory: " + freeMemory + " bytes");
        System.out.println("Percentage Used: " + percentageUsed + "%");
    }



    public void writeBlockToDisk(int blockCounter, DocumentIndex documentIndex) {
        String directoryPath = "data/output/";
        String filePath = directoryPath + "DocumentIndex" + blockCounter + ".txt";

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
            ArrayList<Integer> docIndexKey = documentIndex.sortDocumentIndex();
                for (Integer i:docIndexKey) {
                    // Write information to the bufferedWriter
                    bufferedWriter.write(i + " " + documentIndex.getDocumentIndex().get(i));
                    bufferedWriter.newLine();  // Move to the next line
                }



        } catch (IOException e) {
            System.err.println("Error writing to the file:");
            e.printStackTrace();
        }
    }

    public void writeBlockToDisk(int blockCounter, Lexicon lexicon, InvertedIndex invertedIndex) {
        String directoryPath = "data/output/";
        String filePath = directoryPath + "Lexicon"+blockCounter+".txt";
        invertedIndex.sortInvertedIndexByDocId();
        lexicon.sortLexicon();  //why it doesn't

        // Creazione della directory se non esiste
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs(); // Crea le directory necessarie
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath))) {
            // Write File
            ArrayList<String> sortedTerms = new ArrayList<>(lexicon.sortLexicon());
            for (String term : sortedTerms) {
                TermStats termStats = lexicon.getLexicon().get(term);

                // Scrivi il termine seguito dalle statistiche del termine
                bufferedWriter.write(term + "/" + termStats.getCollectionFrequency() + " " + termStats.getDocumentFrequency() + "/");

                // Ottieni la posting list dal tuo inverted index
                ArrayList<Posting> postingList = invertedIndex.getInvertedIndex().get(term);


                for (Posting posting : postingList) {
                    bufferedWriter.write(posting.getDocId() + " " + posting.getFreq() + " ");
                }

                // Vai a capo per il prossimo termine
                bufferedWriter.newLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void readBlockFromDisk(int blockCounter) {
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
                documentIndexReaders[i] = new BufferedReader(new FileReader("data/output/documentIndex" + i + ".txt"));
            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }
        }
    }


    public void mergeDocumentIndex(int blockCounter) {
        myWriterDocumentIndex = null;
        documentIndexEntries = new ArrayList<>();

        // Initialize the writer
        try {
            myWriterDocumentIndex = new BufferedWriter(new FileWriter("data/output/DocumentIndexMerged.txt"));
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }

        // Read from document index files and merge
        try {
            for (int i = 1; i < blockCounter; i++) {
                String line = documentIndexReaders[i].readLine(); // read the first line
                while (line != null) { // continue until the file is not ended
                    documentIndexEntries.add(line);
                    for (int j = 0; j < 2; j++) { // reads other 2 times a line from the current block file and adds it to the list
                        line = documentIndexReaders[i].readLine();
                        documentIndexEntries.add(line);
                    }
                    line = documentIndexReaders[i].readLine();
                }
            }

            // Write the merged entries to the output file
            for (String entry : documentIndexEntries) {
                if (entry != null) {
                    myWriterDocumentIndex.write(entry);
                    myWriterDocumentIndex.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        } finally {
            closeReadersAndWriter(blockCounter);
        }
    }



    public void closeReadersAndWriter(int blockCounter) {
        try {
            for (int i = 0; i < blockCounter; i++) {
                if (documentIndexReaders[i] != null) {
                    documentIndexReaders[i].close();
                }
            }
            if (myWriterDocumentIndex != null) {
                myWriterDocumentIndex.close();
            }
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }





        //myWriterDocIds = new TextWriter("Data/Output/DocIds/docIds" + blockCounter + ".txt");
        //myWriterFreq = new TextWriter("Data/Output/Frequencies/freq" + blockCounter + ".txt");
        //myWriterDocumentIndex = new TextWriter("Data/Output/DocumentIndex/documentIndex" + blockCounter + ".txt");

    }


