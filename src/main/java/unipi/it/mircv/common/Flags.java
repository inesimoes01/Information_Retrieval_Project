package unipi.it.mircv.common;

public class Flags {
    private static boolean flagIsConjunctive = false;
    private static boolean flagIsTFIDF;
    private static boolean flagIsASCII;
    private static int numberOfDocuments;


    public static void setFlagIsConjunctive(boolean flagIsConjunctive) {
        Flags.flagIsConjunctive = flagIsConjunctive;
    }

    public static void setFlagIsTFIDF(boolean flagIsTFIDF) {
        Flags.flagIsTFIDF = flagIsTFIDF;
    }

    public static void setFlagIsASCII(boolean flagIsASCII) {
        Flags.flagIsASCII = flagIsASCII;
    }

    public static void setNumberOfDocuments(int numberOfDocuments) {
        Flags.numberOfDocuments = numberOfDocuments;
    }


    public static boolean isConjunctive() {
        return flagIsConjunctive;
    }

    public static boolean isTFIDF() {
        return flagIsTFIDF;
    }

    public static boolean isASCII() {
        return flagIsASCII;
    }

    public static int getNumberOfDocuments() {
        return numberOfDocuments;
    }

}
