package pe.gob.onpe.scebackend.model.service.reporte;

import pe.gob.onpe.scebackend.model.dto.reportes.FiltroPrecisionAsistAutomaDigitacionDto;

public interface IPrecisionAsistAutomaDigitacionService {
    byte[] getPrecisionAsistAutomaDigitacionResumen(FiltroPrecisionAsistAutomaDigitacionDto filtro);
    byte[] getPrecisionAsistAutomaDigitacionDetalle(FiltroPrecisionAsistAutomaDigitacionDto filtro);
}
