package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.reportes.FiltroActasDigitalizadasDto;

public interface ReporteActasDigitalizadasService {
	
	byte[] reporteActasDigitalizadas(FiltroActasDigitalizadasDto filtro);
	
	byte[] reporteActasDigitalizadasExcel(FiltroActasDigitalizadasDto filtro);

}
