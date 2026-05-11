package pe.gob.onpe.scebackend.model.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteInformacionOficialRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteInformacionOficialDto;

import java.util.List;

public interface IReporteInformacionOficialService {
    byte[] reporteInformacionOficial(ReporteInformacionOficialRequestDto filtro) throws JRException;
    List<ReporteInformacionOficialDto> consultarInformacionOficial(ReporteInformacionOficialRequestDto filtro);
}
