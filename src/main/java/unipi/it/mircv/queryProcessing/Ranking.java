package unipi.it.mircv.queryProcessing;


import unipi.it.mircv.common.Flags;
import unipi.it.mircv.common.Paths;
import unipi.it.mircv.common.dataStructures.TermDictionary;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


public class Ranking {

    // tunable contants
    private static final double K1 = 1.5;
    private static final double B = 1;

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

    public double computeRanking(Integer nTermInDocument, Integer nTotalDocs, Integer nTotalDocsWithTerm, Integer lengthDocument, double avgDocLenCollection) {
        if (Flags.isIsTFIDF_flag()) return computeTFIDF(nTermInDocument, nTotalDocs, nTotalDocsWithTerm);
        else return computeBM25(nTermInDocument, nTotalDocs, nTotalDocsWithTerm, lengthDocument, avgDocLenCollection);
    }

    // IDF(q) * TF(q, D)
    private double computeTFIDF(Integer nTermInDocument, Integer nTotalDocs, Integer nTotalDocsWithTerm) {
        return computeTF(nTermInDocument) * computeIDF(nTotalDocs, nTotalDocsWithTerm);
    }

    // IDF(q) * TF(q, D) / (TF(q, D) + k1 * (1 — b + b * (|D| / avgdl))
    private double computeBM25(Integer nTermInDocument, Integer nTotalDocs, Integer nTotalDocsWithTerm, Integer lengthDocument, double avgDocLenCollection){
        return computeIDF(nTotalDocs, nTotalDocsWithTerm) * computeTF(nTermInDocument) / computeTF(nTermInDocument) + K1 * (1 - B + B * ((double) lengthDocument / avgDocLenCollection));
    }

    // TF(t, d) = 1 + log(n)
    private static double computeTF(int nTermInDocument){
        if (nTermInDocument > 0) return 1 + Math.log10(nTermInDocument);
        else return 0;
    }

    // idf(t, D) = log (N/( n))
    private static double computeIDF(int nTotalDocs, int nTotalDocsWithTerm) {
        return Math.log10((double) nTotalDocs / nTotalDocsWithTerm);
    }


    public static double computeRanking_QP(TermDictionary term, int freq, int docLen) throws IOException {
        if (Flags.isIsTFIDF_flag()) return computeTFIDF_QP(term, freq);
        else return computeBM25_QP(term, freq, docLen);
    }

    private static double computeTFIDF_QP(TermDictionary term, int freq) {
        // number of times term t appears in the document d.
        int nTermInDocument = freq;
        // total number of documents in the data set.
        int nTotalDocs = OutputResultsReader.getnTotalDocuments();
        // number of documents that contain the term t among the data set
        int nTotalDocsWithTerm = term.getDocumentFrequency();

        double TF;
        double IDF;

        if (nTermInDocument > 0) TF = 1 + Math.log10(nTermInDocument);
        else TF = 0;
        IDF = Math.log10((double) nTotalDocs / nTotalDocsWithTerm);

        return TF * IDF;

//            System.out.println("TF " + term.getPostingByDocId(term.getPostingList(), doc.getDocId()).getFreq() + " / " + doc.getLength());
//            System.out.println("IDF " + OutputResultsReader.getnTotalDocuments() + " / " + term.getDocumentFrequency());


    }

    private static double computeBM25_QP(TermDictionary term, int freq, int docLen) throws IOException {
        int nTotalDocs = OutputResultsReader.getnTotalDocuments();
        int nTotalDocsWithTerm = term.getDocumentFrequency();
        int nTermInDocument = freq;
        int lengthDocument = docLen;
        double avgDocLenCollection = OutputResultsReader.getAverageDocLen();

        //System.out.println("BM25 " + computeIDF(nTotalDocs, nTotalDocsWithTerm) + " * " + computeTF(nTermInDocument) + " / " + computeTF(nTermInDocument) + " + " + K1 + " * (1 - " + B + " + " + B + " * (" + lengthDocument + " / " + avgDocLenCollection);
        return computeIDF(nTotalDocs, nTotalDocsWithTerm) * computeTF(nTermInDocument) / computeTF(nTermInDocument) + K1 * (1 - B + B * ((double) lengthDocument / avgDocLenCollection));

    }


}
