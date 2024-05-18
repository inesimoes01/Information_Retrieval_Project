package unipi.it.mircv.indexing;


import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import unipi.it.mircv.common.IndexUtil;
import unipi.it.mircv.common.MemoryUtil;
import unipi.it.mircv.indexing.dataStructures.*;
import unipi.it.mircv.preprocessing.Preprocessing;
import unipi.it.mircv.preprocessing.Tokenization;
import unipi.it.mircv.queryProcessing.dataStructures.PostingList;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewIndex {

    private static final int N_DOCS = 5000;
    private static final int BUFFER_SIZE = 4096;
    private static int blockNumber = 0;

    private static Lexicon lexicon = new Lexicon();
    private static InvertedIndex invertedIndex = new InvertedIndex();
    private static DocumentIndex documentIndex = new DocumentIndex();
    //public static HashMap<String, ArrayList<Posting>> postingListElem = new HashMap<>();
    public static ArrayList<String> termListToSort = new ArrayList<>();

    public static int lastDocId = -1;

    public static List<Doc> run(String filepath) {
        Preprocessing preprocessing = new Preprocessing();
        Index index = new Index();
        long reading_files_time=0;
        long start_time=0;
        long end_time=0;

        deleteFilesInFolder("data/output");
        System.out.println("Finished deleting");

        Pattern pattern = Pattern.compile("^(\\d+)\\s+(.*)$");
        int len;
        int docid = 0;

        start_time = System.currentTimeMillis();
        try (InputStream fileStream = new FileInputStream(filepath);
             InputStream gzipStream = new GzipCompressorInputStream(fileStream);
             TarArchiveInputStream tarStream = new TarArchiveInputStream(gzipStream)) {

            TarArchiveEntry entry;
            byte[] buffer = new byte[BUFFER_SIZE];

            while ((entry = tarStream.getNextTarEntry()) != null) {

                if(!entry.isDirectory()){

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    while ((len = tarStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, len);
                        String contentChunk = byteArrayOutputStream.toString("UTF-8");
                        String[] lines = contentChunk.split("\\r?\\n");

                        for (String line : lines) {
                            Matcher matcher = pattern.matcher(line);

                            // try to match the pattern <doc_id> <content>
                            if (matcher.find()){

                                handleMemory(docid);
                                int docno = 0;

                                // if string is not a valid integer, continue to the next doc
                                try { docno = Integer.parseInt(matcher.group(1));
                                } catch (NumberFormatException e) { continue; }

                                String text = matcher.group(2);
                                // if second part is an empty string, continue to the next doc
                                if (text.isEmpty()) continue;

                                // process text and create a new doc struct
                                String processed_text = preprocessing.clean(text);
                                if (processed_text.isEmpty()) continue;
                                Doc doc = new Doc(docno, Tokenization.tokenize(processed_text));
                                documentIndex.updateDocumentIndex(docno, processed_text.length());

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

                                        if(currentTerm.getLastDocIdInserted() != (docid)){
                                            currentTerm.updateLastDocIdInserted(docid);
                                            currentTerm.updateCollectionFrequency(termCounter.get(token));
                                            currentTerm.updateDocumentFrequency();
                                            //System.out.println(currentTerm.getCollectionFrequency());

                                            invertedIndex.getInvertedIndex().get(token).add(new Posting(docid, termCounter.get(token)));
                                        }
                                    }
                                    // if token not in the lexicon, add term to structures
                                    else {
                                        lexicon.updateLexicon(token, termCounter.get(token));
                                        TermStats currentTerm = lexicon.getLexicon().get(token);
                                        currentTerm.updateLastDocIdInserted(docid);

                                        invertedIndex.addPosting(token, docid, currentTerm.getCollectionFrequency());

                                        termListToSort.add(token);
                                    }
                                }

                                if (docid % 50000 == 0) {
                                    long middle_time = System.currentTimeMillis();
                                    System.out.println("Current Doc ID: " + docid + " and took " + (double) (middle_time - start_time) / 1000 + " seconds.");
                                }

                                docid++;

                            }

                        }

                        byteArrayOutputStream.reset();
                    }

                    saveBlockInformation();
                    reading_files_time = System.currentTimeMillis();
                    System.out.println("Finished reading file. Took " + (double) (reading_files_time - start_time) / 1000 + " seconds. Starting to merge " + blockNumber + " blocks...");
                    mergeAllStructures();
                }

            }
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
        IndexUtil.mergeDocumentIndex(blockNumber);
        IndexUtil.mergeInvertedIndex(blockNumber);
        IndexUtil.lexiconMerge(blockNumber);
    }

    private static void handleMemory(int currentDocId){
        long threshold = Runtime.getRuntime().totalMemory() * 10 / 100;
        if (Runtime.getRuntime().freeMemory() < threshold){
            System.out.println("Memory Full. Writing block number " + blockNumber);
            saveBlockInformation();
            lastDocId = currentDocId;

            while (Runtime.getRuntime().freeMemory() < threshold * 2) {
                // wait for free memory
                System.gc();
            }

        }
    }

    private static void saveBlockInformation(){
        IndexUtil.writeBlockToDisk(blockNumber, documentIndex, lastDocId);
        IndexUtil.writeBlockToDisk(blockNumber, lexicon, lastDocId);
        IndexUtil.writeBlockToDisk(blockNumber, invertedIndex, lastDocId);

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
                        //System.out.println("Deleted file: " + file.getAbsolutePath());
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
