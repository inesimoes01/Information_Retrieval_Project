package unipi.it.mircv.indexing;


import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import unipi.it.mircv.common.Paths;
import unipi.it.mircv.common.dataStructures.*;
import unipi.it.mircv.preprocessing.Preprocessing;
import unipi.it.mircv.preprocessing.Tokenization;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Indexing {

    private static int blockNumber = 0;

    public static ArrayList<String> termListToSort = new ArrayList<>();

    public static int lastDocId = -1;

    public static List<Doc> run() throws IOException {
        Lexicon lexicon = new Lexicon();
        InvertedIndex invertedIndex = new InvertedIndex();
        DocumentIndex documentIndex = new DocumentIndex();

        long reading_files_time;
        long start_time;
        long end_time;

//        deleteFilesInFolder("data/output");
//        deleteFilesInFolder("data/output/merged");
//        deleteFilesInFolder("data/output/aux_folder");
        System.out.println("Finished deleting");


        int numberOfDocs = 0;

        start_time = System.currentTimeMillis();

        try(FileInputStream inputStream = new FileInputStream(Paths.PATH_COLLECTION);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            GzipCompressorInputStream gzipCompressorInputStream = new GzipCompressorInputStream(bufferedInputStream);
            TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(gzipCompressorInputStream);
            BufferedReader br = new BufferedReader(new InputStreamReader(tarArchiveInputStream, StandardCharsets.UTF_8))) {

            tarArchiveInputStream.getNextTarEntry();

            while(true){
                String line = br.readLine();
                if (line == null) break;

                String[] parts = line.split("\t");

                // save information and skip invalid documents
                int docNo;
                try { docNo = Integer.parseInt(parts[0]);
                } catch (NumberFormatException e) { continue; }
                String text = parts[1];
                if (text.isEmpty()) continue;

                // check if memory is full
                handleMemory(docNo, invertedIndex, lexicon, documentIndex);

                // process text and create a new doc struct
                String processed_text = Preprocessing.clean(text);
                if (processed_text.isEmpty()) continue;
                Tokenization.tokenize(processed_text);
                documentIndex.getDocumentIndex().put(docNo, processed_text.length());

                // create the index
                HashMap<String, Integer> termCounter = new HashMap<>();
                String[] tokens = processed_text.split(" ");

                // calculate term frequency for all terms
                for (String token : tokens) {
                    termCounter.put(token, termCounter.containsKey(token) ? termCounter.get(token) + 1 : 1);
                }

                for (String token : tokens) {
                    // if token already in the lexicon, update stats and posting list
                    if (lexicon.getLexicon().containsKey(token)){
                        TermStats currentTerm = lexicon.getLexicon().get(token);
                        if(currentTerm.getLastDocIdInserted() != (docNo)){
                            currentTerm.updateLastDocIdInserted(docNo);
                            currentTerm.updateCollectionFrequency(termCounter.get(token));
                            currentTerm.updateDocumentFrequency();

                            invertedIndex.getInvertedIndex().get(token).add(new Posting(docNo, termCounter.get(token)));
                        }
                    }
                    // if token not in the lexicon, add term to structures
                    else {
                        lexicon.updateLexicon(token, termCounter.get(token));
                        TermStats currentTerm = lexicon.getLexicon().get(token);
                        currentTerm.updateLastDocIdInserted(docNo);
                        invertedIndex.addPosting(token, docNo, currentTerm.getCollectionFrequency());
                        termListToSort.add(token);
                    }
                }

                if (numberOfDocs % 50000 == 0) {
                    long middle_time = System.currentTimeMillis();
                    System.out.println("Current Doc ID: " + numberOfDocs + " and took " + (double) (middle_time - start_time) / 1000 + " seconds.");
                }

                numberOfDocs++;

            }

            saveBlockInformation(invertedIndex, lexicon, documentIndex);
            reading_files_time = System.currentTimeMillis();
            System.out.println("Finished reading file. Took " + (double) (reading_files_time - start_time) / 1000 + " seconds. Starting to merge " + blockNumber + " blocks...");
            mergeAllStructures();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        end_time = System.currentTimeMillis();

        System.out.println("Indexing took in total " + (double) (end_time - start_time)/1000 + " seconds.");
        System.out.println("Reading took " + (double) (reading_files_time - start_time)/1000 + " seconds.");
        System.out.println("Merge took " + (double) (end_time-reading_files_time)/1000 + " seconds.");
        return null;

    }

    private static void mergeAllStructures(){
        Merging.mergeDocumentIndex(blockNumber);
        Merging.mergeInvertedIndex(blockNumber);
        Merging.mergeLexicon(blockNumber-1);
    }

    private static void handleMemory(int currentDocId, InvertedIndex invertedIndex, Lexicon lexicon, DocumentIndex documentIndex){
        long threshold = Runtime.getRuntime().totalMemory() * 10 / 100;
        if (Runtime.getRuntime().freeMemory() < threshold){
            System.out.println("Memory Full. Writing block number " + blockNumber);
            saveBlockInformation(invertedIndex, lexicon, documentIndex);
            lastDocId = currentDocId;
            System.gc();
        }
    }

    private static void saveBlockInformation(InvertedIndex invertedIndex, Lexicon lexicon, DocumentIndex documentIndex){
//        IndexUtil.writeBlockToDisk(blockNumber, documentIndex);
//        IndexUtil.writeBlockToDisk(blockNumber, lexicon);
//        IndexUtil.writeBlockToDisk(blockNumber, invertedIndex);
        IndexUtil.writeBlockToDisk(blockNumber, documentIndex);
        IndexUtil.writeBlockToDisk(blockNumber, lexicon);
        IndexUtil.writeBlockToDisk(blockNumber, invertedIndex);

        lexicon.getLexicon().clear();
        documentIndex.getDocumentIndex().clear();
        invertedIndex.getInvertedIndex().clear();

        lexicon = new Lexicon();
        invertedIndex = new InvertedIndex();
        documentIndex = new DocumentIndex();

        blockNumber++;
    }
    public static void deleteFilesInFolder(String folderPath) {
        File folder = new File(folderPath);

        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            } else {
                System.out.println("Folder is empty or cannot be read.");
            }
        } else {
            System.out.println("Folder does not exist or is not a directory.");
        }
    }
}
