package unipi.it.mircv.evalution;

import unipi.it.mircv.common.dataStructures.TopDocuments;
import unipi.it.mircv.common.dataStructures.TopDocumentsComparator;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class QueryStructure {

    public QueryStructure(String query, Integer queryID){
        this.query = query;
        this.queryID = queryID;
    }
    private String query;
    private Integer queryID;

    // documentID and Evalution
    private PriorityQueue<TopDocuments> documentEval = new PriorityQueue<>(new TopDocumentsComparator());


    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getQueryID() {
        return queryID;
    }

    public void setQueryID(Integer queryID) {
        this.queryID = queryID;
    }

    public PriorityQueue<TopDocuments> getDocumentEval() {
        return documentEval;
    }

    public void setDocumentEval(PriorityQueue<TopDocuments> topResults) {
        this.documentEval = topResults;
    }


}
