package pe.gob.onpe.scebackend.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class StringTypeDetector {

    private StringTypeDetector() {
        throw new IllegalStateException("Utility class");
    }
    public static String detectType(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "EMPTY";
        }

        if (isBoolean(value)) {
            return "BOOLEAN";
        }

        if (isNumber(value)) {
            return "NUMBER";
        }

        if (isDate(value)) {
            return "DATE";
        }

        return "TEXT";
    }

    private static boolean isBoolean(String value) {
        return "true".equalsIgnoreCase(value) ||
                "false".equalsIgnoreCase(value) ||
                "1".equals(value) ||
                "0".equals(value);
    }

    private static boolean isNumber(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean isDate(String value) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate.parse(value, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
