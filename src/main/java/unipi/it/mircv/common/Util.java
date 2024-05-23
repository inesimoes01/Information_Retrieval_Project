package unipi.it.mircv.common;

import org.apache.commons.io.FileUtils;
import unipi.it.mircv.compression.UnaryInteger;
import unipi.it.mircv.compression.VariableByte;
import unipi.it.mircv.common.dataStructures.TermStats;
import unipi.it.mircv.queryProcessing.Ranking;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

    /**
     * Utility class containing various methods for file I/O, memory management, and merging indexes.
     */
    public class Util {


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


        public static double computeTermUpperBound(BufferedReader bufferedReader, TermStats termStats, Integer[] offsets, int currentOffset) throws IOException {
            String term = termStats.getTerm();

            int len1 = offsets[1] - offsets[0];
            int len2 = offsets[2] - offsets[1];
            byte[] docIdBytes = new byte[len1];
            byte[] freqString = new byte[len2];
            byte[] file = FileUtils.readFileToByteArray(new File(Paths.PATH_INVERTED_INDEX_MERGED));


            System.arraycopy(file, currentOffset + term.getBytes().length, docIdBytes, 0, len1);
            System.arraycopy(file, currentOffset + offsets[1], freqString, 0, len2);

            List<Integer> docIdLine = VariableByte.decode(docIdBytes);
            List<Integer> frequencyLine = UnaryInteger.decodeFromUnary(freqString);

            String avgDocLen = "";
            String count = "";
            HashMap<Integer, Integer> docIdsLen = new HashMap<>();

            try {
                bufferedReader = new BufferedReader(new FileReader(Paths.PATH_DOCUMENT_INDEX_MERGED));
                docIdsLen = searchValuesDocumentIndex(bufferedReader, docIdLine);

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

            for (int i = 0; i < docIdsLen.size(); i++) {
                double result = ranking.computeRanking(frequencyLine.get(i), Integer.parseInt(count), termStats.getDocumentFrequency(), docIdsLen.get(i), Double.parseDouble(avgDocLen));
                if (result > maxResult) {
                    maxResult = result;
                }
            }

            return maxResult;
        }


        public static Integer[] findInvertedIndexOffset(String term) throws IOException {
            Integer[] offsets = new Integer[3];

            try {
                List<String> lines = Files.readAllLines(Path.of(Paths.PATH_OFFSETS), StandardCharsets.UTF_8);
                for (String line : lines) {
                    String[] parts = line.split(" ");
                    if (parts[0].equals(term)) {
                        offsets[0] = Integer.valueOf(parts[1]);
                        offsets[1] = Integer.valueOf(parts[2]);
                        offsets[2] = Integer.valueOf(parts[3]);
                        return offsets;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }


        static HashMap<Integer, Integer> searchValuesDocumentIndex(BufferedReader bufferedReader, List<Integer> inputList) throws IOException {
            HashMap<Integer, Integer> outputList = new HashMap<>();

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(" ");


                if (inputList.contains(Integer.valueOf(parts[0]))){
                    outputList.put(Integer.valueOf(parts[0]), Integer.valueOf(parts[1]));
                }
            }
            bufferedReader.mark(0);
            bufferedReader.reset();

            return outputList;
        }


    }