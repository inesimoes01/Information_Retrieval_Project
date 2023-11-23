package unipi.it.mircv.preprocessing;

public class Tokenization {

        public static String[] tokenize(String text){ // Se il testo è vuoto o nullo, ritorna un array vuoto
            if (text == null || text.isEmpty()) { return new String[0]; } // Altrimenti, usa il metodo split della classe String per dividere il testo in base agli spazi
            return text.split("\\s+"); }


}


