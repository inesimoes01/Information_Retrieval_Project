package unipi.it.mircv.common;

public class Flags {
    //private static boolean isConjunctive_flag;
    private static boolean isTFIDF_flag;
    private static boolean isASCII_flag;

    private static boolean isDAAT_flag;
    private static int numberOfDocuments;


//    public static void setIsConjunctive_flag(boolean isConjunctive_flag) {
//        Flags.isConjunctive_flag = isConjunctive_flag;
//    }

    public static void setIsTFIDF_flag(boolean isTFIDF_flag) {
        Flags.isTFIDF_flag = isTFIDF_flag;
    }

    public static void setIsASCII_flag(boolean isASCII_flag) {
        Flags.isASCII_flag = isASCII_flag;
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

//    public static boolean isIsConjunctive_flag() {
//        return isConjunctive_flag;
//    }

    public static boolean isIsTFIDF_flag() {
        return isTFIDF_flag;
    }

    public static boolean isIsASCII_flag() {
        return isASCII_flag;
    }

    public static boolean isIsDAAT_flag() {
        return isDAAT_flag;
    }

}
