package unipi.it.mircv.common;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import unipi.it.mircv.common.Encoding;

class ToReturn {
    private List<String> highBits;
    private List<String> lowerBits;

    public ToReturn() {
    }

    public void setHighBits(List<String> highBits) {
        this.highBits = highBits;
    }

    public void setLowerBits(List<String> lowerBits) {
        this.lowerBits = lowerBits;
    }

    public List<String> getHighBits() {
        return highBits;
    }

    public List<String> getLowerBits() {
        return lowerBits;
    }
}
public class EliasFano {
    private ArrayList<String> lowerBits = new ArrayList<>();
    private ArrayList<String> highBits = new ArrayList<>();
    private ArrayList<String> encodedListBinary = new ArrayList<>();
    private int l;
    private int h;
    private Encoding encoding = new Encoding();

    public ToReturn eliasFano(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            throw new IllegalArgumentException("Numbers list cannot be null or empty");
        }


        int x=1;
        int n = numbers.size();
        int max = numbers.get(n - 1);
        int nbits= Integer.toBinaryString(max).length();
        this.l = (int) Math.ceil(Math.log(max / n) / Math.log(2));
        this.h = (int) Math.ceil(Math.log(max) / Math.log(2)) - this.l;
        ToReturn toReturn = new ToReturn();


        String previousHighBin = ""; // Inizializza la variabile per memorizzare l'highbin precedente

        for (int number : numbers) {
            String bin = encoding.toBinaryString(number, nbits);
            lowerBits.add(bin.substring(bin.length() - this.l));
            System.out.println(bin);
            String highbin = bin.substring(0, Math.min(bin.length(), this.h));

            if (!highbin.equals(previousHighBin)) {
                if (!previousHighBin.isEmpty()) {
                    highBits.add(encoding.toUnary(x)); // Aggiungi la rappresentazione unaria di x a highBits
                }
                x = 1; // Reimposta x a 1
            } else {
                x++; // Incrementa x
            }
            if(!previousHighBin.equals("")) {
                System.out.println( Integer.parseInt(highbin,2)-Integer.parseInt(previousHighBin, 2));
            }
            if (previousHighBin!="" && Integer.parseInt(highbin,2)-Integer.parseInt(previousHighBin, 2)>1){

                highBits.add("0");
            }
            previousHighBin = highbin; // Aggiorna previousHighBin

        }

// Dopo il ciclo, aggiungi l'ultimo valore di x a highBits
        if (!previousHighBin.isEmpty()) {
            highBits.add(encoding.toUnary(x));
        }


        toReturn.setHighBits(highBits);
        toReturn.setLowerBits(lowerBits);

        return toReturn;
    }


    //Elias Fano Compressor Test
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
        // Usa il metodo select per ottenere il numero all'indice specificato
        // Esempio: int number = eliasFano.select(0);
        System.out.println(eliasFano.eliasFano(numbers).getHighBits() +" "+eliasFano.eliasFano(numbers).getLowerBits());
    }

}

