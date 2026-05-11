package pe.gob.onpe.sceorcbackend.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


public class DateTimeUtil {

    private DateTimeUtil() {

    }

    public static final String AMERICA_LIMA = "America/Lima";

    public static LocalDate getDateByTimeZone() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(AMERICA_LIMA));
        return now.toLocalDate();
    }

    public static LocalDateTime getDateTimeByTimeZone() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(AMERICA_LIMA));
        return now.toLocalDateTime();
    }

    public static LocalDate getDateFromString(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(SceConstantes.PATTERN_DD_MM_YYYY_SLASHED);
        return LocalDate.parse(date, formatter);
    }

    public static String getDateFormat(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(SceConstantes.PATTERN_DD_MM_YYYY_SLASHED));
    }

    public static String getDateTimeFormat(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern(SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SLASHED));
    }

    public static String getDateTimeFormat(LocalDateTime date, String formato) {
        return date.format(DateTimeFormatter.ofPattern(formato));
    }


}
