package unipi.it.mircv.common;


import java.util.ArrayList;
import java.util.HashMap;

public class Lexicon {

    public static class TermStats{
        public int collectionFrequency;//how many times term appearn is collection
        public int documentFrequency;//in how many documents the term appears
    }
    private static HashMap<String, TermStats> lexicon = new HashMap<>();
    public Lexicon(){
        this.lexicon= new HashMap<>();
    }
    public static void updateLexicon(String term, int freq){

        if(!lexicon.containsKey(term)){
            //Create a new "record" with cf=freq and df=1
            TermStats termStats = new TermStats();
            termStats.collectionFrequency = freq;
            termStats.documentFrequency = 1;
            lexicon.put(term, termStats);
        }
        else{
            //Add to the Lexicon
            TermStats termStats = lexicon.get(term);
            termStats.collectionFrequency += freq;
            termStats.documentFrequency++;
        }



    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (String term : lexicon.keySet()) {
            TermStats termStats = lexicon.get(term);

            sb.append("Term: ").append(term).append("\n");
            sb.append("Collection Frequency: ").append(termStats.collectionFrequency).append("\n");
            sb.append("Document Frequency: ").append(termStats.documentFrequency).append("\n\n");
        }

        return sb.toString();
    }

}
