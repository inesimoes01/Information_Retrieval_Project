package unipi.it.mircv;

import unipi.it.mircv.common.Reader;

public class App
{
    /*
    public static void main( String[] args )
    {
        String inputText = "\80\x90 https://www.example.com aaaaaa <div></div> abc   abc ";
        TextCleaner cleaner = new TextCleaner();
        String x = cleaner.cleanText(inputText);

        System.out.println(x);
    }*/

    public static void main(String[] args) {
        // Example usage:
        String path = "src/main/java/unipi/it/mircv/data/collection.tar.gz";
        Reader.processCollection(path);
    }
    }



