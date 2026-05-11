package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteInformacionOficialDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteInformacionOficialRequestDto;


import java.util.List;

public interface IReporteInformacionOficialService {
    byte[] reporteInformacionOficial(ReporteInformacionOficialRequestDto filtro) throws JRException;
    List<ReporteInformacionOficialDto> consultarInformacionOficial(ReporteInformacionOficialRequestDto filtro);
}
