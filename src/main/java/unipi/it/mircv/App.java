package unipi.it.mircv;

import unipi.it.mircv.indexing.Reader;
import unipi.it.mircv.queryProcessing.QueryProcessing;

public class App
{
    public static void main(String[] args) {
        QueryProcessing queryProcessing= new QueryProcessing();
        // Example usage:
        String path = "src/main/java/unipi/it/mircv/data/collection.tar.gz";
        long start_time = System.currentTimeMillis();
        Reader.processCollection(path);
        long end_time = System.currentTimeMillis();
        long processingTime = end_time - start_time;
        System.out.println("Query Processing took " + (double) processingTime/1000 + " seconds.");


        //queryProcessing.mainQueryProcessing();

    }
    }



