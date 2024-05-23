package unipi.it.mircv.common;

import java.nio.file.Path;

public class Paths {
    public static final String PATH_COLLECTION = "data/input/collection.tar.gz";

    public static final String PATH_LEXICON_MERGED = "data/output/merged/LexiconMerged.txt";
    public static final String PATH_INVERTED_INDEX_MERGED = "data/output/merged/InvertedIndexMerged.txt";
    public static final String PATH_DOCUMENT_INDEX_MERGED = "data/output/merged/DocumentIndexMerged.txt";

    public static final String PATH_LEXICON = "data/output/blocks/Lexicon";
    public static final String PATH_INVERTED_INDEX = "data/output/blocks/InvertedIndex";
    public static final String PATH_DOCUMENT_INDEX = "data/output/blocks/DocumentIndex";

    public static final String PATH_EVALUATION_RESULTS = "data/evaluation/output/";
    public static final String PATH_EVALUATION_INPUT = "data/evaluation/input/msmarco-test2019-queries.tsv.gz";
    public static final String PATH_EVALUATION_GT = "data/evaluation/ground-truth-results/2019qrels-pass.txt";

    public static final String PATH_AVGDOCLEN = "data/output/avgDocLen.txt";
    public static final String PATH_OFFSETS = "data/output/aux_folder/offsets.txt";
    public static final String PATH_OUTPUT_FOLDER = "data/output/";
}
