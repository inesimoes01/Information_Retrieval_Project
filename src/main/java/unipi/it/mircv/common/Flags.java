package unipi.it.mircv.common;

public class Flags {

    public static void setIsEvaluation(boolean isEvaluation) {
        Flags.isEvaluation = isEvaluation;
    }


    private static boolean isConjunctive_flag;
    private static boolean isEvaluation = true;
    private static boolean isTFIDF_flag = true;
    private static boolean isDAAT_flag = true;
    private static int numberOfDocuments;

    public static void setIsTFIDF_flag(boolean isTFIDF_flag) {
        Flags.isTFIDF_flag = isTFIDF_flag;
    }

    public static void setNumberOfDocuments(int numberOfDocuments) {
        Flags.numberOfDocuments = numberOfDocuments;
    }

    public static void setIsDAAT_flag(boolean isDAAT_flag) {
        Flags.isDAAT_flag = isDAAT_flag;
    }

    public static int getNumberOfDocuments() {
        return numberOfDocuments;
    }

    public static boolean isIsTFIDF_flag() {
        return isTFIDF_flag;
    }

    public static boolean isIsDAAT_flag() {
        return isDAAT_flag;
    }

    public static boolean isIsEvaluation() {
        return isEvaluation;
    }

    public static boolean isIsConjunctive_flag() {
        return isConjunctive_flag;
    }

    public static void setIsConjunctive_flag(boolean isConjunctive_flag) {
        Flags.isConjunctive_flag = isConjunctive_flag;
    }



}
