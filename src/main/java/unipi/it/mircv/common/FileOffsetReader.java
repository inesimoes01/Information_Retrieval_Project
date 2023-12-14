package unipi.it.mircv.common;

import java.io.IOException;
import java.io.RandomAccessFile;

public class FileOffsetReader {

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
        try (RandomAccessFile randomAccessFile = new RandomAccessFile("data/output/InvertedIndexMerged.txt", "r")) {
            long offsetToRead = 829895;  // Replace with the desired offset

            String line = readLineFromOffset(randomAccessFile, offsetToRead);

            if (line != null) {
                System.out.println("Line at offset " + offsetToRead + ": " + line);
            } else {
                System.out.println("No line found at offset " + offsetToRead);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
