package unipi.it.mircv.common;

import java.io.RandomAccessFile;

public class RecuperaRiga {
    private static String recuperaRigaByParola(RandomAccessFile file1, RandomAccessFile file2, String targetParola) throws Exception {
        long posizioneInizialeFile1 = 0;
        long posizioneTargetFile1 = 0;

        String linea;
        while ((linea = file1.readLine()) != null) {
            // Dividi la linea in parole
            String[] parole = linea.split("\\s+");

            // La parola cercata è la prima nella riga
            String parola = parole[0];

            if (parola.contains(targetParola)) {
                // Trovata la parola nel file1, memorizza la posizione e esci dal ciclo
                posizioneTargetFile1 = file1.getFilePointer();
                break;
            }
        }

        // Torna all'inizio del file1
        file1.seek(posizioneInizialeFile1);

        // Posiziona il puntatore nel file1 alla posizione target
        file1.seek(posizioneTargetFile1);

        // Leggi e restituisci la riga desiderata dal file2
        return leggiRigaCorrispondente(file2);
    }

    private static String leggiRigaCorrispondente(RandomAccessFile file) throws Exception {
        // Leggi e restituisci la riga dal file
        return file.readLine();
    }



    public static void main(String[] args) {
        try {
            // Sostituisci "path_al_tuo_file1" con il percorso del tuo primo file
            RandomAccessFile file1 = new RandomAccessFile("data/output/LexiconMerged.txt", "r");

            // Sostituisci "path_al_tuo_file2" con il percorso del tuo secondo file
            RandomAccessFile file2 = new RandomAccessFile("data/output/InvertedIndexMerged.txt", "r");

            String parolaCercata = "manhattan";

            // Cerca la parola nel primo file e recupera la riga corrispondente nel secondo file
            String rigaCorrispondente = recuperaRigaByParola(file1, file2, parolaCercata);

            // Stampa la riga corrispondente nel secondo file
            System.out.println(rigaCorrispondente);

            // Chiudi i file
            file1.close();
            file2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

