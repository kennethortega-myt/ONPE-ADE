package pe.gob.onpe.scebackend.model.service.reporte;

import pe.gob.onpe.scebackend.model.dto.reportes.FiltroPrecisionAsistAutomaControlDigitalizacionDto;

import java.sql.SQLException;

public interface IPrecisionAsistAutomaControlDigitalizacionService {
    byte[] getPrecisionAsistAutomaControlDigitalizacion(FiltroPrecisionAsistAutomaControlDigitalizacionDto filtro) throws SQLException;
}
