package unipi.it.mircv.indexing.dataStructures;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Lexicon {
    private HashMap<String, TermStats> lexicon = new HashMap<>();

    public Lexicon(){
        lexicon = new HashMap<>();
    }

    public void updateLexicon(String term, int freq){
        if(!lexicon.containsKey(term)){
            //Create a new "record" with cf=freq and df=1
            TermStats termStats = new TermStats();
            termStats.setCollectionFrequency(freq);
            termStats.setDocumentFrequency(1);
            lexicon.put(term, termStats);
        }
        else{
            TermStats termStats = lexicon.get(term);
            termStats.setCollectionFrequency(termStats.getCollectionFrequency() + freq);
            termStats.setDocumentFrequency(termStats.getDocumentFrequency()+1);
        }

    }


    public ArrayList<String> sortLexicon(){
        ArrayList<String> sortedTerms = new ArrayList<>(lexicon.keySet());
        Collections.sort(sortedTerms);
        return sortedTerms;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (String term : lexicon.keySet()) {
            TermStats termStats = lexicon.get(term);

            sb.append("Term: ").append(term).append("\n");
            sb.append("Collection Frequency: ").append(termStats.getCollectionFrequency()).append("\n");
            sb.append("Document Frequency: ").append(termStats.getDocumentFrequency()).append("\n\n");
        }

        return sb.toString();
    }

    public HashMap<String, TermStats> getLexicon(){ return lexicon; }


}
