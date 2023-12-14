package unipi.it.mircv.queryProcessing;


import unipi.it.mircv.common.Flags;

import java.io.IOException;


public class Ranking {

    // tunable contants
    private final double K1 = 1.5;
    private final double B = 1;

    /**
     * Ranking can be done using TFIDF or BM25
     *
     * @param nTermInDocument number of times term T appears in the document D.
     * @param nTotalDocs total number of documents in the collection
     * @param nTotalDocsWithTerm number of documents that contain the term T in the collection
     * @param lengthDocument length of document D
     * @param avgDocLenCollection average length of the documents in the collection
     *
     */

    public double computeRanking(Integer nTermInDocument, Integer nTotalDocs, Integer nTotalDocsWithTerm, Integer lengthDocument, double avgDocLenCollection){
        if (Flags.isIsTFIDF_flag()) return computeTFIDF(nTermInDocument, nTotalDocs, nTotalDocsWithTerm);
        else return computeBM25(nTermInDocument, nTotalDocs, nTotalDocsWithTerm, lengthDocument, avgDocLenCollection);
    }

    // IDF(q) * TF(q, D)
    private double computeTFIDF(Integer nTermInDocument, Integer nTotalDocs, Integer nTotalDocsWithTerm){
        return computeTF(nTermInDocument) * computeIDF(nTotalDocs, nTotalDocsWithTerm);
    }


    // IDF(q) * TF(q, D) / (TF(q, D) + k1 * (1 — b + b * (|D| / avgdl))
    // D = document length
    // avgdl = average document length in the collection
    private double computeBM25(Integer nTermInDocument, Integer nTotalDocs, Integer nTotalDocsWithTerm, Integer lengthDocument, double avgDocLenCollection){
        return computeIDF(nTotalDocs, nTotalDocsWithTerm) * computeTF(nTermInDocument) / computeTF(nTermInDocument) + K1 * (1 - B + B * ((double) lengthDocument / avgDocLenCollection));
    }




    public double computeRanking_QP(TermDictionary term, DocumentQP docId){
        if (Flags.isIsTFIDF_flag()) return computeTFIDF_QP(term, docId);
        else return computeBM25_QP(term, docId);
    }

    public double computeTermUpperBound(TermDictionary term, DocumentQP docId){
        if (Flags.isIsTFIDF_flag()) return computeTFIDF_QP(term, docId);
        else return computeBM25_QP(term, docId);
    }
    /**
     * TF(t, d) = 1 + log(n)
     * - n is the number of times term t appears in the document d.
     */
    private double computeTF(int nTermInDocument){
        if (nTermInDocument > 0) return 1 + Math.log10(nTermInDocument);
        else return 0;
    }

    /**
     * idf(t, D) = log (N/( n))
     * - N is the number of documents in the data set.
     * - n is the number of documents that contain the term t among the data set.
     */
    private double computeIDF(int nTotalDocs, int nTotalDocsWithTerm) {
        return Math.log10((double) nTotalDocs / nTotalDocsWithTerm);
    }


    private double computeTFIDF_QP(TermDictionary term, DocumentQP doc) {
        double value;
        try {
            OutputResultsReader.saveTotalNumberDocs();
            double TF = computeTF(term.getPostingByDocId(term.getPostingList(), doc.getDocId()).getFreq());
            double IDF = computeIDF(OutputResultsReader.getnTotalDocuments(), term.getDocumentFrequency());
//            System.out.println("TF " + term.getPostingByDocId(term.getPostingList(), doc.getDocId()).getFreq() + " / " + doc.getLength());
//            System.out.println("IDF " + OutputResultsReader.getnTotalDocuments() + " / " + term.getDocumentFrequency());
            return TF * IDF;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private double computeBM25_QP(TermDictionary term, DocumentQP doc){
        return 0.0;
    }

    //            OutputResultsReader outputResultsReader = new OutputResultsReader();
//            outputResultsReader.searchTermInInvertedIndex(term);
//            outputResultsReader.saveTotalNumberDocs();
//            outputResultsReader.searchDocIdInDocumentIndex(docId);


//                //System.out.println("TF VALUES "+ outputResultsReader.getTermDocidFreq().get(term).indexOf(docId) + " " + outputResultsReader.getDocumentLens().get(docId));
//            value = computeTF(outputResultsReader.getTermDocidFreq().get(term).get(1),
//                    outputResultsReader.getDocumentLens().get(docId))*computeIDF(outputResultsReader.getNTotalDocuments(),
//                    outputResultsReader.getDocumentFrequency().get(term));
    //termList.


}
