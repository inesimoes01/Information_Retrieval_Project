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
            for (Integer i : docIndexKey) {
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
            for (String i : docLexiconKey) {
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
        String outputPath = "data/output/LexiconMerged.txt";
        String invertedIndexPath = "data/output/InvertedIndexMerged.txt";

        File directory = new File("data/output/");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputPath)); RandomAccessFile randomAccessFile = new RandomAccessFile(invertedIndexPath, "r"); ) {

            TreeMap<String, TermStats> termStatsMap = new TreeMap<>();
            PriorityQueue<String> priorityQueue = new PriorityQueue<>(Comparator.naturalOrder());
            BufferedReader[] lexiconReaders = new BufferedReader[blockCounter];
            Iterator<String>[] iterators = new Iterator[blockCounter];

            // Open lexicon readers and create iterators
            for (int i = 1; i < blockCounter; i++) {
                lexiconReaders[i] = new BufferedReader(new FileReader("data/output/Lexicon" + i + ".txt"));
                iterators[i] = lexiconReaders[i].lines().iterator();
            }

            // Process lexicon entries in larger batches
            ArrayList<String> batchEntries = new ArrayList<>();
            int batchSize = 10000; // Adjust the batch size based on performance testing

            while (true) {
                for (int i = 1; i < blockCounter; i++) {
                    if (iterators[i].hasNext()) {
                        batchEntries.add(iterators[i].next());
                    }
                }

                if (batchEntries.isEmpty()) {
                    break;
                }

                // Sort the batch entries
                batchEntries.sort(Comparator.naturalOrder());

                // Merge and update termStatsMap
                for (String currentEntry : batchEntries) {
                    String[] parts = currentEntry.split(" ");
                    String term = parts[0];
                    int cf = Integer.parseInt(parts[1]);
                    int df = Integer.parseInt(parts[2]);

                    TermStats termStats = termStatsMap.getOrDefault(term, new TermStats(0, 0, 0));
                    termStats.addToCollectionFrequency(cf);
                    termStats.addToDocumentFrequency(df);
                    termStatsMap.put(term, termStats);
                    System.out.println(blockCounter);
                    if (blockCounter >= 0 && blockCounter < iterators.length) {
                        priorityQueue.add(iterators[blockCounter].next());
                    }
                }

                batchEntries.clear();
            }
            long offset=0;
            // Write the merged and sorted entries to the output file
            for (Map.Entry<String, TermStats> entry : termStatsMap.entrySet()) {
                String term = entry.getKey();
                TermStats termStats = entry.getValue();
                offset = findOffset(randomAccessFile,term, offset);
                termStats.setInvertedIndexOffset(offset);
                bufferedWriter.write(term + " " + termStats.getCollectionFrequency() + " " + termStats.getDocumentFrequency() + " " + termStats.getInvertedIndexOffset());
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

                for (int i = 1; i < size; i += 2) {
                    Posting tempPosting = new Posting();
                    tempPosting.setDocId(Integer.parseInt(parts[i]));
                    tempPosting.setFreq(Integer.parseInt(parts[i + 1]));
                    postingList.add(tempPosting);

                }

                Collections.sort(postingList, new Comparator<Posting>() {
                    @Override
                    public int compare(Posting p1, Posting p2) {
                        return Integer.compare(p1.getDocId(), p2.getDocId()); // Ordine crescente
                    }
                });

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

                // Creare liste separate per docId e freq
                ArrayList<Integer> docIds = new ArrayList<>();
                ArrayList<Integer> freqs = new ArrayList<>();

                // Popolare le liste con i valori corrispondenti
                for (Posting p : postingList2) {
                    docIds.add(p.getDocId());
                    freqs.add(p.getFreq());
                }

                // Scrivere prima tutti i docId, poi tutte le freq
                bufferedWriter.write(term + " ");
                for (int docId : docIds) {
                    bufferedWriter.write(docId + " ");
                }

                // Aggiungere il separatore
                bufferedWriter.write(" ");

                for (int freq : freqs) {
                    bufferedWriter.write(freq + " ");
                }
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
    public static ArrayList<String> mergeArrayLists(ArrayList<String> list1, ArrayList<String> list2) {
        if (list1 == null || list2 == null) {
            throw new IllegalArgumentException("Le liste non possono essere nulli");
        }

        if (list1.size() != list2.size()) {
            throw new IllegalArgumentException("Le liste devono avere la stessa dimensione");
        }

        ArrayList<String> mergedList = new ArrayList<>();
        for (int i = 0; i < list1.size(); i++) {
            mergedList.add(list1.get(i) + "" + list2.get(i));
        }
        return mergedList;
    }


    public static long findOffset(RandomAccessFile randomAccessFile, String searchTerm, long startOffset) throws IOException {
        long currentOffset = startOffset;
        System.out.println(startOffset);
        System.out.println(searchTerm);
        // Move to the specified start offset
        randomAccessFile.seek(startOffset);

        // Read the line from the current offset
        String line;
        while ((line = randomAccessFile.readLine()) != null) {
            // Increment the current offset by the length of the read line plus a newline character
            currentOffset += line.length() + System.lineSeparator().length();

            // Split the line by spaces and compare the first part with the search term
            String[] parts = line.split("\\s+");
            if (parts.length > 0 && parts[0].equals(searchTerm)) {
                return currentOffset;
            }
        }

        // If the search term is not found, return -1
        return -1;
    }




/*

    public static void main(String[] args) {
        String filePath = "data/output/InvertedIndexMerged.txt";
 // Sostituisci con il numero di riga che vuoi leggere

        try {
            // Apri il file in modalità di sola lettura
            RandomAccessFile file = new RandomAccessFile(filePath, "r");

            // Calcola l'offset in byte per la riga desiderata

            // Posizionati all'offset desiderato nel file
            file.seek(32);

            // Leggi la riga
            String rigaDesiderata = file.readLine();
            System.out.println(rigaDesiderata);

            // Chiudi il file
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/


}