package unipi.it.mircv.evalution;

import java.util.HashMap;

public class QueryStructure {

    public QueryStructure(String query, Integer queryID){
        this.query = query;
        this.queryID = queryID;
    }
    private String query;
    private Integer queryID;
    private HashMap<Integer, Double> documentEval = new HashMap<>();
    // documentID and Evalution


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

    public HashMap<Integer, Double> getDocumentEval() {
        return documentEval;
    }

    public void setDocumentEval(Integer docID, double score) {

        this.documentEval.put(docID, score);
    }


}
