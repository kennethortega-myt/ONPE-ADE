package pe.gob.onpe.scebackend.model.orc.repository.reportes;

import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteInformacionOficialRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteInformacionOficialDto;

import java.util.List;

public interface IReporteInformacionOficialRepository {
    List<ReporteInformacionOficialDto> listarReporteInformacionOficial(ReporteInformacionOficialRequestDto filtro);
}
