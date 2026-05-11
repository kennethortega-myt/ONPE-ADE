package pe.gob.onpe.scebackend.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateUtil {
    private DateUtil() {
        throw new UnsupportedOperationException("DateUtil es una clase utilitaria y no debe ser instanciada");
    }
    public static String getDateString(Date date, String formato) {
        String strDate = null;
        if(date!=null) {
            DateFormat dateFormat = new SimpleDateFormat(formato);
            strDate = dateFormat.format(date);
        }
        return strDate;
    }

    public static String getFechaActual(String formato) {
        String strDate = null;
        DateFormat dateFormat = new SimpleDateFormat(formato);
        strDate = dateFormat.format(new Date());
        return strDate;
    }

    public static Date getDate(String dateString, String formato){
        Date date = null;
        if(dateString!=null) {
            try {
                date=new SimpleDateFormat(formato).parse(dateString);
            } catch (ParseException e) {
                log.error(e.getMessage());
            }
        }
        return date;
    }

    public static Boolean puedeEditarOrEliminar(Date fechaEleccion, Date fechaActual, Integer dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaEleccion);
        calendar.add(Calendar.DAY_OF_YEAR, -dias);
        Date limiteEdicion = calendar.getTime();
        return !fechaActual.after(limiteEdicion);
    }
    
    public static LocalDateTime convertToLocalDateTime(Date date) {
        return date.toInstant()
                   .atZone(ZoneId.of("America/Lima"))
                   .toLocalDateTime();
    }
    
    public static LocalDateTime getLocalDateTime(String dateString, String formato) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(formato);
            return LocalDateTime.parse(dateString, formatter);
        } catch (Exception e) {
            return null;
        }
    }
    
    public static Date getFechaActualPeruana() {
    	ZonedDateTime fechaActualPeru = ZonedDateTime.now(ZoneId.of("America/Lima"));
        Instant instant = fechaActualPeru.toInstant();
        return Date.from(instant);
    }

    public static boolean beforeDiaD(Date fechaConvocatoria, String hora) {
        LocalDate fecha = fechaConvocatoria.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalTime horaLimite = LocalTime.parse(hora); //"HH:mm:ss"
        LocalDateTime fechaYHoraLimite = LocalDateTime.of(fecha, horaLimite);
        LocalDateTime ahora = LocalDateTime.now();
        return ahora.isAfter(fechaYHoraLimite);
    }
}
