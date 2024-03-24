package unipi.it.mircv.common;

import unipi.it.mircv.indexing.dataStructures.*;
import unipi.it.mircv.queryProcessing.Ranking;

import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.util.*;

    /**
     * Utility class containing various methods for file I/O, memory management, and merging indexes.
     */
    public class Util {



        //myWriterDocIds = new TextWriter("Data/Output/DocIds/docIds" + blockCounter + ".txt");
        //myWriterFreq = new TextWriter("Data/Output/Frequencies/freq" + blockCounter + ".txt");
        //myWriterDocumentIndex = new TextWriter("Data/Output/DocumentIndex/documentIndex" + blockCounter + ".txt");
        /**
         * Merges two ArrayLists element-wise.
         * @param list1 The first ArrayList.
         * @param list2 The second ArrayList.
         * @return The merged ArrayList.
         */
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
        /**
         * Splits an InvertedIndex line into its components.
         *
         * @param line The InvertedIndex line to be split.
         * @return An ArrayList containing the split components.
         */
        static ArrayList<String> splitInvertedIndexLine(String line) {
            if (line!= null) {
                String[] parts = line.split("\\s+", 3);
                ArrayList<String> toReturn = new ArrayList<>();
                if (parts.length == 3) {
                    String term = parts[0];

                    String[] numbers = parts[2].split("\\s+");

                    if (numbers.length <= 2) {
                        toReturn.add(term);
                        toReturn.add(parts[1]);
                        toReturn.add(parts[2]);
                    } else {
                        numbers[0] = parts[1] + " " + numbers[0];
                        int length = numbers.length;
                        int half = length / 2;

                        String list1 = String.join(" ", Arrays.copyOfRange(numbers, 0, half));
                        String list2 = String.join(" ", Arrays.copyOfRange(numbers, half, length));

                        toReturn.add(term);
                        toReturn.add(list1);
                        toReturn.add(list2);

                    }
                }

                return toReturn;
            }
            return null;
        }

        /**
         * Computes the upper bound for a term based on ranking calculations.
         *
         * @param bufferedReader The BufferedReader for DocumentIndex.
         * @param input           The ArrayList containing InvertedIndex components.
         * @param termStats       The statistics for the term.
         * @return The computed upper bound.
         * @throws IOException If an I/O error occurs.
         */
        static double computeTermUpperBound(BufferedReader bufferedReader, ArrayList<String> input, TermStats termStats) throws IOException {
            if (input != null) {
                String[] docids = input.get(1).split("\\s+");
                String[] freqs = input.get(2).split("\\s+");
                String avgDocLen = "";
                String count = "";
                ArrayList<String> docidslen = new ArrayList<>(Arrays.asList(docids));
                try {
                    bufferedReader = new BufferedReader(new FileReader(Paths.PATH_DOCUMENT_INDEX_MERGED));
                    docidslen = new ArrayList<>(searchValuesDocumentIndex(bufferedReader, docidslen));
                    // ... altre operazioni ...

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    bufferedReader.close();
                }


                try (BufferedReader br = new BufferedReader(new FileReader("data/output/avgDocLen.txt"))) {
                    avgDocLen = br.readLine();
                    count = br.readLine();

                } catch (IOException e) {
                    e.printStackTrace();
                }


                double maxResult = Integer.MIN_VALUE;
                Ranking ranking = new Ranking();


                for (int i = 0; i < docids.length; i++) {
                    //int num = Integer.parseInt(numStr);
                    double result = ranking.computeRanking(Integer.parseInt(freqs[i]), Integer.parseInt(count), termStats.getDocumentFrequency(), Integer.parseInt(docidslen.get(i)), Double.parseDouble(avgDocLen));
                    if (result > maxResult) {
                        maxResult = result;
                    }


                    //maxResult = Math.max(maxResult, result);
                }

                return maxResult;
            }
            return 0;
        }

        /**
         * Finds the offset of a term in a RandomAccessFile.
         *
         * @param randomAccessFile The RandomAccessFile to search.
         * @param searchTerm       The term to search for.
         * @param startOffset      The starting offset for the search.
         * @return The offset of the term or -1 if not found.
         * @throws IOException If an I/O error occurs.
         */
        public static long findOffset(RandomAccessFile randomAccessFile, String searchTerm, long startOffset) throws IOException {
            long currentOffset = startOffset;

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
        /**
         * Searches for values in DocumentIndex based on a list of terms.
         *
         * @param bufferedReader The BufferedReader for DocumentIndex.
         * @param inputList      The list of terms to search for.
         * @return The list of corresponding values from DocumentIndex.
         * @throws IOException If an I/O error occurs.
         */
        static ArrayList<String> searchValuesDocumentIndex(BufferedReader bufferedReader, ArrayList<String> inputList) throws IOException {
            ArrayList<String> outputList = new ArrayList<>();
            HashMap<String, String> map = new HashMap<>();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (inputList.contains(parts[0])){
                    outputList.add(parts[1]);
                }
            }
            bufferedReader.mark(0);
            bufferedReader.reset();

            return outputList;
        }



        /**
         * Searches for values in Lexicon based on a term.
         *
         * @param bufferedReader The BufferedReader for Lexicon.
         * @param term            The term to search for.
         * @return The list of corresponding values from Lexicon.
         * @throws IOException If an I/O error occurs.
         */
        static ArrayList<String> searchValuesLexicon (BufferedReader bufferedReader, String term) throws IOException {
            ArrayList<String> toReturn = new ArrayList<>();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts[0] == term){
                    toReturn.add(parts[1]);
                    toReturn.add(parts[2]);
                    toReturn.add(parts[3]);
                }
            }
            return toReturn;
        }

        public static String readLineFromOffset(RandomAccessFile randomAccessFile, long offset) throws IOException {
            // Move to the specified offset
            randomAccessFile.seek(offset);

            // Read the line from the current offset
            String line = randomAccessFile.readLine();

            // If the line is null, it means the end of the file is reached
            if (line != null) {
                return line;
            } else {
                return null;
            }
        }



        public static void main(String[] args) {

            TermStats termStats = new TermStats();
            String abc= "ciaooo 20 1 ";

            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(Paths.PATH_DOCUMENT_INDEX_MERGED))) {
            }catch (IOException e) {
                e.printStackTrace();
            }

        }

    }