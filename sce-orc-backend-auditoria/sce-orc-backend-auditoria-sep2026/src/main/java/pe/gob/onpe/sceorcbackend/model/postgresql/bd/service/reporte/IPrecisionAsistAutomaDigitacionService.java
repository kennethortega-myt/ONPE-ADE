package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;

import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroPrecisionAsistAutomaDigitacionDto;

public interface IPrecisionAsistAutomaDigitacionService {
    byte[] getPrecisionAsistAutomaDigitacionResumen(FiltroPrecisionAsistAutomaDigitacionDto filtro);
    byte[] getPrecisionAsistAutomaDigitacionDetalle(FiltroPrecisionAsistAutomaDigitacionDto filtro);
}
