package unipi.it.mircv.queryProcessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TermQP {
    private String term;
    private Map<Integer, Integer> docIdFreq = new HashMap<>();
    // DocId Freq
    private Integer collectionFrequency; // not being used
    // how many times the term appear in the collection
    private Integer documentFrequency;
    // how many documents the term appears in

    private List<DocumentQP> documentsWithTerm = new ArrayList<>();

    public Map<Integer, Integer> getDocIdFreq() {
        return docIdFreq;
    }

    public void setDocIdFreq(Map<Integer, Integer> docIdFreq) {
        this.docIdFreq = docIdFreq;
    }

    public List<DocumentQP> getDocumentsWithTerm() {
        return documentsWithTerm;
    }

    public List<Integer> getDocumentsWithTermDocIDs() {
        List<Integer> list = new ArrayList<>();
        for (DocumentQP doc : documentsWithTerm){
            list.add(doc.getDocId());
        }
        return list;
    }

    public void setDocumentsWithTerm(DocumentQP documentsWithTerm) {
        this.documentsWithTerm.add(documentsWithTerm);
    }



    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }



    public Integer getCollectionFrequency() {
        return collectionFrequency;
    }

    public void setCollectionFrequency(Integer collectionFrequency) {
        this.collectionFrequency = collectionFrequency;
    }

    public Integer getDocumentFrequency() {
        return documentFrequency;
    }

    public void setDocumentFrequency(Integer documentFrequency) {
        this.documentFrequency = documentFrequency;
    }
}
