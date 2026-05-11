package pe.gob.onpe.scebatchpr.utils;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class FechaUtils {

	private FechaUtils() {}

	public static Date getFechaActualPeruana() {
		ZonedDateTime fechaActualPeru = ZonedDateTime.now(ZoneId.of("America/Lima"));
		Instant instant = fechaActualPeru.toInstant();
		return Date.from(instant);
	}
}
