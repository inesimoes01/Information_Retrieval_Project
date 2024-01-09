package unipi.it.mircv;


import unipi.it.mircv.common.Flags;
import unipi.it.mircv.common.Paths;
import unipi.it.mircv.evalution.Evaluation;
import unipi.it.mircv.indexing.Reader;
import unipi.it.mircv.queryProcessing.QueryProcessing;

import java.io.IOException;
import java.util.Scanner;

public class App
{
    public static void main(String[] args) throws IOException {
        boolean isValid = false;
        Scanner input = new Scanner(System.in);
        String message;

        label:
        do {
            System.out.print("Do you want to do Indexing (i), Query Processing (q) or Evaluation (e)? ");
            message = input.nextLine().toLowerCase();
            switch (message) {
                case "i":
                    isValid = true;
                    Reader.processCollection(Paths.PATH_COLLECTION);
                    break label;
                case "q":
                    isValid = true;
                    Flags.setIsEvaluation(false);
                    QueryProcessing.mainQueryProcessing();
                    break label;
                case "e":
                    isValid = true;
                    Flags.setIsEvaluation(true);
                    QueryProcessing.mainQueryProcessing();
                    break label;
            }
            if (!isValid) {
                System.out.println("Invalid input. Please enter a valid character.");
            }
        } while (!isValid);

    }
}



