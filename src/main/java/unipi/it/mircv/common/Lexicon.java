package unipi.it.mircv.common;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import unipi.it.mircv.common.TermStats;
public class Lexicon {



    public HashMap<String, TermStats> getLexicon(){ return lexicon; }

    private static HashMap<String, TermStats> lexicon = new HashMap<>();
    public Lexicon(){
        this.lexicon= new HashMap<>();
    }
    public static void updateLexicon(String term, int freq){

        if(!lexicon.containsKey(term)){
            //Create a new "record" with cf=freq and df=1
            TermStats termStats = new TermStats();
            termStats.setCollectionFrequency(freq);
            termStats.setDocumentFrequency(1);
            lexicon.put(term, termStats);
        }
        else{
            //Add to the Lexicon
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

}