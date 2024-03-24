package unipi.it.mircv.preprocessing;

public class Preprocessing {
    public String clean(String doc){

        doc = TextCleaner.cleanText(doc);
        doc = RemoveStopWords.removeStopWords(doc);
        doc = Stemmer.stemming(doc);

        return doc;
    }
}
