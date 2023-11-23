package unipi.it.mircv.preprocessing;

public class Preprocessing {
TextCleaner textCleaner = new TextCleaner();
Stemmer stemmer = new Stemmer();
RemoveStopWords removeStopWords = new RemoveStopWords();
    public String clean(String doc){

        doc = textCleaner.cleanText(doc);
        doc = removeStopWords.removeStopWords(doc);
        doc = stemmer.stemming(doc);

        return doc;
    }
}
