package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import pe.gob.onpe.scebackend.model.dto.reportes.FiltroPrecisionAsistAutomaDigitacionDto;
import pe.gob.onpe.scebackend.model.dto.reportes.PrecisionAsistAutomaDigitacionDetalleDto;
import pe.gob.onpe.scebackend.model.dto.reportes.PrecisionAsistAutomaDigitacionResumenDto;

import java.util.List;

public interface IPrecisionAsistAutomaDigitacionRepository {
    public List<PrecisionAsistAutomaDigitacionResumenDto> listaPrecisionAsistAutomaResumen(FiltroPrecisionAsistAutomaDigitacionDto filtro);
    public List<PrecisionAsistAutomaDigitacionDetalleDto> listaPrecisionAsistAutomaDetalle(FiltroPrecisionAsistAutomaDigitacionDto filtro);
    public List<PrecisionAsistAutomaDigitacionDetalleDto> listaPrecisionAsistAutomaDetallePref(FiltroPrecisionAsistAutomaDigitacionDto filtro);
}
