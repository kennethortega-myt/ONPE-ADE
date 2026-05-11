package pe.gob.onpe.sceorcbackend.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateUtil {

    private static final Logger logger = LoggerFactory.getLogger(DateUtil.class);

    private DateUtil() {

    }

    public static String getDateString(Date date, String formato) {
        String strDate = null;
        if (date != null) {
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

    public static Date getDate(String dateString, String formato) {
        Date date = null;
        if (dateString != null && !dateString.trim().isEmpty()) {
            try {
                date = new SimpleDateFormat(formato).parse(dateString);
            } catch (ParseException e) {
                logger.error("Error: {}", e.getMessage());
            }
        }
        return date;
    }
    
    public static Date getFechaActualPeruana() {
    	ZonedDateTime fechaActualPeru = ZonedDateTime.now(ZoneId.of(DateTimeUtil.AMERICA_LIMA));
        Instant instant = fechaActualPeru.toInstant();
        return Date.from(instant);
    }
    
    public static Date getFechaActualPeruana(String formato) {
    	 Date fechaActual = getFechaActualPeruana();
    	 String sFechaActual = getDateString(fechaActual, formato);
    	 return getDate(sFechaActual, formato);
    }
}
