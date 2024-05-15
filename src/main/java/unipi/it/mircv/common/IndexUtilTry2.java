package unipi.it.mircv.common;

import java.io.*;
import java.util.*;

public class IndexUtilTry2 {
    private static Scanner[] lexiconScanners;
    private static Scanner[] invertedIndexScanners;
    private static Scanner[] documentIndexScanners;
    private static BufferedWriter lexiconWriter;
    private static BufferedWriter invertedIndexWriter;
    private static BufferedWriter documentIndexWriter;


    static private int ndocs=0;
    static private int totdoclength=0;

    static private double avgdoclength=0;

    public static void setAvgdoclength(double avgdoclength) {
        IndexUtilTry2.avgdoclength = avgdoclength;
    }

    public static double getAvgdoclength() {
        return avgdoclength;
    }

    public static void setNdocs(int ndocs) {
        IndexUtilTry2.ndocs = ndocs;
    }

    public static int getNdocs() {
        return ndocs;
    }

    public static void setTotdoclength(int totdoclength) {
        IndexUtilTry2.totdoclength = totdoclength;
    }

    public static int getTotdoclength() {
        return totdoclength;
    }
    private static TreeMap<String, String> sortMap = new TreeMap<>(new Comparator<String>() {
        @Override
        public int compare(String s1, String s2) {
            // Converte le stringhe in numeri e confronta
            int num1 = Integer.parseInt(s1);
            int num2 = Integer.parseInt(s2);
            return Integer.compare(num1, num2);
        }});

    public static TreeMap<String, String> getSortMap() {
        return sortMap;
    }

    public static void setSortMap(TreeMap<String, String> sortMap) {
        IndexUtilTry2.sortMap = sortMap;
    }

    public static void openScanners(int blockCounter) {
        lexiconScanners = new Scanner[blockCounter];

        for (int i = 1; i < blockCounter; i++) {
            try {
                lexiconScanners[i] = new Scanner(new File("data/output/Lexicon" + i + ".txt"));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }


        invertedIndexScanners = new Scanner[blockCounter];
        documentIndexScanners = new Scanner[blockCounter];
        for (int i = 1; i < blockCounter; i++) {
            try {
                invertedIndexScanners[i] = new Scanner(new File("data/output/InvertedIndex" + i + ".txt"));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                documentIndexScanners[i] = new Scanner(new File("data/output/DocumentIndex" + i + ".txt"));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void mergeDocumentIndex(int blockCounter) {
        try {
            // Apertura del BufferedWriter all'interno del try-with-resources per assicurare la chiusura automatica

            for (int i = 1; i < blockCounter; i++) {
                // Apertura del Scanner all'interno del try-with-resources per assicurare la chiusura automatica

                while (documentIndexScanners[i].hasNextLine()) {
                    String line = documentIndexScanners[i].nextLine();
                    setTotdoclength(  getTotdoclength() + Integer.parseInt(line.split("\\s+")[1]));
                    documentIndexWriter.write(line);
                    documentIndexWriter.newLine();
                    setNdocs(getNdocs()+1);

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (getNdocs()!=0) {
            setAvgdoclength(getTotdoclength() / getNdocs());
        }
    }

    public static void updatePointers(Scanner[] textReaders, Scanner[] textReaders2, boolean[] activeScanners, String[][] wordLists, boolean[] completedScanners) {
        int numberOfScanners = textReaders.length;
        int t = 0;

        for (int i = 1; i < numberOfScanners; i++) {  // Parti da 0 invece che da 1
            if (!completedScanners[i]) {

                t++;


                // Aggiungi questa riga per attivare la lettura dal nuovo blocco


                String line = textReaders[i].nextLine() + " x";
                String line2 = textReaders2[i].nextLine();
                String[] words = line2.split("\\s+");
                StringBuilder evens = new StringBuilder();
                StringBuilder odds = new StringBuilder();

                for (int j = 1; j < words.length; j++) {
                    int number = Integer.parseInt(words[j]);

                    if (j % 2 == 0) {
                        evens.append(number).append(" ");
                    } else {
                        odds.append(number).append(" ");
                    }
                }

                String output = odds.toString().trim() + " " + evens.toString().trim();
                wordLists[i] = line.split("\\s+");
                wordLists[i][3] = output;
            } else {
                completedScanners[i] = true;
                activeScanners[i] = false;  // Disattiva la lettura dal blocco corrente
            }
        }
    }

    public static void advancePointers(Scanner[] textScanners, Scanner[] textScanners2, boolean[] scannerToRead, String[][] terms, boolean[] scannerFinished){
        int blockCounter= textScanners.length;
        for(int i = 1; i<blockCounter; i++){
            if(scannerToRead[i]) {
                if(textScanners[i].hasNextLine() ){



                    String line = textScanners[i].nextLine() + " x x";
                    String line2 = textScanners2[i].nextLine();
                    terms[i] = line.split(" ") ;
                    terms[i][3] = line2;
                }
                else{
                    scannerFinished[i] = true;
                }
            }
        }
    }



    public static boolean continueMerging(boolean[] scannerFinished, int blockCounter){
        boolean continueMerging;
        continueMerging = false;
        for(int i = 1; i< blockCounter; i++){
            if (!scannerFinished[i]) {
                continueMerging = true;
                break;
            }
        }
        return continueMerging;
    }

    public static void pushTermsPriorityQueue(String[][] terms, boolean[] scannerFinished, int blockCounter, PriorityQueue priorityQueue){

        for(int i=1; i< blockCounter; i++){
            terms[i][4]= i + "";
            if(!scannerFinished[i]){
                priorityQueue.add(terms[i]);
            }
        }

    }

    public static boolean availableScanners(Scanner[] scanners){
        int availableCount=1;

        for (int i=1; i<scanners.length; i++){
            if(!scanners[i].hasNext()){
            availableCount++;
        }

        if (availableCount==scanners.length){
            return false;
        }
        }
        return true;
    }
    public static void pushTermPriorityQueue(String[][] terms ,String line,String line2, int blockIndex, PriorityQueue priorityQueue){
        terms[blockIndex] = line.split(" ");
        terms[blockIndex][3] = line2;
        terms[blockIndex][4] = blockIndex + "";
        priorityQueue.add(terms[blockIndex]);
    }
    public static String parseInvertedIndex(String input, boolean sorting) {
        // Dividi la stringa in array di stringhe
        input= input.trim();
        String[] elements = input.split("\\s+");

        // Rimuovi il primo elemento
        String[] remainingElements = Arrays.copyOfRange(elements, 1, elements.length);
        // Ordina gli elementi di posto dispari


        // Ricostruisci la stringa di output
        String output = "";
        for (int i=0; i<remainingElements.length ;i++) {
            output= output.concat(remainingElements[i] + " "); // Aggiungi il primo elemento
        }
        if (sorting==true){
        output= sortPostingList(output);}

        return output.toString().trim(); // Rimuovi spazi in eccesso e restituisci la stringa di output
    }
    public static String sortPostingList(String input) {


        // Dividi la stringa in array di stringhe
        String[] elements = input.split("\\s+");

        // Inserisci ogni elemento nella TreeMap
        for (int i = 0; i < elements.length; i += 2) {
            // Verifica che ci siano abbastanza elementi
            if (i + 1 < elements.length) {
                getSortMap().put(elements[i], elements[i + 1]);
            }
        }

        // Costruisci la stringa di output
        StringBuilder output = new StringBuilder();
        for (Map.Entry<String, String> entry : getSortMap().entrySet()) {
            output.append(entry.getKey()).append(" ").append(entry.getValue()).append(" ");
        }


        return output.toString().trim(); // Rimuovi spazi in eccesso e restituisci la stringa di output
    }

    public static void SPIMIAlgorithm(int blockCounter) throws IOException {
        PriorityQueue<String[]> priorityQueue = new PriorityQueue<>(new Comparator<String[]>() {
            @Override
            public int compare(String[] row1, String[] row2) {
                // Assuming the first elements are strings, you can compare them
                return row1[0].compareTo(row2[0]);
            }
        });

        String[] next = null;
        String[] current= null;
        String[] toWrite=new String[5];
        for (int i=0; i<5; i++){
            if (i!=1 && i!=2){
                toWrite[i]="";
            }else {toWrite[i]="0";}
        }
        String line="";
        String line2="";
        boolean[] scannerToRead = new boolean[blockCounter];
        boolean[] scannerFinished = new boolean[blockCounter];
        long offset=0;
        for (int i = 1; i < blockCounter; i++) {
            scannerToRead[i] = true;
            scannerFinished[i] = false;
        }
        String[][] terms = new String[blockCounter][];

        openScanners(blockCounter);
        lexiconWriter = new BufferedWriter (new FileWriter("data/output/LexiconMerged.txt"));
        invertedIndexWriter = new BufferedWriter (new FileWriter("data/output/InvertedIndexMerged.txt"));
        documentIndexWriter = new BufferedWriter (new FileWriter("data/output/DocumentIndexMerged.txt"));

        // Merge DocIndex
        mergeDocumentIndex(blockCounter);

        // ...

        advancePointers(lexiconScanners, invertedIndexScanners, scannerToRead, terms, scannerFinished);
        pushTermsPriorityQueue(terms, scannerFinished,blockCounter,priorityQueue);

        while(availableScanners(lexiconScanners)) {
            //advancePointers(lexiconScanners, invertedIndexScanners, scannerToRead, terms, scannerFinished);
            if (!priorityQueue.isEmpty()) {
                current = priorityQueue.poll();
                next = priorityQueue.peek();


                if (lexiconScanners[Integer.parseInt(current[4])].hasNext() && invertedIndexScanners[Integer.parseInt(current[4])].hasNext()) {
                    line = lexiconScanners[Integer.parseInt(current[4])].nextLine() + " x x";
                    line2 = invertedIndexScanners[Integer.parseInt(current[4])].nextLine();
                    terms[Integer.parseInt(current[4])] = line.split(" ");
                    terms[Integer.parseInt(current[4])][3] = line2;
                    terms[Integer.parseInt(current[4])][4] = current[4];

                    pushTermPriorityQueue(terms, line, line2, Integer.parseInt(current[4]), priorityQueue);
                }


                if (next != null && current[0].equals(next[0])) {

                    if (!toWrite[2].equals("")){
                        toWrite[1] = String.valueOf(Integer.parseInt(toWrite[1].trim()) + Integer.parseInt(next[1].trim()));
                        toWrite[2] = String.valueOf(Integer.parseInt(toWrite[2].trim()) + Integer.parseInt(next[2].trim()));
                        toWrite[3]= toWrite[3].concat(" ").concat(parseInvertedIndex(next[3], false));
                    }

                } else {

                    toWrite[3]= parseInvertedIndex(toWrite[3],true);


                    lexiconWriter.write(toWrite[0] + "   " + toWrite[1] + "   " + toWrite[2] + "   " + offset);
                    lexiconWriter.newLine();
                    invertedIndexWriter.write(toWrite[3]);
                    invertedIndexWriter.newLine();
                    offset += toWrite[3].length() + System.lineSeparator().length();
                    setSortMap(new TreeMap<>(new Comparator<String>() {
                        @Override
                        public int compare(String s1, String s2) {
                            // Converte le stringhe in numeri e confronta
                            int num1 = Integer.parseInt(s1);
                            int num2 = Integer.parseInt(s2);
                            return Integer.compare(num1, num2);
                        }
                    }));

                        toWrite = priorityQueue.peek();

            }

        }}
        while (!priorityQueue.isEmpty()) {
            current = priorityQueue.poll();
                toWrite = current;


            toWrite[3]= parseInvertedIndex(toWrite[3],true);


            lexiconWriter.write(toWrite[0] + "   " + toWrite[1] + "   " + toWrite[2] + "   " + offset );
            lexiconWriter.newLine();
            invertedIndexWriter.write(toWrite[3]);
            invertedIndexWriter.newLine();
            offset += toWrite[3].length() + System.lineSeparator().length();
        }
        priorityQueue.forEach(elemento -> {
            System.out.print("[");
            System.out.print(String.join(", ", elemento));
            System.out.println("]");});
// Close the writers
        lexiconWriter.close();
        invertedIndexWriter.close();
    }



    public static void main(String[] args) throws IOException {

        SPIMIAlgorithm(52);

    }
}