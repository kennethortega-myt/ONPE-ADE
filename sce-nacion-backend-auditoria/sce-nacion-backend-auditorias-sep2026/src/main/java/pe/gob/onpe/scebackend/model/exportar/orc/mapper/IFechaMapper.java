package pe.gob.onpe.scebackend.model.exportar.orc.mapper;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.mapstruct.Named;

public interface IFechaMapper {

	@Named("convertirFecha")
	default String convertirFecha(Date fecha) {
		if(fecha!=null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	        return dateFormat.format(fecha);
		}
        return null;
    }
}
