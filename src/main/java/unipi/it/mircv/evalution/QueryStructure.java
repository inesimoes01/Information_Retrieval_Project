package unipi.it.mircv.evalution;

import java.util.HashMap;
import java.util.Map;

public class QueryStructure {

    public QueryStructure(String query, Integer queryID){
        this.query = query;
        this.queryID = queryID;
    }
    private String query;
    private Integer queryID;

    // documentID and Evalution
    private Map<Integer, Double> documentEval = new HashMap<>();



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

    public Map<Integer, Double> getDocumentEval() {
        return documentEval;
    }

    public void setDocumentEval(Map<Integer, Double> topResults) {
        this.documentEval = topResults;
    }


}
