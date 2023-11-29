package unipi.it.mircv.queryProcessing;


import static java.lang.Math.log;

////////// i need total number of documents and length of each document

public class Scoring {

    /**
     * TF(t, d) = n / N
     * - n is the number of times term t appears in the document d.
     * - N is the total number of terms in the document d.
     *
     * @param term The term for which TF is calculated.
     * @param nTermInDocument The number of times term t appears in the document d
     * @return The calculated TF score for the term in the document.
     */
    private double computeTF(String term, int nTermInDocument){
        int nTotalTermsInDocument = 111111;
        return (float) nTermInDocument /nTotalTermsInDocument;
    }

    /**
     * idf(t, D) = log (N/( n))
     * - N is the number of documents in the data set.
     * - n is the number of documents that contain the term t among the data set.
     *
     * @param term The term for which TF is calculated.
     * @param nTotalDocsWithTerm The number of documents that contain the term t among the data set.
     * @return The calculated TF score for the term in the document.
     */
    private double computeIDF(String term, int nTotalDocsWithTerm){
        int nTotalDocs = 111111;
        return log((double) nTotalDocs / nTotalDocsWithTerm);
    }


    public double computeTFIDF(String term, int docId){
        FileReader fileReader = new FileReader();
        if(fileReader.searchTermInFile(term)) {
            return computeIDF(term, fileReader.getDocumentFrequency()) * computeTF(term, fileReader.getFrequenciesByDocId().get(docId));
        }else return 0;
    }


}
