package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;


import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroAvanceDigitalizacionDto;

public interface ReporteAvanceDigitalizacionService {

	byte[] reporteAvanceDigitalizacion(FiltroAvanceDigitalizacionDto filtro);
}
