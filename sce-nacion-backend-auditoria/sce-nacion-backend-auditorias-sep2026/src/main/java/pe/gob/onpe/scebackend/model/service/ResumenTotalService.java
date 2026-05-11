package pe.gob.onpe.scebackend.model.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.dto.reportes.DetalleResumenTotal;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroResumenTotalDto;

public interface ResumenTotalService {

	List<DetalleResumenTotal> resumenTotalCentroComputo(FiltroResumenTotalDto filtro);
	
	byte[] getReporteResumenTotalPdf(FiltroResumenTotalDto filtro);
}
