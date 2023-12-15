package unipi.it.mircv.compression;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Encoding {

        public static String toUnary(int number) {
            if (number < 0) {
                throw new IllegalArgumentException("Number must be non-negative");
            }

            StringBuilder unary = new StringBuilder();
            for (int i = 0; i < number; i++) {
                unary.append("1");
            }
            unary.append("0");

            return unary.toString();
        }

        public int fromUnary(String unary) {
            int count = 0;
            for (char c : unary.toCharArray()) {
                if (c == '1') {
                    count++;
                } else {
                    break; // Interrompe il conteggio quando incontra un carattere diverso da '1'
                }
            }
            return count;
        }

    public static int countOnes (ArrayList<String> list) {
        int count = 0; // initialize the counter
        for (String s : list) { // loop over each string in the list
            count += Collections.frequency (Arrays.asList (s.split ("")), "1"); // count the number of 1s in the string and add it to the counter
        }
        return count; // return the final count
    }

    public static ArrayList<String> splitLowerBytes (byte[] byteArray, int n){
        int lastByte = (int) byteArray[byteArray.length - 1];
        ArrayList<String> toReturn = new ArrayList<String>(bytesToBinaryStrings(byteArray));


        if (toReturn.size() >= 2) {
            // Trova il penultimo elemento
            int penultimoIndex = toReturn.size() - 2;

            // Ottieni il penultimo elemento
            String penultimoElemento = toReturn.get(penultimoIndex);

            // Elimina gli ultimi n caratteri dal penultimo elemento
            if (penultimoElemento.length() >= lastByte) {
                String nuovoPenultimoElemento = penultimoElemento.substring(0, penultimoElemento.length() - lastByte);

                // Sostituisci il penultimo elemento con quello modificato
                toReturn.set(penultimoIndex, nuovoPenultimoElemento);
                toReturn.remove(toReturn.size() - 1);
            }
            StringBuilder concatenatedString = new StringBuilder();

            for (String element : toReturn) {
                concatenatedString.append(element);
            }
            toReturn = divideAndInsert(concatenatedString.toString(), n);
        }
                return toReturn;

    }

    public static ArrayList<String> splitHighBytes (byte[] byteArray){
        int lastByte = (int) byteArray[byteArray.length - 1];
        ArrayList<String> toReturn = new ArrayList<String>(bytesToBinaryStrings(byteArray));

            StringBuilder concatenatedString = new StringBuilder();

            for (String element : toReturn) {
                concatenatedString.append(element);
            }

            toReturn = divideStringByZero(concatenatedString);


        System.out.println(toReturn);
        return toReturn;
    }


    private static ArrayList<String> divideStringByZero(StringBuilder inputStringBuilder) {
        ArrayList<String> dividedStrings = new ArrayList<>();
        String inputString = inputStringBuilder.toString();
        // Usando un lookbehind positivo (?<=0)
        ArrayList<String> resultArray = new ArrayList<String>(Arrays.asList(inputString.split("(?<=0)")));




        return resultArray;
    }
    private static ArrayList<String> divideAndInsert(String inputString, int n) {
        ArrayList<String> dividedStrings = new ArrayList<>();
        int partSize = (int) Math.ceil((double) inputString.length() / n);

        for (int i = 0; i < inputString.length(); i += partSize) {
            int endIndex = Math.min(i + partSize, inputString.length());
            dividedStrings.add(inputString.substring(i, endIndex));
        }

        return dividedStrings;
    }
    // Converte un array di byte in una stringa per la stampa
    private static String byteArrayToString(byte[] byteArray) {
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < byteArray.length; i++) {
            result.append(byteArray[i]);
            if (i < byteArray.length - 1) {
                result.append(", ");
            }
        }
        result.append("]");
        return result.toString();
    }



    public static String toBinaryString(int number, int bitLength) {
            if (bitLength <= 0) {
                throw new IllegalArgumentException("bitLength must be positive");
            }

            String binaryString = Integer.toBinaryString(number);
            int paddingLength = bitLength - binaryString.length();

            // Aggiungi zeri all'inizio se la lunghezza della stringa binaria è inferiore a bitLength
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < paddingLength; i++) {
                sb.append("0");
            }
            sb.append(binaryString);

            return sb.toString();
        }


        public static byte[] lowerBinaryStringsToBytes(ArrayList<String> binaryStrings) {
            StringBuilder combinedString = new StringBuilder();
            for (String binary : binaryStrings) {
                combinedString.append(binary);
            }

            int remainder = combinedString.length() % 8;
            if (remainder != 0) {
                // Aggiungi zeri alla fine per completare l'ultimo byte, se necessario
                String padding = "0".repeat(8 - remainder);
                combinedString.append(padding);
            }

            // Aggiungi il numero di zeri aggiunti come un byte alla fine dell'array
            byte paddingCount = (byte) (8 - remainder);
            combinedString.append(String.format("%08d", Integer.parseInt(Integer.toBinaryString(paddingCount))));

            int byteSize = combinedString.length() / 8;
            byte[] bytes = new byte[byteSize];
            for (int i = 0; i < byteSize; i++) {
                String byteString = combinedString.substring(i * 8, (i + 1) * 8);
                bytes[i] = (byte) Integer.parseInt(byteString, 2);
            }
            return bytes;
        }

    public static byte[] highBinaryStringsToBytes(ArrayList<String> binaryStrings)
    {
        StringBuilder combinedString = new StringBuilder();
        for (String binary : binaryStrings) {
            combinedString.append(binary);
        }

        int remainder = combinedString.length() % 8;
        if (remainder != 0) {
            // Aggiungi zeri alla fine per completare l'ultimo byte, se necessario
            String padding = "0".repeat(8 - remainder);
            combinedString.append(padding);
        }

        int byteSize = combinedString.length() / 8;
        byte[] bytes = new byte[byteSize];
        for (int i = 0; i < byteSize; i++) {
            String byteString = combinedString.substring(i * 8, (i + 1) * 8);
            bytes[i] = (byte) Integer.parseInt(byteString, 2);
        }
        return bytes;
    }

    public static ArrayList<String> bytesToBinaryStrings(byte[] bytes) {
        ArrayList<String> binaryStrings = new ArrayList<>();
        StringBuilder combinedBinaryString = new StringBuilder();

        // Converti ogni byte in una stringa binaria di 8 bit
        for (byte b : bytes) {
            String binaryString = String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            combinedBinaryString.append(binaryString);
        }

        // Rimuovi gli zeri finali in eccesso, tranne l'ultimo prima dell'1
        int lastOneIndex = combinedBinaryString.lastIndexOf("1");
        if (lastOneIndex != -1) {
            combinedBinaryString.delete(lastOneIndex + 1, combinedBinaryString.length());
        }

        // Riconverti la stringa binaria combinata nell'array di stringhe binarie
        int startIndex = 0;
        while (startIndex < combinedBinaryString.length()) {
            int endIndex = Math.min(startIndex + 8, combinedBinaryString.length());
            String binaryString = combinedBinaryString.substring(startIndex, endIndex);
            binaryStrings.add(binaryString);
            startIndex = endIndex;
        }
        binaryStrings.set(binaryStrings.size() - 1, binaryStrings.get(binaryStrings.size() - 1).concat("0"));


        return binaryStrings;
    }




        public static void main(String[] args) {
            Encoding encoding = new Encoding();
            System.out.print(encoding.toUnary(1));
        }
    }
