package unipi.it.mircv.queryProcessing;


import unipi.it.mircv.common.Flags;

import java.io.IOException;


import static java.lang.Math.log;


public class Scoring {

    public double computeScoring(TermDictionary term, DocumentQP docId){
        if (Flags.isIsTFIDF_flag()) return computeTFIDF(term, docId);
        else return computeBM25(term, docId);
    }
    public double computeTermUpperBound(TermDictionary term, DocumentQP docId){
        if (Flags.isIsTFIDF_flag()) return computeTFIDF(term, docId);
        else return computeBM25(term, docId);
    }
    /**
     * TF(t, d) = 1 + log(n)
     * - n is the number of times term t appears in the document d.
     */
    private double computeTF(int nTermInDocument){
        //System.out.println("TF: " + nTermInDocument + " / " + nTotalTermsInDocument);
        //return (double) nTermInDocument/nTotalTermsInDocument;
        if (nTermInDocument > 0) return 1 + log(nTermInDocument);
        else return 0;
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


    private double computeTFIDF(TermDictionary term, DocumentQP doc) {
        double value;
        try {
            OutputResultsReader.saveTotalNumberDocs();
//            System.out.println("TF " + term.getPostingByDocId(term.getPostingList(), doc.getDocId()).getFreq() + " / " + doc.getLength());
//            System.out.println("IDF " + OutputResultsReader.getnTotalDocuments() + " / " + term.getDocumentFrequency());
            value = computeTF(term.getPostingByDocId(term.getPostingList(), doc.getDocId()).getFreq())
                    * computeIDF(OutputResultsReader.getnTotalDocuments(), term.getDocumentFrequency());
            return value;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

//    private double computeTFIDF(TermDictionary term, DocumentQP doc) {
//        double value = 0.0;
//        try {
//            OutputResultsReader.saveTotalNumberDocs();
////            System.out.println("TF " + term.getDocIdFreq().get(doc.getDocId()) + " / " + doc.getLength());
////            System.out.println("IDF " + OutputResultsReader.getnTotalDocuments() + " / " + term.getDocumentFrequency());
//            value = computeTF(term.getPostingList().get(doc.getDocId()), doc.getLength()) * computeIDF(OutputResultsReader.getnTotalDocuments(), term.getDocumentFrequency());
//            return value;
//
 //        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }



    private double computeBM25(TermDictionary term, DocumentQP doc){
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
