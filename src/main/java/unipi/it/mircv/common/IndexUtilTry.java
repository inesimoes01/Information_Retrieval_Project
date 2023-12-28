/*package unipi.it.mircv.common;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;

public class IndexUtilTry {
    static Scanner[] lexiconScanners;
    static Scanner[] invertedIndexScanners;
    static Scanner[] documentIndexScanners;
    static BufferedWriter lexiconWriter;
    static BufferedWriter invertedIndexWriter;
    static BufferedWriter documentIndexWriter;


    static private int ndocs=0;
    static private int totdoclength=0;

    static private double avgdoclength=0;

    public static void setAvgdoclength(double avgdoclength) {
        IndexUtilTry.avgdoclength = avgdoclength;
    }

    public static double getAvgdoclength() {
        return avgdoclength;
    }

    public static void setNdocs(int ndocs) {
        IndexUtilTry.ndocs = ndocs;
    }

    public static int getNdocs() {
        return ndocs;
    }

    public static void setTotdoclength(int totdoclength) {
        IndexUtilTry.totdoclength = totdoclength;
    }

    public static int getTotdoclength() {
        return totdoclength;
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
                System.out.println(t);

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

    public static void pushTermPriorityQueue(String[][] terms, boolean[] scannerFinished, int blockCounter, PriorityQueue priorityQueue){

        for(int i=1; i< blockCounter; i++){
        terms[i][4]= i + "";
            if(!scannerFinished[i]){
                priorityQueue.add(terms[i]);
            }
        }

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
        boolean[] scannerToRead = new boolean[blockCounter];
        boolean[] scannerFinished = new boolean[blockCounter];
        for (int i = 1; i < blockCounter; i++) {
            scannerToRead[i] = true;
            scannerFinished[i] = false;
        }
        String[][] terms = new String[blockCounter][];

        openScanners(blockCounter);
        lexiconWriter = new BufferedWriter(new FileWriter("data/output/LexiconMerged.txt"));
        invertedIndexWriter = new BufferedWriter(new FileWriter("data/output/InvertedIndexMerged.txt"));
        documentIndexWriter = new BufferedWriter(new FileWriter("data/output/DocumentIndexMerged.txt"));

        // Merge DocIndex
        mergeDocumentIndex(blockCounter);

        // ...

        while (true) {
            // Advance pointer of each block to the next line
            advancePointers(lexiconScanners, invertedIndexScanners, scannerToRead, terms, scannerFinished);

            if (!continueMerging(scannerFinished, blockCounter)) {
                break;
            }

            pushTermPriorityQueue(terms, scannerFinished, blockCounter, priorityQueue);
            System.out.println(priorityQueue.size());

for (int i=1; i<blockCounter; i++) {
    if (!priorityQueue.isEmpty()) {
        String[] current = priorityQueue.poll();
        if (lexiconScanners[i].hasNextLine() && !scannerFinished[i]) {
            String line = lexiconScanners[i].nextLine() + " x x";
            String line2 = invertedIndexScanners[i].nextLine();
            terms[i] = line.split(" ") ;
            terms[i][3] = line2;
            priorityQueue.add(terms[i]);
        }
        else{
            scannerFinished[i] = true;
        }

        next = priorityQueue.peek();
        if (next != null && next[0].equals(current[0])) {
            current[1] = current[1].concat(" "+ next[1]);
            current[2] = current[2].concat(" "+ next[2]);
            current[3] = current[3].concat(" "+ next[3]);

            priorityQueue.poll(); // Remove the next element
            priorityQueue.add(current);

            }else {
            lexiconWriter.write(current[0] + "   " + current[1] + "   " + current[2] + "   " + current[3]+"   " + current[4]);
            lexiconWriter.newLine();
        }
        }
    }


        }


// Close the writers
        lexiconWriter.close();

    }



    public static void main(String[] args) throws IOException {

        SPIMIAlgorithm(52);

    }
}*/