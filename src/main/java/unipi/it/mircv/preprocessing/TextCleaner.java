package unipi.it.mircv.preprocessing;

public class TextCleaner {

    // RegEx to match urls
    private static final String URL_MATCH = "[(http(s)?):\\/\\/(www\\.)?a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";

    // RegEx to match html tags
    private static final String HTML_TAGS_MATCH = "<[^>]+>";

    // RegEx to match a character that is not a letter/number
    private static final String NON_DIGIT_MATCH = "[^a-zA-Z0-9]";

    // RegEx to match sequential spaces
    private static final String SEQUENTIAL_SPACES_MATCH = " +";

    // RegEx to match at least 3 consecutive letters
    private static final String CONSECUTIVE_LETTER_MATCH = "(.)\\1{2,}";

    // RegEx to match strings in camel case
    private static final String CAMEL_CASE_MATCH = "(?<=[a-z])(?=[A-Z])";

    public static String cleanText(String text) {
        // Remove URLs, if present
        text = text.replaceAll(URL_MATCH, " ");

        // Remove HTML tags, if present
        text = text.replaceAll(HTML_TAGS_MATCH, " ");

        // Remove non-alphanumeric characters, including punctuation
        text = text.replaceAll(NON_DIGIT_MATCH, " ");

        // Reduce consecutive characters repeated 3 or more times to 2 characters
        text = text.replaceAll(CONSECUTIVE_LETTER_MATCH, "$1$1");

        // Remove sequential spaces, leaving only one space
        text = text.replaceAll(SEQUENTIAL_SPACES_MATCH, " ");

        // Remove any leading or trailing spaces
        text = text.trim();
        text = text.toLowerCase();
        return text;
    }


}

