package unipi.it.mircv.common;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Unary {

    // Metodo per codificare un numero in unario
    public static String encode(int number) {
        if (number <= 0) {
            throw new IllegalArgumentException("Il numero deve essere maggiore di zero");
        }
        StringBuilder unary = new StringBuilder();
        for (int i = 1; i < number; i++) {
            unary.append("1");
        }
        unary.append("0");
        return unary.toString();
    }

    // Metodo per decodificare una stringa unaria in un numero
    public static int decode(String unary) {
        if (unary == null || !unary.matches("1*0")) {
            throw new IllegalArgumentException("La stringa non è valida");
        }
        return unary.length(); // La lunghezza della stringa unaria è il numero decodificato
    }

    public static ArrayList<String> encodeArrayList(ArrayList<String> strings) {
        return strings.stream()
                .map(s -> encode(Integer.parseInt(s)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // Esempio di utilizzo
    public static void main(String[] args) {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("1");
        strings.add("2");
        strings.add("3");
        strings.add("4");
        strings.add("5");

        ArrayList<String> encodedStrings = encodeArrayList(strings);
        System.out.println("Stringhe codificate in unario: " + encodedStrings);
    }

}

