package unipi.it.mircv.common;

import junit.framework.TestCase;
import unipi.it.mircv.common.dataStructures.DocumentIndex;


import java.util.ArrayList;
import java.util.Arrays;

public class DocumentIndexTest extends TestCase {

//    public void testUpdateDocumentIndex_whenDocIdNotPresent() {
////        int docId = 1;
////        int len = 50;
////        DocumentIndex docIndex = new DocumentIndex();
////
////        docIndex.updateDocumentIndex(docId, len);
////
////        assertTrue(docIndex.getDocumentIndex().containsKey(docId));
////        assertEquals(len, docIndex.getDocumentIndex().get(docId).getLen());
//
//    }
//    public void testUpdateDocumentIndex_whenDocIdAlreadyPresent() {
//        DocumentIndex docIndex = new DocumentIndex();
//        int docId = 2;
//        int initialLen = 100;
//        int updatedLen = 200;
//
//        docIndex.updateDocumentIndex(docId, initialLen);
//        docIndex.updateDocumentIndex(docId, updatedLen);
//
//        assertTrue(docIndex.getDocumentIndex().containsKey(docId));
//        assertEquals(updatedLen, docIndex.getDocumentIndex().get(docId).getLen());
//    }
//
//    public void testSortDocumentIndex(){
//        DocumentIndex documentIndex = new DocumentIndex();
//        documentIndex.updateDocumentIndex(3, 150);
//        documentIndex.updateDocumentIndex(1, 100);
//        documentIndex.updateDocumentIndex(5, 200);
//
//        ArrayList<Integer> expectedSortedIds = new ArrayList<>(Arrays.asList(1, 3, 5));
//
//        ArrayList<Integer> sortedDocIds = documentIndex.sortDocumentIndex();
//
//        assertEquals(expectedSortedIds, sortedDocIds);
//    }
//
//    public void testToString(){
//        DocumentIndex documentIndex = new DocumentIndex();
//        documentIndex.updateDocumentIndex(1, 150);
//        documentIndex.updateDocumentIndex(2, 200);
//        String expectedOutput = "1 150\n2 200\n";
//
//        // Act
//        String toStringResult = documentIndex.toString();
//
//        // Assert
//        assertEquals(expectedOutput, toStringResult);
//    }
}