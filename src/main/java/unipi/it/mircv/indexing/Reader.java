//package unipi.it.mircv.indexing;
//
//import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
//import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
//import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
//import unipi.it.mircv.indexing.IndexUtil;
//import unipi.it.mircv.common.Util;
//import unipi.it.mircv.indexing.Index;
//import unipi.it.mircv.indexing.dataStructures.Doc;
//import unipi.it.mircv.indexing.dataStructures.DocumentIndex;
//import unipi.it.mircv.indexing.dataStructures.InvertedIndex;
//import unipi.it.mircv.indexing.dataStructures.Lexicon;
//import unipi.it.mircv.preprocessing.Preprocessing;
//import unipi.it.mircv.preprocessing.Tokenization;
//
//import java.io.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//
//public class Reader {
//    private static final int N_DOCS = 5000;
//    private static final int BUFFER_SIZE = 4096;
//
//    public static List<Doc> processCollection(String file) {
//
//        Index index = new Index();
//        Util util = new Util();
//        IndexUtil indexUtil= new IndexUtil();
//        Preprocessing preprocessing = new Preprocessing();
//        List<Doc> docList = new ArrayList<>();
//        Tokenization tokenization = new Tokenization();
//
//        deleteFilesInFolder("data/output");
//
//        // Use a regular expression to match the document ID in the entry name
//        Pattern pattern = Pattern.compile("^(\\d+)\\s+(.*)$");
//
//        int documentCount = 0;  // Counter to track the number of processed documents
//
//        try (InputStream fileStream = new FileInputStream(file);
//             InputStream gzipStream = new GzipCompressorInputStream(fileStream);
//             TarArchiveInputStream tarStream = new TarArchiveInputStream(gzipStream)) {
//
//            TarArchiveEntry entry;
//            byte[] buffer = new byte[BUFFER_SIZE];
//
//            while ((entry = tarStream.getNextTarEntry()) != null) {
//
//                if (!entry.isDirectory()) {
//                    // Create a ByteArrayOutputStream to store the content in chunks
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//
//                    // Read and process the content in chunks
//                    int len;
//                    while ((len = tarStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
//                        byteArrayOutputStream.write(buffer, 0, len);
//                        // Process the chunked content
//                        String contentChunk = byteArrayOutputStream.toString("UTF-8");
//                        String[] lines = contentChunk.split("\\r?\\n");
//
//                        for (String line : lines) {
//                            Matcher matcher = pattern.matcher(line);
//
//                            if (matcher.find()) {
//
//                                int id = 0;
//                                try {
//                                    id = Integer.parseInt(matcher.group(1));
//                                } catch (NumberFormatException e) {
//                                    // If the parsing fails (e.g., the string is not a valid integer), continue to the next iteration.
//                                    continue;
//                                }
//
//                                // If the second column in the 'columns' array is an empty string, continue to the next iteration.
//                                if (matcher.group(2).length() == 0) {
//                                    continue;
//                                }
//
//                                // The code continues here if the parsing is successful and the second column is not empty.
//                                // You can use 'docNo' and 'columns[1]' for further processing.
//
//                                String text = matcher.group(2);
//                                // Create a Doc object and add it to the list
//                                text = preprocessing.clean(text);
//
//                                Doc doc = new Doc(id, Tokenization.tokenize(text));
//                                index.createIndex(doc);
//
//                                docList.add(doc);
//
//                                // Increment the document count
//                                documentCount++;
//
////                                // Break out of the loop if the 4th document is processed
////                                if (documentCount >= N_DOCS) {
////                                    break;
////                                }
//                            }
//
//                        }
//
//                        // Reset the ByteArrayOutputStream after processing the chunk
//                        byteArrayOutputStream.reset();
//
//                        // Break out of the loop if the 4th document is processed
////                        if (documentCount >= N_DOCS) {
////                            break;
////                        }
//                    }
//
//
//
//                    //Generate the last Block
//                    System.out.println("Getting document index...");
//                    indexUtil.writeBlockToDisk(index.getBlockNumber(),index.getDocumentIndex());
//                    System.out.println("Getting lexicon...");
//                    indexUtil.writeBlockToDisk(index.getBlockNumber(),index.getLexicon());
//                    System.out.println("Getting inverted index...");
//                    indexUtil.writeBlockToDisk(index.getBlockNumber(),index.getInvertedIndex());
//
//
//                    index.setLexicon(new Lexicon());
//                    index.setInvertedIndex(new InvertedIndex());
//                    index.setDocumentIndex(new DocumentIndex());
//
//                    index.setBlockNumber(index.getBlockNumber() + 1);
//                    System.gc(); //calls the garbage collector to force to free memory.
//                    //Write to the disk
//                    //Increment blockNumber
//
//
//
//                    indexUtil.readBlockFromDisk(index.getBlockNumber());
//                    System.out.println("Merging document index...");
//                    indexUtil.mergeDocumentIndex(index.getBlockNumber());
//                    //util.invertedIndexMerge(index.getBlockNumber());
//                    System.out.println("Merging inverted index...");
//                    indexUtil.mergeInvertedIndex(index.getBlockNumber());
//                    System.out.println("Merging lexicon...");
//                    indexUtil.lexiconMerge(index.getBlockNumber());
//
//                }
//
//                // Break out of the loop if the 4th document is processed
////                if (documentCount >= N_DOCS) {
////                    break;
////                }
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return docList;
//    }
//
//    public static void deleteFilesInFolder(String folderPath) {
//        File folder = new File(folderPath);
//
//        if (folder.exists() && folder.isDirectory()) {
//            File[] files = folder.listFiles();
//            if (files != null) {
//                for (File file : files) {
//                    if (file.isFile()) {
//                        file.delete();
//                        //System.out.println("Deleted file: " + file.getAbsolutePath());
//                    }
//                }
//            } else {
//                System.out.println("Folder is empty or cannot be read.");
//            }
//        } else {
//            System.out.println("Folder does not exist or is not a directory.");
//        }
//    }
//
//}