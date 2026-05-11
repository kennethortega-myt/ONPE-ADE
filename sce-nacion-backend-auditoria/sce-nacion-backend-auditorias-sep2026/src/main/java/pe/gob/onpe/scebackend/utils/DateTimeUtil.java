package pe.gob.onpe.scebackend.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {
    private DateTimeUtil() {
        throw new UnsupportedOperationException("DateTimeUtil es una clase utilitaria y no debe ser instanciada");
    }
    public static final String AMERICA_LIMA = "America/Lima";
    public static final String FROMAT_DATA = "dd/MM/yyyy";
    public static final String FROMAT_DATA_TIME = "dd/MM/yyyy HH:mm";

    public static LocalDate getDateByTimeZone() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(AMERICA_LIMA));
        return now.toLocalDate();
    }

    public static LocalDateTime getDateTimeByTimeZone() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of(AMERICA_LIMA));
        return now.toLocalDateTime();
    }

    public static LocalDate getDateFromString(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FROMAT_DATA);
        return LocalDate.parse(date, formatter);
    }

    public static LocalDateTime getDateTimeFromString(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FROMAT_DATA);
        return LocalDateTime.parse(date, formatter);
    }

    public static String getDateFormat(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern(FROMAT_DATA));
    }

    public static String getDateTimeFormat(LocalDateTime date) {
        return date.format(DateTimeFormatter.ofPattern(FROMAT_DATA_TIME));
    }

    public static java.time.LocalDate convertISO8601StringToLocalDate(String iso8601String, String dateTimeZone) {
        org.joda.time.LocalDate joda = (new DateTime(iso8601String)).withZone(DateTimeZone.forID(dateTimeZone)).toLocalDate();
        return java.time.LocalDate.of(joda.getYear(), joda.getMonthOfYear(), joda.getDayOfMonth());
    }

    public static java.time.LocalDateTime convertISO8601StringToLocalDateTime(String iso8601String, String dateTimeZone) {
        org.joda.time.LocalDateTime joda = (new DateTime(iso8601String)).withZone(DateTimeZone.forID(dateTimeZone)).toLocalDateTime();
        return java.time.LocalDateTime.of(joda.getYear(), joda.getMonthOfYear(), joda.getDayOfMonth(),joda.getHourOfDay(), joda.getMinuteOfHour());
    }
}
