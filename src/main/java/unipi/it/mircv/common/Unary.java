package unipi.it.mircv.common;

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

    // Esempio di utilizzo
    public static void main(String[] args) {
        int number = 7;
        String encoded = encode(number);
        System.out.println("Numero codificato in unario: " + encoded);

        int decoded = decode(encoded);
        System.out.println("Numero decodificato dall'unario: " + decoded);
    }
}

