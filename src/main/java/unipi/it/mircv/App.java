package unipi.it.mircv;

import unipi.it.mircv.common.Reader;
import unipi.it.mircv.queryProcessing.QueryProcessing;

public class App
{
    public static void main(String[] args) {
        QueryProcessing queryProcessing= new QueryProcessing();
        // Example usage:
        String path = "src/main/java/unipi/it/mircv/data/collection.tar.gz";
        Reader.processCollection(path);
        queryProcessing.mainQueryProcessing();

    }
    }



