package unipi.it.mircv.compression;

import unipi.it.mircv.common.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

class ToReturn {
    private ArrayList<String> highBits;
    private ArrayList<String> lowerBits;
    private int l;
    private int h;

    public ToReturn() {
    }
    public ToReturn(int h, int l) {
        this.l=l;
        this.h=l;
    }



    public void setHighBits(ArrayList<String> highBits) {
        this.highBits = highBits;
    }

    public void setLowerBits(ArrayList<String> lowerBits) {
        this.lowerBits = lowerBits;
    }

    public ArrayList<String> getHighBits() {
        return highBits;
    }

    public ArrayList<String> getLowerBits() {
        return lowerBits;
    }
}


public class EliasFano {
    private static Util util= new Util();
    private ArrayList<String> lowerBits = new ArrayList<>();
    private ArrayList<String> highBits = new ArrayList<>();
    private int l;
    private int h;

    public void setHighBits(ArrayList<String> highBits) {
        this.highBits = highBits;
    }

    public void setLowerBits(ArrayList<String> lowerBits) {
        this.lowerBits = lowerBits;
    }

    public ArrayList<String> getHighBits() {
        return highBits;
    }

    public ArrayList<String> getLowerBits() {
        return lowerBits;
    }

    public void setH(int h) {
        this.h = h;
    }

    public int getH() {
        return h;
    }

    public void setL(int l) {
        this.l = l;
    }

    public int getL() {
        return l;
    }

    private static Encoding encoding = new Encoding();

    public ToReturn encode(ArrayList<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            throw new IllegalArgumentException("Numbers list cannot be null or empty");
        }

        lowerBits.clear();
        highBits.clear();

        int n = numbers.size();
        int max = Collections.max(numbers);
        int nbits = Integer.toBinaryString(max).length();

        this.l = (int) Math.ceil(Math.log(max / n) / Math.log(2));
        this.h = (int) Math.ceil(Math.log(max) / Math.log(2)) - this.l;

        ToReturn toReturn = new ToReturn();
        ArrayList<String> tempHighBits = new ArrayList<>();

        for (int number : numbers) {
            String bin = encoding.toBinaryString(number, nbits);
            lowerBits.add(bin.substring(bin.length() - this.l));
            String highbin = bin.substring(0, Math.min(bin.length(), this.h));
            tempHighBits.add(highbin);
        }

        for (int i = 0; i <= Integer.parseInt(tempHighBits.get(tempHighBits.size() - 1), 2); i++) {
            highBits.add(encoding.toUnary(Collections.frequency(tempHighBits, encoding.toBinaryString(i, this.h))));
        }

        toReturn.setHighBits(highBits);
        toReturn.setLowerBits(lowerBits);

        return toReturn;
    }

    public static ArrayList<String> decode(ArrayList<String> highBits, ArrayList<String> lowBits) {
        Encoding encoding = new Encoding();
        EliasFano eliasFano = new EliasFano();
        int l= lowBits.get(0).length() ;
        int lastIndex = highBits.size() - 1;

// Converti l'indice in una stringa binaria
        String binaryString = Integer.toBinaryString(lastIndex);
// Calcola la lunghezza della stringa binaria
        int h = binaryString.length();

        int decodedNumber;
        ArrayList<String> decodedNumbers = new ArrayList<>();
        ArrayList<String> highBitsDecoded = new ArrayList<>();
        //calcolo highbits
        int j = 0;
        for (String highBit : highBits) {

            for (int i = 0; i < encoding.fromUnary(highBit); i++) {
                highBitsDecoded.add(encoding.toBinaryString(j, h));
            }
            j++;

        }
        decodedNumbers = util.mergeArrayLists(highBitsDecoded, lowBits);
        decodedNumbers.replaceAll(element -> Integer.toString(Integer.parseInt(element, 2)));
        return decodedNumbers;
    }






    // Elias Fano Compressor Test
    public static void main(String[] args) {
        ArrayList<Integer> numbers = new ArrayList<>();
        numbers.add(3);
        numbers.add(4);
        numbers.add(7);
        numbers.add(13);
        numbers.add(14);
        numbers.add(15);
        numbers.add(21);
        numbers.add(25);
        numbers.add(36);
        numbers.add(38);
        numbers.add(54);
        numbers.add(62);

        EliasFano eliasFano = new EliasFano();
        ToReturn result = eliasFano.encode(numbers);
        byte[] x = encoding.highBinaryStringsToBytes(result.getHighBits());
        byte[] y = encoding.lowerBinaryStringsToBytes(result.getLowerBits());

        System.out.println("Encoded High Bits: " + result.getHighBits());
        System.out.println("Encoded High Bits: " + Arrays.toString(x));
        System.out.println("split hb: " + encoding.splitHighBytes(x));



        System.out.println("Encoded High Bits: " + result.getLowerBits());
        System.out.println("Encoded Lower Bits: " + Arrays.toString(y));
        System.out.println("Decoded High Bits: " + encoding.splitLowerBytes(encoding.lowerBinaryStringsToBytes(result.getLowerBits()) , encoding.countOnes(result.getHighBits())));
//DONE THE OUTPUT IS AN ARRAY OF STRING.
        ArrayList<String> highBits = new ArrayList<>(result.getHighBits());
        ArrayList<String> lowerBits = new ArrayList<>(result.getLowerBits());

        ArrayList<String> decodedNumbers = EliasFano.decode(highBits,lowerBits);

        // Stampiamo i numeri decodificati
        System.out.println("Decoded Numbers: " + decodedNumbers);
    }
}



