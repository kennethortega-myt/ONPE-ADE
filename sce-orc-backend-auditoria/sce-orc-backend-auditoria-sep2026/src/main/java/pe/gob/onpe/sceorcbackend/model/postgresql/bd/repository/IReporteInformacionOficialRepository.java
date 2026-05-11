package pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository;


import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteInformacionOficialDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteInformacionOficialRequestDto;

import java.util.List;

public interface IReporteInformacionOficialRepository {
    List<ReporteInformacionOficialDto> listarReporteInformacionOficial(ReporteInformacionOficialRequestDto filtro);
}
