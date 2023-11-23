package unipi.it.mircv.common;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import unipi.it.mircv.preprocessing.Preprocessing;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Doc {
    private int id;
    private String text;

    public Doc(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public String getText() {
        return text;
    }
    @Override
    public String toString(){
        return this.id+"    "+this.text;
    }
}



public class Reader {

    private static final int BUFFER_SIZE = 4096;

    public static List<Doc> processCollection(String file) {
        Preprocessing preprocessing = new Preprocessing();
        List<Doc> docList = new ArrayList<>();

        // Use a regular expression to match the document ID in the entry name
        Pattern pattern = Pattern.compile("^(\\d+)\\s+(.*)$");

        try (InputStream fileStream = new FileInputStream(file);
             InputStream gzipStream = new GzipCompressorInputStream(fileStream);
             TarArchiveInputStream tarStream = new TarArchiveInputStream(gzipStream)) {

            TarArchiveEntry entry;
            byte[] buffer = new byte[BUFFER_SIZE];

            while ((entry = tarStream.getNextTarEntry()) != null) {
                if (!entry.isDirectory()) {
                    // Create a ByteArrayOutputStream to store the content in chunks
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                    // Read and process the content in chunks
                    int len;
                    while ((len = tarStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
                        byteArrayOutputStream.write(buffer, 0, len);
                        // Process the chunked content
                        String contentChunk = byteArrayOutputStream.toString("UTF-8");
                        String[] lines = contentChunk.split("\\r?\\n");

                        for (String line : lines) {
                            Matcher matcher = pattern.matcher(line);

                            if (matcher.find()) {
                                int id = Integer.parseInt(matcher.group(1));
                                String text = matcher.group(2);
                                // Create a Doc object and add it to the list
                                Doc doc = new Doc(id, preprocessing.clean(text));
                                System.out.println(doc);
                                docList.add(doc);
                            }
                        }

                        // Reset the ByteArrayOutputStream after processing the chunk
                        byteArrayOutputStream.reset();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return docList;
    }

    public static void main(String[] args) {
        List<Doc> docList = processCollection("path/to/your/collection.tar.gz");

        // Print the extracted documents
        for (Doc doc : docList) {
            System.out.println("ID: " + doc.getId());
            System.out.println("Text: " + doc.getText());
            System.out.println("---------------");
        }
    }
}
//Tokenization and lowercase!
//fhdhss