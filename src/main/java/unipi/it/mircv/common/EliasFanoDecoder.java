package unipi.it.mircv.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


    public class EliasFanoDecoder {

        private int l;
        private int h;

        public EliasFanoDecoder(int l, int h) {
            this.l = l;
            this.h = h;
        }

        public ArrayList<String> decode(ArrayList<String> highBits,ArrayList<String> lowBits) {
            Encoding encoding = new Encoding();

            int decodedNumber;
            ArrayList<String> decodedNumbers = new ArrayList<>();
            ArrayList<String> highBitsDecoded = new ArrayList<>();
            //calcolo highbits
            int j=0;
            for (String highBit : highBits){

                for (int i=0;i<encoding.fromUnary(highBit);i++){
                    System.out.println(j);
                    highBitsDecoded.add(encoding.toBinaryString(j,this.h));
                }
                j++;

            }
            decodedNumbers = mergeArrayLists(highBitsDecoded,lowBits);
            decodedNumbers.replaceAll(element -> Integer.toString(Integer.parseInt(element, 2)));
            return decodedNumbers;
        }

        public static ArrayList<String> mergeArrayLists(ArrayList<String> list1, ArrayList<String> list2) {
            if (list1 == null || list2 == null) {
                throw new IllegalArgumentException("Le liste non possono essere nulli");
            }

            if (list1.size() != list2.size()) {
                throw new IllegalArgumentException("Le liste devono avere la stessa dimensione");
            }

            ArrayList<String> mergedList = new ArrayList<>();
            for (int i = 0; i < list1.size(); i++) {
                mergedList.add(list1.get(i) + "" + list2.get(i));
            }
            return mergedList;
        }
        public static void main(String[] args) {
            // Assumiamo che questi siano i valori di l e h usati per la codifica
            int l = 3; // Numero di bit per la parte inferiore (lowerBits)
            int h = 3; // Numero di bit per la parte superiore (highBits)
            //Possibilità di essere calcolati automaticamente. (To Fix)


            // Creiamo un oggetto ToReturn con i dati codificati (esempio fittizio)
            ToReturn encodedData = new ToReturn();
            ArrayList<String> highBits = new ArrayList<>(Arrays.asList("1110", "1110", "10", "10", "110", "0", "10", "10"));
            ArrayList<String> lowerBits = new ArrayList<>(Arrays.asList("011", "100", "111", "101", "110", "111", "101", "001", "100", "110", "110", "110"));
            encodedData.setHighBits(highBits);
            encodedData.setLowerBits(lowerBits);

            // Creiamo un decodificatore e decodifichiamo i dati
            EliasFanoDecoder decoder = new EliasFanoDecoder(l, h);
            ArrayList<String> decodedNumbers = decoder.decode(encodedData.getHighBits(),encodedData.getLowerBits());

            // Stampiamo i numeri decodificati
            System.out.println("Decoded Numbers: " + decodedNumbers);
        }
    }

