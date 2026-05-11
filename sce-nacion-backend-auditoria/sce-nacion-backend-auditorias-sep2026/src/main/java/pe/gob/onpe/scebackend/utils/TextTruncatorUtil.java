package pe.gob.onpe.scebackend.utils;


public class TextTruncatorUtil {

    private static final int DEFAULT_MAX_LENGTH = 300;
    private static final String ELLIPSIS = "...";


    public static String truncateTo300Chars(String text) {
        return truncateToLength(text, DEFAULT_MAX_LENGTH);
    }


    public static String truncateToLength(String text, int maxLength) {
        if (text == null) {
            return null;
        }
        
        if (maxLength <= 0) {
            return "";
        }

        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength) + ELLIPSIS;
    }

}