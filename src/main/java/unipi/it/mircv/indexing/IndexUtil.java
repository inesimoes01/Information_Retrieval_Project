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

        //System.out.println("this is the block counter bad "+ blockCounter);
        int index = 0;

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
                //index = i + lastDocId + 1;
                // Write information to the bufferedWriter
                bufferedWriter.write(i + " " + documentIndex.getDocumentIndex().get(i));
                bufferedWriter.newLine();  // Move to the next line
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

    public static void lexiconMerge(int blockCounter) {
        int currentOffset = 0;
        int prevOffset = 0;

        File directory = new File("data/output/");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(Paths.PATH_LEXICON_MERGED));
             RandomAccessFile randomAccessFile = new RandomAccessFile(Paths.PATH_INVERTED_INDEX_MERGED, "r");
             BufferedReader bufferedReader = new BufferedReader(new FileReader(Paths.PATH_DOCUMENT_INDEX_MERGED)); ) {

            try (BufferedReader auxFileReader  = new BufferedReader(new FileReader(Paths.PATH_OFFSETS))){
                TreeMap<String, TermStats> termStatsMap = new TreeMap<>();
                PriorityQueue<String> priorityQueue = new PriorityQueue<>(Comparator.naturalOrder());
                BufferedReader[] lexiconReaders = new BufferedReader[blockCounter];
                Iterator<String>[] iterators = new Iterator[blockCounter];

                // Open lexicon readers and create iterators
                for (int i = 0; i < blockCounter; i++) {
                    lexiconReaders[i] = new BufferedReader(new FileReader("data/output/Lexicon" + i + ".txt"));
                    iterators[i] = lexiconReaders[i].lines().iterator();
                }

                // Process lexicon entries in larger batches
                ArrayList<String> batchEntries = new ArrayList<>();
                int batchSize = 10000; // Adjust the batch size based on performance testing

                while (true) {
                    for (int i = 0; i < blockCounter; i++) {
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

                        TermStats termStats = termStatsMap.getOrDefault(term, new TermStats(term, 0, 0, 0));
                        termStats.addToCollectionFrequency(cf);
                        termStats.addToDocumentFrequency(df);
                        termStatsMap.put(term, termStats);
                        if (blockCounter >= 0 && blockCounter < iterators.length) {
                            priorityQueue.add(iterators[blockCounter].next());
                        }
                    }

                    batchEntries.clear();
                }

                double termUpperBound = 0;
                Integer[] offsetsII = new Integer[2];

                for (Map.Entry<String, TermStats> entry : termStatsMap.entrySet()) {
                    String term = entry.getKey();
                    TermStats termStats = entry.getValue();
//                    // Altrimenti, esegui la tua logica per trovare l'offset
//                    if (findOffset(randomAccessFile, term, offset) == 0){
//                        continue;
//                    }
//
//                    offset = findOffset(randomAccessFile, term, offset);

                    termStats.setInvertedIndexOffset(currentOffset);
                    termStats.setTerm(term);

                    randomAccessFile.seek(currentOffset);
                    String line = randomAccessFile.readLine();

                    offsetsII = findInvertedIndexOffset(term);
                    assert offsetsII != null;

                    termUpperBound = computeTermUpperBound(bufferedReader, termStats, offsetsII, currentOffset);

                    bufferedWriter.write(term + " " + termStats.getCollectionFrequency() + " " +
                            termStats.getDocumentFrequency() + " " + termStats.getInvertedIndexOffset()+" "+ termUpperBound + " " + currentOffset +
                            " " + offsetsII[0] + " " + offsetsII[1] + " " + offsetsII[2]);

                    prevOffset = currentOffset;
                    currentOffset = prevOffset + offsetsII[2] + "\n".getBytes().length;

                    bufferedWriter.newLine();

                }

                // Close readers
                for (int i = 0; i < blockCounter; i++) {
                    lexiconReaders[i].close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public static void mergeInvertedIndex(int blockCounter) {
//        String outputPath = Paths.PATH_INVERTED_INDEX_MERGED; // Output file path for merged lexicon
//        blockCounter++;
//        try (FileOutputStream fos = new FileOutputStream(outputPath)) {
//            try (BufferedWriter auxFile = new BufferedWriter(new FileWriter(Paths.PATH_OFFSETS))) {
//                int offsetDocID = 0;
//                int offsetFreq = 0;
//                int endLineOffset = 0;
//                int offsetInvertedIndex = 0;
//                //try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outputPath))) {
//                // TreeMap to store the accumulated statistics for each term (sorted by term)
//                TreeMap<String, ArrayList<Posting>> postingListMap = new TreeMap<>();
//
//                // PriorityQueue to efficiently merge and sort entries
//                PriorityQueue<PostingEntry> priorityQueue = new PriorityQueue<>(Comparator.comparing(PostingEntry::getTerm));
//
//                // Initialize lexicon readers and iterators
//                BufferedReader[] lexiconReaders = new BufferedReader[blockCounter];
//                Iterator<String>[] iterators = new Iterator[blockCounter];
//
//                // Open lexicon readers and create iterators
//                for (int i = 0; i < blockCounter; i++) {
//                    lexiconReaders[i] = new BufferedReader(new FileReader("data/output/InvertedIndex" + i + ".txt"));
//                    iterators[i] = lexiconReaders[i].lines().iterator();
//
//                    if (iterators[i].hasNext()) {
//                        String line = iterators[i].next();
//                        String[] parts = line.split(" ");
//                        String term = parts[0];
//                        ArrayList<Posting> postings = new ArrayList<>();
//
//                        for (int j = 1; j < parts.length; j += 2) {
//                            int docId = Integer.parseInt(parts[j]);
//                            int freq = Integer.parseInt(parts[j + 1]);
//                            postings.add(new Posting(docId, freq));
//                        }
//                        if (postingListMap.containsKey(term)) {
//                            // join all postings
//                            ArrayList<Posting> temp = postingListMap.get(term);
//                            temp.addAll(postings);
//                            postingListMap.put(term, temp);
//                        } else postingListMap.put(term, postings);
//
//                        priorityQueue.add(new PostingEntry(term, i));
//                    }
//                }
//
//                // Continue merging and sorting until the PriorityQueue is empty
//                while (!priorityQueue.isEmpty()) {
//                    PostingEntry entry = priorityQueue.poll();
//                    String term = entry.getTerm();
//                    int blockIndex = entry.getBlockIndex();
//
//                    ArrayList<Posting> postingList = postingListMap.get(term);
//
//                    // Move to the next entry from the same block or fetch new entry from another block
//                    if (iterators[blockIndex].hasNext()) {
//                        String line = iterators[blockIndex].next();
//                        String[] parts = line.split(" ");
//                        String nextTerm = parts[0];
//                        ArrayList<Posting> nextPostings = postingListMap.getOrDefault(nextTerm, new ArrayList<>());
//
//                        for (int j = 1; j < parts.length; j += 2) {
//                            int docId = Integer.parseInt(parts[j]);
//                            int freq = Integer.parseInt(parts[j + 1]);
//                            nextPostings.add(new Posting(docId, freq));
//                        }
//                        Collections.sort(postingList, new Comparator<Posting>() {
//                            @Override
//                            public int compare(Posting p1, Posting p2) {
//                                return Integer.compare(p1.getDocId(), p2.getDocId()); // Ordine crescente
//                            }
//                        });
//
//                        postingListMap.put(nextTerm, nextPostings);
//                        priorityQueue.add(new PostingEntry(nextTerm, blockIndex));
//                    }
//                }
//
//                // Write the merged and sorted entries to the output file
//                for (Map.Entry<String, ArrayList<Posting>> entry : postingListMap.entrySet()) {
//                    String term = entry.getKey();
//                    ArrayList<Posting> postingList2 = entry.getValue();
//
//                    // Creare liste separate per docId e freq
//                    ArrayList<Integer> docIds = new ArrayList<>();
//                    ArrayList<Integer> freqs = new ArrayList<>();
//
//                    // Popolare le liste con i valori corrispondenti
//                    for (Posting p : postingList2) {
//                        docIds.add(p.getDocId());
//                        freqs.add(p.getFreq());
//                    }
//
//                    byte[] compressedDocIds = VariableByte.encode(docIds);
//                    offsetDocID = term.getBytes().length;
//                    byte[] compressedFreq = UnaryInteger.encodeToUnary(freqs);
//                    offsetFreq = offsetDocID + compressedDocIds.length;
//                    endLineOffset = offsetFreq + compressedFreq.length;
//
//                    // write offsets
//                    auxFile.write(term + " " + offsetDocID + " " + offsetFreq + " " + endLineOffset);
//                    auxFile.newLine();
//
//                    // write merged file
//                    fos.write(term.getBytes());
//                    fos.write(compressedDocIds);
//                    fos.write(compressedFreq);
//                    fos.write("\n".getBytes());
//
//                }
//                for (int i = 1; i < blockCounter; i++) {
//                    lexiconReaders[i - 1].close();
//                }
//
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public static byte[] readFromFile(String fileName) throws IOException {
        try (FileInputStream fis = new FileInputStream(fileName)) {
            byte[] data = fis.readAllBytes();

            return data;
        }
    }


    public static void main(String[] args){
        //NewIndex.deleteFilesInFolder("data/output/merged");
        long start = System.currentTimeMillis();
//        mergeDocumentIndex(92);
//        System.gc();
//        Merging.mergeInvertedIndex(93);
//        System.gc();
        Merging.mergeLexicon(93);
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