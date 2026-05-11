package pe.gob.onpe.sceorcbackend.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class StringTypeDetector {

  private StringTypeDetector() {
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
    return ConstantesComunes.TEXT_TRUE.equalsIgnoreCase(value) || ConstantesComunes.TEXT_FALSE.equalsIgnoreCase(value);
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
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern(SceConstantes.PATTERN_YYYY_MM_DD_DASH);
      LocalDate.parse(value, formatter);
      return true;
    } catch (DateTimeParseException e) {
      return false;
    }
  }
}
