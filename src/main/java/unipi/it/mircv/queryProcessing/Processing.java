package unipi.it.mircv.queryProcessing;

import unipi.it.mircv.common.Flags;
import unipi.it.mircv.queryProcessing.dataStructures.DocumentQP;
import unipi.it.mircv.queryProcessing.dataStructures.TermDictionary;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Processing {

    public static List<Integer> processingStrategy(List<TermDictionary> termList, String[] query, List<String> termNonExistent) throws IOException {
        if (Flags.isIsConjunctive_flag()) return conjunctiveProcessing(termList, query, termNonExistent);
        else return disjunctiveProcessing(termList, query, termNonExistent);
    }

    public static List<Integer> conjunctiveProcessing(List<TermDictionary> termList, String[] query, List<String> termNonExistent) throws FileNotFoundException {
        ArrayList<DocumentQP> docsWithTerms = new ArrayList<>();
        ArrayList<Integer> docsIDSWithTerms = new ArrayList<>();

        for (String term : query) {
            // fills the term list
            TermDictionary termInstance = OutputResultsReader.fillTermDictionary(termList, term);

            // if query term does not exist in the collection, return null
            if (termInstance == null) {
                System.out.println("Term \"" + term + "\" not found");
                termNonExistent.add(term);
                //docsWithTerms.clear();
                return null;
            } else {
                // save all the IDs from the first term
                if (docsIDSWithTerms.isEmpty()) {
                    List<Integer> docs = termInstance.getDocumentsWithTerm();
                    ArrayList<Integer> aux = new ArrayList<>(docs);
                    docsIDSWithTerms.addAll(aux);
                } else {
                    List<Integer> docs = termInstance.getDocumentsWithTerm();
                    ArrayList<Integer> aux = new ArrayList<>(docs);
                    docsIDSWithTerms.retainAll(aux);
                }
            }
        }

        List<Integer> finalList = new ArrayList<>();

        for (TermDictionary term_aux : termList){
            List<Integer> doc_aux = term_aux.getDocumentsWithTerm();
            for (Integer doc : doc_aux){
                if (docsIDSWithTerms.contains(doc)){
                    finalList.add(doc);
                }
            }
        }

        finalList = removeDuplicates(finalList);
        if (finalList == null || finalList.isEmpty()) return null;
        return finalList;

    }

    public static List<Integer> disjunctiveProcessing(List<TermDictionary> termList, String[] query, List<String> termNonExistent) throws FileNotFoundException {
        List<Integer> docsWithTerms = new ArrayList<>();

        for (String term : query) {
            // fills the term list
            TermDictionary termInstance = OutputResultsReader.fillTermDictionary(termList, term);
            //System.out.println("Term \"" + term + "\"");
            // if query term does not exist in the collection, return null
            if (termInstance == null){
                System.out.println("Term \"" + term + "\" not found");
                termNonExistent.add(term);
            } else {
                // keeps all documents
                docsWithTerms.addAll(termInstance.getDocumentsWithTerm());
            }
        }
        docsWithTerms = removeDuplicates(docsWithTerms);
        if (docsWithTerms == null || docsWithTerms.isEmpty()) return null;
        return docsWithTerms;
    }

    private static List<Integer> removeDuplicates(List<Integer> list) {
        List<Integer> uniqueList = new ArrayList<>();
        boolean exist;
        for (Integer element : list) {
            exist = false;
            for (Integer uniqueElement : uniqueList){
                if (uniqueElement.equals(element)){
                    exist = true;
                    break;
                }
            }
            if (!exist) uniqueList.add(element);
        }
        return uniqueList;
    }
}
