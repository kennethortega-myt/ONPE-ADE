package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.reportes.FiltroAvanceDigitalizacionDto;

public interface ReporteAvanceDigitalizacionService {

	byte[] reporteAvanceDigitalizacion(FiltroAvanceDigitalizacionDto filtro);
}
