package unipi.it.mircv.indexing;

import unipi.it.mircv.common.Paths;
import unipi.it.mircv.compression.UnaryInteger;
import unipi.it.mircv.compression.VariableByte;
import unipi.it.mircv.indexing.dataStructures.Posting;
import unipi.it.mircv.indexing.dataStructures.PostingEntry;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.util.*;
import java.io.*;

public class Merging {

    public static void mergeInvertedIndex(int blockCounter) {
        String outputPath = Paths.PATH_INVERTED_INDEX_MERGED; // Output file path for merged lexicon

        try (FileOutputStream fos = new FileOutputStream(outputPath);
             BufferedWriter auxFile = new BufferedWriter(new FileWriter(Paths.PATH_OFFSETS))) {

            TreeMap<String, ArrayList<Posting>> postingListMap = new TreeMap<>();
            PriorityQueue<PostingEntry> priorityQueue = new PriorityQueue<>(Comparator.comparing(PostingEntry::getTerm));

            BufferedReader[] lexiconReaders = new BufferedReader[blockCounter];
            Iterator<String>[] iterators = new Iterator[blockCounter];

            for (int i = 0; i < blockCounter; i++) {
                lexiconReaders[i] = new BufferedReader(new FileReader("data/output/InvertedIndex" + i + ".txt"));
                iterators[i] = lexiconReaders[i].lines().iterator();

                if (iterators[i].hasNext()) {
                    String line = iterators[i].next();
                    addTermToQueue(line, i, postingListMap, priorityQueue);
                }
            }

            while (!priorityQueue.isEmpty()) {
                PostingEntry entry_aux = priorityQueue.poll();
                String term_aux = entry_aux.getTerm();
                int blockIndex_aux = entry_aux.getBlockIndex();

                ArrayList<Posting> postingList = postingListMap.get(term_aux);
                //postingListMap.remove(term_aux);

                if (iterators[blockIndex_aux].hasNext()) {
                    String line = iterators[blockIndex_aux].next();
                    addTermToQueue(line, blockIndex_aux, postingListMap, priorityQueue);
                }
            }

            for (Map.Entry<String, ArrayList<Posting>> entry : postingListMap.entrySet()) {
                String term_aux = entry.getKey();
                ArrayList<Posting> postingList = entry.getValue();
                writeMergedPostings(term_aux, postingList, fos, auxFile);
            }

            for (BufferedReader reader : lexiconReaders) {
                if (reader != null) {
                    reader.close();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    private static List<Posting> readPostings(String term, int blockIndex, Iterator<String>[] iterators, PriorityQueue<PostingEntry> priorityQueue) {
//        List<Posting> postings = new ArrayList<>();
//        while (iterators[blockIndex].hasNext()) {
//            String line = iterators[blockIndex].next();
//            String[] parts = line.split(" ");
//            if (!parts[0].equals(term)) {
//                addTermToQueue(line, blockIndex, priorityQueue);
//                break;
//            }
//            for (int j = 1; j < parts.length; j += 2) {
//                int docId = Integer.parseInt(parts[j]);
//                int freq = Integer.parseInt(parts[j + 1]);
//                postings.add(new Posting(docId, freq));
//            }
//        }
//        return postings;
//    }

    private static void writeMergedPostings(String term, ArrayList<Posting> postingList, FileOutputStream fos, BufferedWriter auxFile) throws IOException {
        ArrayList<Integer> docIds = new ArrayList<>();
        ArrayList<Integer> freqs = new ArrayList<>();

        for (Posting p : postingList) {
            docIds.add(p.getDocId());
            freqs.add(p.getFreq());
        }

        byte[] compressedDocIds = VariableByte.encode(docIds);
        byte[] compressedFreq = UnaryInteger.encodeToUnary(freqs);

        int offsetDocID = term.getBytes().length;
        int offsetFreq = offsetDocID + compressedDocIds.length;
        int endLineOffset = offsetFreq + compressedFreq.length;

        auxFile.write(term + " " + offsetDocID + " " + offsetFreq + " " + endLineOffset);
        auxFile.newLine();

        fos.write(term.getBytes());
        fos.write(compressedDocIds);
        fos.write(compressedFreq);
        fos.write("\n".getBytes());
    }


//    private static void addTermToQueue(String line, int blockIndex, PriorityQueue<PostingEntry> priorityQueue) {
//        String[] parts = line.split(" ");
//        String term = parts[0];
//        priorityQueue.add(new PostingEntry(term, blockIndex));
//    }

    private static void addTermToQueue(String line, int blockIndex, TreeMap<String, ArrayList<Posting>> postingListMap, PriorityQueue<PostingEntry> priorityQueue) {
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

}
