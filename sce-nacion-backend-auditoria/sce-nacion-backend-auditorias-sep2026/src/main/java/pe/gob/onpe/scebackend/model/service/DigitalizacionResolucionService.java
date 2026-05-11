package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.reportes.DigitalizacionResolucionDto;

public interface DigitalizacionResolucionService {

	byte[] getReporteDigitalizacionResolucion(DigitalizacionResolucionDto filtro);
}
