package unipi.it.mircv.indexing.dataStructures;

import java.util.*;
import java.util.stream.Collectors;

public class DocumentIndex {

    private TreeMap<Integer, Integer> documentIndex = new TreeMap<>();

    public DocumentIndex() { }

//    public ArrayList<Integer> sortDocumentIndex(){
////        for (Integer i : documentIndex.keySet()) {
////            int index = documentIndex.get(i);
////
////            Collections.sort(int, new Comparator<Integer>()){
////
////            };
////        }
////        return documentIndex.entrySet()
////                .stream()
////                .sorted(Map.Entry.comparingByKey())
////                .collect(Collectors.toMap(
////                        Map.Entry::getKey,
////                        Map.Entry::getValue,
////                        (e1, e2) -> e1, // In case of key collisions, keep the first value
////                        LinkedHashMap::new // Use LinkedHashMap to maintain the order
////                ));
//
//        Collections.sort(documentIndex);
//        for (int num : list) {
//            for (Map.Entry<String, Integer> entry : map.entrySet()) {
//                if (entry.getValue().equals(num)) {
//                    sortedMap.put(entry.getKey(), num);
//                }
//            }
//        }
//
//    }

    public TreeMap<Integer, Integer> getDocumentIndex() {
        return documentIndex;
    }


//    public static class DocumentStats {
//        private int len;  // Remove the static keyword
//
//        public void setLen(int len) {
//            this.len = len;
//        }
//
//        public int getLen() {
//            return len;
//        }
//
//        @Override
//        public String toString() {
//            return "" + len + "";
//        }
//    }

//    public void updateDocumentIndex(int docId, int len) {
//        if (!documentIndex.containsKey(docId)) {
//            DocumentStats documentStats = new DocumentStats();
//            documentStats.setLen(len);
//            documentIndex.put(docId, documentStats);
//        } else {
//            DocumentStats documentStats = documentIndex.get(docId);
//            documentStats.setLen(len);
//        }
//    }



//    @Override
//    public String toString() {
//        StringBuilder sb = new StringBuilder();
//
//        for (Integer docId : documentIndex.keySet()) {
//            DocumentStats documentStats = documentIndex.get(docId);
//
//            sb.append(docId).append(" ");
//            sb.append(documentStats.getLen()).append("\n");
//        }
//
//        return sb.toString();
//    }



    public void setDocumentIndex(HashMap<Integer, Integer> documentIndex) {
    }



}

