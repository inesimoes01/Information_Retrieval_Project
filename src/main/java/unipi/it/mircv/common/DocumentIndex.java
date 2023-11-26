package unipi.it.mircv.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class DocumentIndex {

    private HashMap<Integer, DocumentStats> documentIndex = new HashMap<>();

    public DocumentIndex() {
        // No need to initialize documentIndex here
    }

    public static class DocumentStats {
        private int len;  // Remove the static keyword

        public void setLen(int len) {
            this.len = len;
        }

        public int getLen() {
            return len;
        }
    }

    public void updateDocumentIndex(int docId, int len) {
        if (!documentIndex.containsKey(docId)) {
            DocumentStats documentStats = new DocumentStats();
            documentStats.setLen(len);
            documentIndex.put(docId, documentStats);
        } else {
            DocumentStats documentStats = documentIndex.get(docId);
            documentStats.setLen(len);
        }
    }

    public ArrayList<Integer> sortDocumentIndex(){
        ArrayList<Integer> sortedDocIds = new ArrayList<>(documentIndex.keySet());
        Collections.sort(sortedDocIds);

        return sortedDocIds;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int docId : documentIndex.keySet()) {
            DocumentStats documentStats = documentIndex.get(docId);

            sb.append("DocID: ").append(docId).append("\n");
            sb.append("Document Length: ").append(documentStats.getLen()).append("\n\n");
        }

        return sb.toString();
    }



}

