package unipi.it.mircv.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ToReturn {
    private ArrayList<String> highBits;
    private ArrayList<String> lowerBits;

    public ToReturn() {
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
    private ArrayList<String> lowerBits = new ArrayList<>();
    private ArrayList<String> highBits = new ArrayList<>();
    private int l;
    private int h;
    private Encoding encoding = new Encoding();

    public ToReturn eliasFano(ArrayList<Integer> numbers) {
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
        ToReturn result = eliasFano.eliasFano(numbers);

        System.out.println("Encoded High Bits: " + result.getHighBits());
        System.out.println("Encoded Lower Bits: " + result.getLowerBits());

    }
}
