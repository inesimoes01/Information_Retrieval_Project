package unipi.it.mircv.indexing;

import org.apache.commons.io.FileUtils;
import unipi.it.mircv.common.Paths;
import unipi.it.mircv.compression.UnaryInteger;
import unipi.it.mircv.compression.VariableByte;
import unipi.it.mircv.indexing.dataStructures.LexiconEntry;
import unipi.it.mircv.indexing.dataStructures.Posting;
import unipi.it.mircv.indexing.dataStructures.PostingEntry;
import unipi.it.mircv.indexing.dataStructures.TermStats;
import unipi.it.mircv.queryProcessing.Ranking;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.io.*;

public class Merging {
    public static void mergeLexicon(int blockCounter){
        File directory = new File("data/output/");
        if (!directory.exists()) { directory.mkdirs(); }

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(Paths.PATH_LEXICON_MERGED));
            RandomAccessFile rafInvertedIndex = new RandomAccessFile(new File(Paths.PATH_INVERTED_INDEX_MERGED), "rw")){

            TreeMap<String, TermStats> termStatsMap = new TreeMap<>();
            BufferedReader[] lexiconReaders = new BufferedReader[blockCounter];
            Iterator<String>[] iterators = new Iterator[blockCounter];

            PriorityQueue<LexiconEntry> priorityQueue = new PriorityQueue<>(Comparator.comparing(LexiconEntry::getTerm));

            TreeMap<Integer, Integer> documentIndexTree = readDocumentIndex();
            TreeMap<String, long[]> offSetTree = readOffsetFile();

            // open lexicon readers and create iterators
            for (int i = 0; i < blockCounter; i++) {
                lexiconReaders[i] = new BufferedReader(new FileReader("data/output/Lexicon" + i + ".txt"));
                iterators[i] = lexiconReaders[i].lines().iterator();

                if(iterators[i].hasNext()){
                    String line = iterators[i].next();
                    addTermToQueueL(line, i, termStatsMap, priorityQueue);
                }
            }

            while (!priorityQueue.isEmpty()) {
                LexiconEntry currentEntry = priorityQueue.poll();
                String term_aux = currentEntry.getTerm();
                int cf_aux = currentEntry.getCf();
                int df_aux = currentEntry.getDf();
                int blockIndex_aux = currentEntry.getBlockIndex();

                // merge term statistics
                if(iterators[blockIndex_aux].hasNext()){
                    String line = iterators[blockIndex_aux].next();
                    addTermToQueueL(line, blockIndex_aux, termStatsMap, priorityQueue);
                }

//                // add the next entry from the same iterator to the priority queue
//                if (iterators[currentEntry.getIteratorIndex()].hasNext()) {
//                    String line = iterators[currentEntry.getIteratorIndex()].next();
//                    priorityQueue.add(new LexiconEntry(line, currentEntry.getIteratorIndex()));
//                }
            }

            for (Map.Entry<String, TermStats> entry : termStatsMap.entrySet()) {
                String term_aux = entry.getKey();
                TermStats termStats_aux = entry.getValue();

                // calculate offset values and update term upper bound
                //long[] offsetsII = findInvertedIndexOffset(term_aux);
                long[] offsetsII = offSetTree.get(term_aux);
                assert offsetsII != null;
                double termUpperBound = computeTermUpperBound(rafInvertedIndex, termStats_aux, offsetsII, documentIndexTree);

                bufferedWriter.write(String.format("%s %d %d %d %.2f %d %d %d",
                        term_aux, termStats_aux.getCollectionFrequency(), termStats_aux.getDocumentFrequency(),
                        termStats_aux.getInvertedIndexOffset(), termUpperBound,
                        offsetsII[0], offsetsII[1], offsetsII[2]));
                bufferedWriter.newLine();

            }

            // close lexicon readers
            for (BufferedReader reader : lexiconReaders) {
                reader.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void mergeInvertedIndex(int blockCounter) {
        String outputPath = Paths.PATH_INVERTED_INDEX_MERGED; // Output file path for merged lexicon

        try (RandomAccessFile raf = new RandomAccessFile(new File(outputPath), "rw");
             BufferedWriter auxFile = new BufferedWriter(new FileWriter(Paths.PATH_OFFSETS))) {

            TreeMap<String, ArrayList<Posting>> postingListMap = new TreeMap<>();
            PriorityQueue<PostingEntry> priorityQueue = new PriorityQueue<>(Comparator.comparing(PostingEntry::getTerm));

            BufferedReader[] lexiconReaders = new BufferedReader[blockCounter];
            Iterator<String>[] iterators = new Iterator[blockCounter];

            HashMap<String, long[]> offsetsHelper = new HashMap<>();

            // initialize the iterators with the first line of each block
            for (int i = 0; i < blockCounter; i++) {
                lexiconReaders[i] = new BufferedReader(new FileReader("data/output/InvertedIndex" + i + ".txt"));
                iterators[i] = lexiconReaders[i].lines().iterator();

                if (iterators[i].hasNext()) {
                    String line = iterators[i].next();
                    addTermToQueueII(line, i, postingListMap, priorityQueue);
                }
            }

            String prevTerm = " ";
            int lastOffset = 0;
            while (!priorityQueue.isEmpty()) {
                PostingEntry entry_aux = priorityQueue.poll();
                String term_aux = entry_aux.getTerm();
                int blockIndex_aux = entry_aux.getBlockIndex();

                ArrayList<Posting> postingList = postingListMap.get(term_aux);
                if (iterators[blockIndex_aux].hasNext()) {
                    String line = iterators[blockIndex_aux].next();
                    addTermToQueueII(line, blockIndex_aux, postingListMap, priorityQueue);
                }

                // if the current term is equal to the previous term, merge posting lists together
                if (!prevTerm.equals(term_aux)){
                    writeMergedPostings(term_aux, postingList, raf, auxFile, offsetsHelper);
                    postingListMap.remove(term_aux);
                }
                // if the current term is different from the previous term, write it to the disk and update offsets
                prevTerm = term_aux;
            }

            for (Map.Entry<String, ArrayList<Posting>> entry : postingListMap.entrySet()) {
                String term_aux = entry.getKey();
                ArrayList<Posting> postingList = entry.getValue();
                writeMergedPostings(term_aux, postingList, raf, auxFile, offsetsHelper);
            }

            writeOffsets(offsetsHelper, auxFile);

            for (BufferedReader reader : lexiconReaders) {
                if (reader != null) {
                    reader.close();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeOffsets(HashMap<String, long[]> offsetsHelper, BufferedWriter auxFile) throws IOException {
        TreeMap<String, long[]> sortedMap = new TreeMap<>(offsetsHelper);

        // Display the sorted map
        for (Map.Entry<String, long[]> entry : sortedMap.entrySet()) {
            String term = entry.getKey();
            long[] offsets = entry.getValue();

            auxFile.write(term + " " + offsets[0] + " " + offsets[1] + " " + offsets[2] + " " + offsets[3]);
            auxFile.newLine();
        }

    }


    private static void writeMergedPostings(String term, ArrayList<Posting> postingList, RandomAccessFile raf, BufferedWriter auxFile, HashMap<String, long[]> offsetsHelper) throws IOException {
        raf.seek(0);
        ArrayList<Integer> docIds = new ArrayList<>();
        ArrayList<Integer> freqs = new ArrayList<>();

        for (Posting p : postingList) {
            docIds.add(p.getDocId());
            freqs.add(p.getFreq());
        }

        byte[] compressedDocIds = VariableByte.encode(docIds);
        byte[] compressedFreq = UnaryInteger.encodeToUnary(freqs);

        long offsetDocID;
        long freqOffset;
        long endOffset;


        if (offsetsHelper.containsKey(term)){
            // get old offsets
            offsetDocID = offsetsHelper.get(term)[0];
            freqOffset = offsetsHelper.get(term)[1];
            endOffset = offsetsHelper.get(term)[2];

            // update offsets
            long[] off = new long[3];
            off[0] = offsetDocID;
            off[1] = freqOffset + compressedDocIds.length;
            off[2] = endOffset + compressedDocIds.length + compressedFreq.length;
            offsetsHelper.remove(term);
            offsetsHelper.put(term, off);

            // write new postings to the file
            String line;
            long position = 0;
            while ((line = raf.readLine()) != null) {
                if (line.contains(term)) {
                    position = raf.getFilePointer();
                }
            }
            raf.seek(position + freqOffset);
            raf.write(compressedDocIds);
            raf.seek(position + endOffset);
            raf.write(compressedFreq);

        } else {
            offsetDocID = term.getBytes().length;
            freqOffset = offsetDocID + compressedDocIds.length;
            endOffset = freqOffset + compressedFreq.length;

            long[] off = new long[4];
            off[0] = offsetDocID;
            off[1] = freqOffset;
            off[2] = endOffset;
            off[3] = raf.length();
            offsetsHelper.put(term, off);


            // write new term at the end of the file
            long endOfFile = raf.length();
            raf.seek(endOfFile);
            raf.write(term.getBytes());
            raf.write(compressedDocIds);
            raf.write(compressedFreq);
            raf.write("\n".getBytes());


            if(term.equals("001015") || term.equals("001013")) {
                System.out.println("kill " + term);
            }
        }



        // TODO check if it is better to save in a file as you go or only on the last step of the algorithm
//        auxFile.write(term + " " + offsetDocID + " " + offsetFreq + " " + endLineOffset);
//        auxFile.newLine();
    }


    private static void addTermToQueueII(String line, int blockIndex, TreeMap<String, ArrayList<Posting>> postingListMap, PriorityQueue<PostingEntry> priorityQueue) {
        String[] parts = line.split(" ");
        String term = parts[0];
        ArrayList<Posting> postings = new ArrayList<>();

        for (int j = 1; j < parts.length; j += 2) {
            int docId = Integer.parseInt(parts[j]);
            int freq = Integer.parseInt(parts[j + 1]);
            postings.add(new Posting(docId, freq));
        }

        if (postingListMap.containsKey(term)) {
            postingListMap.get(term).addAll(postings);
        } else {
            postingListMap.put(term, postings);
        }

        priorityQueue.add(new PostingEntry(term, blockIndex));
    }

    private static void addTermToQueueL(String line, int blockIndex, TreeMap<String, TermStats> termStatsTreeMap, PriorityQueue<LexiconEntry> priorityQueue) {
        String[] parts = line.split(" ");
        String term = parts[0];
        int cf = Integer.parseInt(parts[1]);
        int df = Integer.parseInt(parts[2]);

        TermStats termStats = termStatsTreeMap.getOrDefault(term, new TermStats(term, 0, 0, 0));
        termStats.addToCollectionFrequency(cf);
        termStats.addToDocumentFrequency(df);
        termStatsTreeMap.put(term, termStats);

        priorityQueue.add(new LexiconEntry(term, cf, df, blockIndex));
    }

    private static double computeTermUpperBound(RandomAccessFile rafInvertedIndex, TermStats termStats, long[] offsets, TreeMap<Integer, Integer> documentIndexes) throws IOException {
        rafInvertedIndex.seek(0);
        String term = termStats.getTerm();

        long len1 = offsets[1] - offsets[0];
        long len2 = offsets[2] - offsets[1];
        long currentOffset = offsets[3];

        termStats.setInvertedIndexOffset(currentOffset);

        byte[] docIdBytes = new byte[(int) len1];
        byte[] freqBytes = new byte[(int) len2];

        rafInvertedIndex.seek(currentOffset + term.getBytes().length);
        rafInvertedIndex.readFully(docIdBytes);

        rafInvertedIndex.seek(0);

        rafInvertedIndex.seek(currentOffset + offsets[1]);
        rafInvertedIndex.readFully(freqBytes);
        rafInvertedIndex.seek(0);

        List<Integer> docIdDecoded = VariableByte.decode(docIdBytes);
        List<Integer> frequencyDecoded = UnaryInteger.decodeFromUnary(freqBytes);
        TreeMap<Integer, Integer> docIdsLengths = new TreeMap<>();

        String avgDocLen = "";
        int totalNumberOfDocs = 0;

        // get statistics calculated before
        try (BufferedReader br = new BufferedReader(new FileReader("data/output/avgDocLen.txt"))) {
            avgDocLen = br.readLine();
            totalNumberOfDocs = Integer.parseInt(br.readLine());
        } catch (IOException e) {
            e.printStackTrace();
        }


        for (Integer docId : docIdDecoded){
            if (documentIndexes.containsKey(docId)){
                docIdsLengths.put(docId, documentIndexes.get(docId));
            }
        }


        double maxResult = Integer.MIN_VALUE;
        Ranking ranking = new Ranking();

        for (int i = 0; i < docIdsLengths.size(); i++) {

            double result = ranking.computeRanking(frequencyDecoded.get(i), totalNumberOfDocs, termStats.getDocumentFrequency(), docIdsLengths.get(i), Double.parseDouble(avgDocLen));
            if (result > maxResult) {
                maxResult = result;
            }
        }

        return maxResult;
    }
//
//    private static HashMap<Integer, Integer> searchValuesDocumentIndex(RandomAccessFile bufferedReader, List<Integer> inputList) throws IOException {
//        HashMap<Integer, Integer> outputList = new HashMap<>();
//
//        String line;
//        while ((line = bufferedReader.readLine()) != null) {
//            String[] parts = line.split(" ");
//
//
//            if (inputList.contains(Integer.valueOf(parts[0]))){
//                outputList.put(Integer.valueOf(parts[0]), Integer.valueOf(parts[1]));
//            }
//        }
//        bufferedReader.mark(0);
//        bufferedReader.reset();
//
//        return outputList;
//    }
//
//    private static HashMap<Integer, Integer> searchDocumentIndex(RandomAccessFile file, List<Integer> inputList, int totalNumberOfDocs) throws IOException {
//        HashMap<Integer, Integer> outputList = new HashMap<>();
//
//        for (Integer valueToSearch : inputList){
//            long low = 0;
//            long high = totalNumberOfDocs - 1;
//
//            while (low <= high) {
//                long mid = (low + high) >>> 1;
//                file.seek(mid);
//
//                // Read the line from the current position
//                String line = file.readLine();
//                if (line == null || line.equals("")) break; // Reached end of file
//
//                String[] parts = line.split(" ");
//
//                // Process the line to extract the value for comparison
//                int value = Integer.parseInt(parts[0]); // Assuming each line contains a single integer
//
//                if (value == valueToSearch) {
//                    outputList.put(Integer.valueOf(parts[0]), Integer.valueOf(parts[1]));
//                    break;
//                } else if (value < valueToSearch) {
//                    low = mid + 1;
//                } else {
//                    high = mid - 1;
//                }
//            }
//        }
//        return outputList;
//    }

    private static TreeMap<Integer, Integer> readDocumentIndex() {
        TreeMap<Integer, Integer> documentIndex = new TreeMap<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(Paths.PATH_DOCUMENT_INDEX_MERGED));
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\s+");
                documentIndex.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));

            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return documentIndex;
    }



    private static TreeMap<String, long[]> readOffsetFile() throws IOException {
        TreeMap<String, long[]> offsetTree = new TreeMap<>();

        try {
            BufferedReader br = new BufferedReader(new FileReader(Paths.PATH_OFFSETS));
            String line;

            while ((line = br.readLine()) != null) {
                long[] offsets = new long[4];
                String[] parts = line.split(" ");

                offsets[0] = Integer.parseInt(parts[1]);
                offsets[1] = Integer.parseInt(parts[2]);
                offsets[2] = Integer.parseInt(parts[3]);
                offsets[3] = Integer.parseInt(parts[4]);

                offsetTree.put(parts[0], offsets);
            }
            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return offsetTree;

    }
}
