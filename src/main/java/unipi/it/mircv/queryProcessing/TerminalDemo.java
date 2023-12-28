package unipi.it.mircv.queryProcessing;

import unipi.it.mircv.common.Flags;

import java.util.Scanner;

public class TerminalDemo {
    public String runTerminal(){
        Scanner input = new Scanner(System.in);
        String query;

        query = getUserInput(input, "Enter your query: ");

        String processingType = getValidInput(input, "DAAT (d) or MaxScore (m) processing? ", "d", "m");
        Flags.setIsDAAT_flag(processingType.equals("d"));

        String rankingType = getValidInput(input, "TFIDF (t) or BM25 (b) ranking? ", "t", "b");
        Flags.setIsTFIDF_flag(rankingType.equals("t"));

        int numDocuments = getValidNumber(input, "Enter number of documents: ");
        Flags.setNumberOfDocuments(numDocuments);

        return query;
    }

    private String getUserInput(Scanner scanner, String message){
        System.out.print(message);
        return scanner.nextLine();
    }

    private String getValidInput(Scanner scanner, String message, String... validOptions){
        String input;
        boolean isValid;
        do {
            input = getUserInput(scanner, message).toLowerCase();
            isValid = false;
            for (String validOption : validOptions) {
                if (input.equals(validOption)) {
                    isValid = true;
                    break;
                }
            }
            if (!isValid) {
                System.out.println("Invalid input. Please enter a valid character.");
            }
        } while (!isValid);
        return input;
    }

    private static int getValidNumber(Scanner scanner, String message) {
        int num = 0;
        boolean isValid;
        do {
            System.out.print(message);
            String input = scanner.nextLine();
            try {
                num = Integer.parseInt(input);
                if (num >= 0) {
                    isValid = true;
                } else {
                    isValid = false;
                    System.out.println("Please enter a non-negative number.");
                }
            } catch (NumberFormatException e) {
                isValid = false;
                System.out.println("Invalid input. Please enter a valid number.");
            }
        } while (!isValid);
        return num;
    }
}
