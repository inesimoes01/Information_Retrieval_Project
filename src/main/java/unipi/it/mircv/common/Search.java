package unipi.it.mircv.common;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Search {
    /**
     * Performs binary search on a file to find a target word.
     *
     * @param filePath    The path to the file to search.
     * @param targetWord  The word to search for.
     * @return A string containing the row index (1-based), the row itself, and the next value.
     */
    public static String binarySearch(String filePath, String targetWord) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int low = 0;
            int high = countLines(filePath) - 1;

            // Perform binary search to find the range of lines containing the target word
            while (low <= high) {
                int mid = (low + high) / 2;
                String currentLine = readLine(filePath, mid);
                String currentWord = currentLine.split("\\s+")[0];

                if (currentWord.equals(targetWord)) {
                    // Found the target word, return the 1-based row index, the row itself, and the next value
                    int rowIndex = mid + 1;
                    return String.format("%d %s", rowIndex, currentLine.trim());
                } else if (currentWord.compareTo(targetWord) < 0) {
                    low = mid + 1;
                } else {
                    high = mid - 1;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error reading the file";
        }

        return "Word not found in the file";
    }

    /**
     * Counts the number of lines in a file.
     *
     * @param filePath The path to the file.
     * @return The number of lines in the file.
     * @throws IOException If an I/O error occurs.
     */
    private static int countLines(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            int lines = 0;
            while (reader.readLine() != null) {
                lines++;
            }
            return lines;
        }
    }

    /**
     * Reads a specific line from a file.
     *
     * @param filePath The path to the file.
     * @param lineNumber The line number to read (0-based).
     * @return The content of the specified line.
     * @throws IOException If an I/O error occurs.
     */
    private static String readLine(String filePath, int lineNumber) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            for (int i = 0; i < lineNumber; i++) {
                reader.readLine();
            }
            return reader.readLine();
        }
    }

    public static void main(String[] args) {
        // Example usage
        String filePath = "data/output/LexiconMerged.txt";
        String targetWord = "mania";
        String result = binarySearch(filePath, targetWord);
        System.out.println(result);
    }
}
