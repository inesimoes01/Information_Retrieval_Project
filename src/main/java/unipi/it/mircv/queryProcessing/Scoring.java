package unipi.it.mircv.queryProcessing;


import java.io.IOException;

import static java.lang.Math.log;


public class Scoring {

    /**
     * TF(t, d) = n / N
     * - n is the number of times term t appears in the document d.
     * - N is the total number of terms in the document d.
     */
    private double computeTF(int nTermInDocument, int nTotalTermsInDocument){
        //System.out.println("TF: " + nTermInDocument + " / " + nTotalTermsInDocument);
        return (double) nTermInDocument/nTotalTermsInDocument;
    }

    /**
     * idf(t, D) = log (N/( n))
     * - N is the number of documents in the data set.
     * - n is the number of documents that contain the term t among the data set.
     */
    private double computeIDF(int nTotalDocs, int nTotalDocsWithTerm){
        //System.out.println("IDF: " + nTotalDocs + " / " + nTotalDocsWithTerm);
        return log((double) nTotalDocs / nTotalDocsWithTerm);
    }


    public double computeTFIDF(String term, int docId) {
        double value = 0.0;
        try {
            OutputResultsReader outputResultsReader = new OutputResultsReader();
            outputResultsReader.searchTermInInvertedIndex(term);
            outputResultsReader.saveTotalNumberDocs();
            outputResultsReader.searchDocIdInDocumentIndex(docId);

            if (outputResultsReader.searchTermInLexicon(term, true)) {
                //System.out.println("TF VALUES "+ outputResultsReader.getTermDocidFreq().get(term).indexOf(docId) + " " + outputResultsReader.getDocumentLens().get(docId));
                value = computeTF(outputResultsReader.getTermDocidFreq().get(term).get(1),
                        outputResultsReader.getDocumentLens().get(docId))*computeIDF(outputResultsReader.getNTotalDocuments(),
                        outputResultsReader.getDocumentFrequency().get(term));
                return value;
            } else return 0;
        }catch (NullPointerException e){
            return 0;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
