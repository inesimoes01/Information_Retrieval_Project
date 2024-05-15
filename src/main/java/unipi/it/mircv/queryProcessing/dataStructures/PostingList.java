package unipi.it.mircv.queryProcessing.dataStructures;

public class PostingList {

    Integer docId;

    Integer freq;

    public PostingList(Integer docId, Integer freq) {
        this.docId = docId;
        this.freq = freq;
    }

    public Integer getDocId() {
        return docId;
    }



    public Integer getFreq() {
        return freq;
    }


}
