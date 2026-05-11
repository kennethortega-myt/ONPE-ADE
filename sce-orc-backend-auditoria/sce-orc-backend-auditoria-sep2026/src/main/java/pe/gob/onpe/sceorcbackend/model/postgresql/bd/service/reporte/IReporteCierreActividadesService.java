package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteHistoricoCierreReaperturaRequestDto;

public interface IReporteCierreActividadesService {

	byte[] reporteHistoricoCierreReapertura(ReporteHistoricoCierreReaperturaRequestDto filtro) throws JRException;
	byte[] reporteCierreActividades(ReporteHistoricoCierreReaperturaRequestDto filtro) throws JRException;
	byte[] reporteReaperturaActividades(ReporteHistoricoCierreReaperturaRequestDto filtro) throws JRException;
}
