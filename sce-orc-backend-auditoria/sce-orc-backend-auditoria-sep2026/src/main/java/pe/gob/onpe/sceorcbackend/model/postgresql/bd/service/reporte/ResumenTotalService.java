package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;

import java.util.List;

import pe.gob.onpe.sceorcbackend.model.dto.reporte.DetalleResumenTotal;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroResumenTotalDto;

public interface ResumenTotalService {

	public List<DetalleResumenTotal> resumenTotalCentroComputo(FiltroResumenTotalDto filtro);
	
	public byte[] getReporteResumenTotalPdf(FiltroResumenTotalDto filtro);
}
