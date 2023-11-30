package unipi.it.mircv.common;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.*;


public class Util {
    private BufferedReader[] lexiconScanners;
    private BufferedReader[] documentIndexReaders;
    private BufferedWriter myWriterDocumentIndex;
    private ArrayList<String> documentIndexEntries;
    private BufferedReader[] lexiconReaders;
    private BufferedWriter myWriterLexicon;
    private ArrayList<String> lexiconEntries;
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

    public void writeBlockToDisk(int blockCounter, Lexicon lexicon) {
        String directoryPath = "data/output/";
        String filePath = directoryPath + "Lexicon" + blockCounter + ".txt";

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
            ArrayList<String> docLexiconKey = lexicon.sortLexicon();
            for (String i:docLexiconKey) {
                // Write information to the bufferedWriter
                bufferedWriter.write(i + " " + lexicon.getLexicon().get(i));
                bufferedWriter.newLine();  // Move to the next line
            }

        } catch (IOException e) {
            System.err.println("Error writing to the file:");
            e.printStackTrace();
        }
    }


    public void writeBlockToDisk(int blockCounter, InvertedIndex invertedIndex) {
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
            for (String i:docLexiconKey) {
                // Write information to the bufferedWriter
                bufferedWriter.write(i + " " + invertedIndex.getInvertedIndex().get(i).toString().replaceAll("[^a-zA-Z0-9\\s]", "")  );
                bufferedWriter.newLine();  // Move to the next line
            }

        } catch (IOException e) {
            System.err.println("Error writing to the file:");
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


    public void lexiconMerge(int blockCounter) {
        // Output file path for merged lexicon
        String outputPath = "data/output/LexiconMerged.txt";

        // Create the directories if they don't exist
        File directory = new File("data/output/");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputPath))) {
            // TreeMap to store the accumulated statistics for each term (sorted by term)
            TreeMap<String, TermStats> termStatsMap = new TreeMap<>();

            // Priority queue to efficiently merge and sort entries
            PriorityQueue<String> priorityQueue = new PriorityQueue<>(Comparator.naturalOrder());

            // Initialize lexicon readers and iterators
            BufferedReader[] lexiconReaders = new BufferedReader[blockCounter];
            Iterator<String>[] iterators = new Iterator[blockCounter];

            // Open lexicon readers and create iterators
            for (int i = 1; i < blockCounter; i++) {
                lexiconReaders[i] = new BufferedReader(new FileReader("data/output/Lexicon" + i + ".txt"));
                iterators[i] = lexiconReaders[i].lines().iterator();
            }

            // Initialize lexicon entries from the first entry of each block
            for (int i = 1; i < blockCounter; i++) {
                if (iterators[i].hasNext()) {
                    priorityQueue.add(iterators[i].next());
                }
            }

            // Continue merging and sorting until the PriorityQueue is empty
            while (!priorityQueue.isEmpty()) {
                String currentEntry = priorityQueue.poll();
                String[] parts = currentEntry.split(" ");

                String term = parts[0];
                int cf = Integer.parseInt(parts[1]);
                int df = Integer.parseInt(parts[2]);

                TermStats termStats = termStatsMap.getOrDefault(term, new TermStats(0, 0));
                termStats.addToCollectionFrequency(cf);
                termStats.addToDocumentFrequency(df);
                termStatsMap.put(term, termStats);

                // Determine the block index for the next entry
                int blockIndex = (priorityQueue.size() % (blockCounter - 1)) + 1;

                // Add the next entry from the corresponding block to the PriorityQueue
                if (iterators[blockIndex].hasNext()) {
                    priorityQueue.add(iterators[blockIndex].next());
                }
            }

            // Write the merged and sorted entries to the output file
            for (Map.Entry<String, TermStats> entry : termStatsMap.entrySet()) {
                String term = entry.getKey();
                TermStats termStats = entry.getValue();

                bufferedWriter.write(term + " " + termStats.getCollectionFrequency() + " " + termStats.getDocumentFrequency());
                bufferedWriter.newLine();
            }

            // Close readers substitute with function!!!!!!
            for (int i = 1; i < blockCounter; i++) {
                lexiconReaders[i].close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void mergeInvertedIndex(int blockCounter) {
        // Output file path for merged lexicon
        String outputPath = "data/output/InvertedIndexMerged.txt";

        // Create the directories if they don't exist
        File directory = new File("data/output/");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputPath))) {
            // TreeMap to store the accumulated statistics for each term (sorted by term)
            TreeMap<String, ArrayList<Posting>> postingListMap = new TreeMap<>();

            // Priority queue to efficiently merge and sort entries
            PriorityQueue<String> priorityQueue = new PriorityQueue<>(Comparator.naturalOrder());

            // Initialize lexicon readers and iterators
            BufferedReader[] lexiconReaders = new BufferedReader[blockCounter];
            Iterator<String>[] iterators = new Iterator[blockCounter];

            // Open lexicon readers and create iterators
            for (int i = 1; i < blockCounter; i++) {
                lexiconReaders[i] = new BufferedReader(new FileReader("data/output/InvertedIndex" + i + ".txt"));
                iterators[i] = lexiconReaders[i].lines().iterator();
            }

            // Initialize lexicon entries from the first entry of each block
            for (int i = 1; i < blockCounter; i++) {
                if (iterators[i].hasNext()) {
                    priorityQueue.add(iterators[i].next());
                }
            }

            // Continue merging and sorting until the PriorityQueue is empty
            while (!priorityQueue.isEmpty()) {
                String currentEntry = priorityQueue.poll();
                String[] parts = currentEntry.split(" "); //reading line
                int size = parts.length;
                String term = parts[0];
                ArrayList<Posting> postingList = new ArrayList<>();



                postingList = postingListMap.getOrDefault(term, postingList); //get the value otherwise it returns an empty postingList

                for (int i=1; i<size ;i+=2){
                    Posting tempPosting = new Posting();
                    tempPosting.setDocId(Integer.parseInt(parts[i]));
                    tempPosting.setFreq(Integer.parseInt(parts[i+1]));
                    postingList.add(tempPosting);

                }

                postingListMap.put(term, postingList);

                // Determine the block index for the next entry
                int blockIndex = (priorityQueue.size() % (blockCounter - 1)) + 1;

                // Add the next entry from the corresponding block to the PriorityQueue
                if (iterators[blockIndex].hasNext()) {
                    priorityQueue.add(iterators[blockIndex].next());
                }
            }

            // Write the merged and sorted entries to the output file
            for (Map.Entry<String, ArrayList<Posting>> entry : postingListMap.entrySet()) {
                String term = entry.getKey();
                ArrayList<Posting> postingList2 = entry.getValue();

                bufferedWriter.write(term + " "+ postingList2.toString().replaceAll("[^a-zA-Z0-9\\s]", ""));
                bufferedWriter.newLine();
            }

            // Close readers
            for (int i = 1; i < blockCounter; i++) {
                lexiconReaders[i].close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





        //myWriterDocIds = new TextWriter("Data/Output/DocIds/docIds" + blockCounter + ".txt");
        //myWriterFreq = new TextWriter("Data/Output/Frequencies/freq" + blockCounter + ".txt");
        //myWriterDocumentIndex = new TextWriter("Data/Output/DocumentIndex/documentIndex" + blockCounter + ".txt");

    }


