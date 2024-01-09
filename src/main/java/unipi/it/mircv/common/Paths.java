package unipi.it.mircv.common;

import java.nio.file.Path;

public class Paths {
    public static final String PATH_COLLECTION = "src/main/java/unipi/it/mircv/data/collection.tar.gz";
    public static final Path PATH_LEXICON = java.nio.file.Paths.get("data/output/LexiconMerged.txt");
    public static final Path PATH_INVERTED_INDEX = java.nio.file.Paths.get("data/output/InvertedIndexMerged.txt");
    public static final Path PATH_DOCUMENT_INDEX = java.nio.file.Paths.get("data/output/DocumentIndexMerged.txt");
    public static final String PATH_EVALUATION_RESULTS = "data/evaluation/input/";
    public static final String PATH_EVALUATION_INPUT = "data/evaluation/input/msmarco-test2019-queries.tsv.gz";
    public static final Path PATH_AVGDOCLEN = java.nio.file.Paths.get("data/output/avgDocLen.txt");
}
