package unipi.it.mircv.common;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;


public class Util {

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

    public void  writeBlockToDisk(int blockCounter, String encodingType, Lexicon lexicon) {
        String directoryPath = "data/output/";
        String filePath = directoryPath + "Lexicon.txt";

        // Create the directories if they don't exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs(); // Creates parent directories as needed
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath))) {
            // Writing to the file
            bufferedWriter.write(lexicon.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void  writeBlockToDisk(int blockCounter, String encodingType, DocumentIndex documentIndex) {
        String directoryPath = "data/output/";
        String filePath = directoryPath + "DocumentIndex.txt";

        // Create the directories if they don't exist
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs(); // Creates parent directories as needed
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath))) {
            // Writing to the file
            bufferedWriter.write(documentIndex.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        public void writeBlockToDisk (int blockCounter, String encodingType, InvertedIndex invertedIndex){
            String directoryPath = "data/output/";
            String filePath = directoryPath + "InvertedIndex.txt";

            // Create the directories if they don't exist
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                directory.mkdirs(); // Creates parent directories as needed
            }

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath))) {
                // Writing to the file
                bufferedWriter.write(invertedIndex.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    public void writeBlockToDisk(int blockCounter, String encodingType, Lexicon lexicon, InvertedIndex invertedIndex) {
        String directoryPath = "data/output/";
        String filePath = directoryPath + "Lexicon.txt";
        invertedIndex.sortInvertedIndexByDocId();
        lexicon.sortLexicon();  //why it doesn't 

        // Creazione della directory se non esiste
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdirs(); // Crea le directory necessarie
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filePath))) {
            // Scrittura nel file

            for (String term : lexicon.getLexicon().keySet()) {
                TermStats termStats = lexicon.getLexicon().get(term);

                // Scrivi il termine seguito dalle statistiche del termine
                bufferedWriter.write(term + "/" + termStats.getCollectionFrequency() + " " + termStats.getDocumentFrequency() + "/");

                // Ottieni la posting list dal tuo inverted index
                ArrayList<Posting> postingList = invertedIndex.getInvertedIndex().get(term);

                // Scrivi le informazioni sulla posting list
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




        //myWriterDocIds = new TextWriter("Data/Output/DocIds/docIds" + blockCounter + ".txt");
        //myWriterFreq = new TextWriter("Data/Output/Frequencies/freq" + blockCounter + ".txt");
        //myWriterDocumentIndex = new TextWriter("Data/Output/DocumentIndex/documentIndex" + blockCounter + ".txt");

    }


