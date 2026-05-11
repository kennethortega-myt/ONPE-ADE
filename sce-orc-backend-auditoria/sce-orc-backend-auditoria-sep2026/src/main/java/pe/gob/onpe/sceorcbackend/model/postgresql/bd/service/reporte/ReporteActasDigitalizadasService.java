package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;


import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroActasDigitalizadasDto;

public interface ReporteActasDigitalizadasService {
	
	public byte[] reporteActasDigitalizadas(FiltroActasDigitalizadasDto filtro);
	
	public byte[] reporteActasDigitalizadasExcel(FiltroActasDigitalizadasDto filtro);

}
