package unipi.it.mircv.common;

import unipi.it.mircv.indexing.dataStructures.*;

import java.io.*;
import java.util.*;

import static unipi.it.mircv.common.Util.*;

public class IndexUtil {

        private static BufferedReader[] lexiconScanners;
        private static BufferedReader[] documentIndexReaders;
        private BufferedWriter myWriterDocumentIndex;
        private ArrayList<String> documentIndexEntries;

        /**
         * Writes a block of DocumentIndex to disk.
         *
         * @param blockCounter   The block number.
         * @param documentIndex  The DocumentIndex to be written.
         */
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


        /**
         * Writes a block of Lexicon to disk.
         *
         * @param blockCounter The block number.
         * @param lexicon      The Lexicon to be written.
         */
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
                    System.out.println(i + " " + lexicon.getLexicon().get(i));
                    bufferedWriter.write(i + " " + lexicon.getLexicon().get(i));
                    bufferedWriter.newLine();  // Move to the next line
                }

            } catch (IOException e) {
                System.err.println("Error writing to the file:");
                e.printStackTrace();
            }
        }



        /**
         * Writes a block of InvertedIndex to disk.
         *
         * @param blockCounter   The block number.
         * @param invertedIndex  The InvertedIndex to be written.
         */
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

        /**
         * Reads a block from disk and initializes scanners for lexicon and document index.
         *
         * @param blockCounter The block number.
         */
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
                    documentIndexReaders[i] = new BufferedReader(new FileReader("data/output/documentIndex" + i + ".txt"));
                } catch (IOException e) {
                    e.printStackTrace(); // Handle the exception appropriately
                }
            }
        }

        /**
         * Merges DocumentIndex entries from multiple blocks and writes to a merged file.
         *
         * @param blockCounter The total number of blocks.
         */
        public void mergeDocumentIndex(int blockCounter) {
            myWriterDocumentIndex = null;
            documentIndexEntries = new ArrayList<>();
            double avgLen = 0.00;
            int count=0;
            int totLen=0;
            // Initialize the writer


            // Read from document index files and merge
            try(BufferedWriter myWriterDocumentIndex = new BufferedWriter(new FileWriter("data/output/DocumentIndexMerged.txt"))) {
                for (int i = 1; i < blockCounter; i++) {
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
                        String parts[] = entry.split("\\s+");
                        count++;
                        totLen += Integer.parseInt(parts[1]);
                        myWriterDocumentIndex.write(entry);
                        myWriterDocumentIndex.newLine();
                    }
                }
                avgLen=totLen/count;
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

        /**
         * Merges Lexicon entries from multiple blocks and writes to a merged file.
         *
         * @param blockCounter The total number of blocks.
         */
        public void lexiconMerge(int blockCounter) {
            String outputPath = "data/output/LexiconMerged.txt";
            String invertedIndexPath = "data/output/InvertedIndexMerged.txt";

            File directory = new File("data/output/");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputPath)); RandomAccessFile randomAccessFile = new RandomAccessFile(invertedIndexPath, "r"); BufferedReader bufferedReader = new BufferedReader(new FileReader("data/output/DocumentIndexMerged.txt")); ) {

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
                        if (blockCounter >= 0 && blockCounter < iterators.length) {
                            priorityQueue.add(iterators[blockCounter].next());
                        }
                    }

                    batchEntries.clear();
                }
                long offset=0;
                long prevoffset=0;
                double termUpperBound = 0;
                // Write the merged and sorted entries to the output file

                for (Map.Entry<String, TermStats> entry : termStatsMap.entrySet()) {
                    String term = entry.getKey();
                    TermStats termStats = entry.getValue();
                    // Altrimenti, esegui la tua logica per trovare l'offset

                    offset = findOffset(randomAccessFile, term, offset) ;
                    termStats.setInvertedIndexOffset(prevoffset);


                    randomAccessFile.seek(prevoffset);
                    String line = randomAccessFile.readLine();

                    termUpperBound = computeTermUpperBound(bufferedReader, splitInvertedIndexLine(line), termStats);

                    bufferedWriter.write(term + " " + termStats.getCollectionFrequency() + " " +
                            termStats.getDocumentFrequency() + " " + termStats.getInvertedIndexOffset()+" "+ termUpperBound);
                    prevoffset = offset;
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

    /**
     * Merges InvertedIndex entries from multiple blocks and writes to a merged file.
     *
     * @param blockCounter The total number of blocks.
     */
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
    }

