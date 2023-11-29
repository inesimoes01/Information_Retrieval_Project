package unipi.it.mircv.queryProcessing;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FileReader {
    private Map<Integer, Integer> frequenciesByDocId = new LinkedHashMap<>();
    private int collectionFrequency;
    private int documentFrequency;
    private int nTotalDocuments;

    // number of total documents

    private final Path FOLDER_PATH = Paths.get("data/output");
    public boolean searchTermInFile(String term){
        try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(FOLDER_PATH, "Lexicon*.txt")) {
            for (Path file : dirStream) {
                if (searchInFile(file, term)) {
                    return true;
                }
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return false;
    }

    private boolean searchInFile(Path filePath, String searchTerm) throws IOException {
        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        for (String line : lines) {
            String[] parts = line.split("/");
            if (parts.length >= 2 && parts[0].equalsIgnoreCase(searchTerm)) {
                System.out.println("File: " + filePath.getFileName());
                saveLexiconValues(line);
                return true; // found the term in this file
            }
        }
        return false; // Term not found in this file
    }

    private void saveLexiconValues(String line) {
        String[] parts = line.split("/");
        if (parts.length >= 2) {
            String term = parts[0].trim();
            String[] freqInfo = parts[1].trim().split("\\s+");
            String[] termInfo = parts[2].trim().split("\\s+");

            if (termInfo.length >= 2) {
                collectionFrequency = Integer.parseInt(freqInfo[0].trim());
                documentFrequency = Integer.parseInt(freqInfo[1].trim());

                //Map<Integer, Integer> frequenciesByDocId = new LinkedHashMap<>();
                for (int i = 0; i < termInfo.length; i += 2) {
                    int docId = Integer.parseInt(termInfo[i].trim());
                    int freq = Integer.parseInt(termInfo[i + 1].trim());
                    frequenciesByDocId.put(docId, freq);
                }

//                System.out.println("collectionFrequency: "+ collectionFrequency);
//                System.out.println("documentFrequency: " + documentFrequency);
//                System.out.println("frequenciesByDocID: "+  frequenciesByDocId);
            }
        }

    }


    public Map<Integer, Integer> getFrequenciesByDocId() {
        return frequenciesByDocId;
    }

    public int getCollectionFrequency() {
        return collectionFrequency;
    }

    public int getDocumentFrequency() {
        return documentFrequency;
    }

    public int getNTotalDocuments() {
        return nTotalDocuments;
    }
}
