package unipi.it.mircv.evalution;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import unipi.it.mircv.common.Flags;
import unipi.it.mircv.common.Paths;
import unipi.it.mircv.queryProcessing.QueryProcessing;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class Evaluation {


    // time for indexing and query processing

    // size of files documentIndex, invertedIndex and Lexicon

    // qrels structure
    // 1st query number
    // 2nd Q0
    // 3rd document id
    // 4th rank the passage/document is retrieved.

    public void mainEvaluation(String filePath){
        long totalTimeForQueryProcessing = 0;

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        LocalDateTime now = LocalDateTime.now();

        String scoringStrategy;
        String ranking;

        // create file
        if (Flags.isIsDAAT_flag()) scoringStrategy = "DAAT";
        else scoringStrategy = "MAXSCORE";
        if (Flags.isIsTFIDF_flag()) ranking = "TFIDF";
        else ranking = "BM25";

        File file = new File(Paths.PATH_EVALUATION_RESULTS + "EvaluationResults_" + scoringStrategy + "_" + ranking +".txt");

        try {
            FileInputStream fin = new FileInputStream(filePath);
            BufferedInputStream bis = new BufferedInputStream(fin);
            GZIPInputStream gis = new GZIPInputStream(bis);
            InputStreamReader isr = new InputStreamReader(gis);
            BufferedReader br = new BufferedReader(isr);

            String line;
            while ((line = br.readLine()) != null) {
                //System.out.println(line);
                String[] values = line.split("\t", 2);
                //System.out.println("Values: " + Arrays.toString(values));
                QueryStructure query = new QueryStructure(values[1], Integer.parseInt(values[0]));
                //System.out.println("Query: " + query.getQuery());

                QueryProcessing.processing(query.getQuery(), query);
                totalTimeForQueryProcessing += QueryProcessing.getTimeForQueryProcessing();

                saveEvaluationResults(file, query);

            }

            br.close();

        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }

        System.out.println("Total query for " + scoringStrategy + " with " + ranking + " took " + totalTimeForQueryProcessing/1000 + " seconds");
    }


    private void saveEvaluationResults(File file, QueryStructure query) throws IOException {

        FileWriter myWriter = new FileWriter(file, true);
        for (Integer i : query.getDocumentEval().keySet()) {
            String resultLine;
            resultLine = query.getQueryID() + " Q0 " + i + " " + query.getDocumentEval().get(i) + "\n";
            //System.out.println("Results " + resultLine);
            myWriter.write(resultLine);
        }
        myWriter.close();

    }
}
