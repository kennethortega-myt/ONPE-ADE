package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;

import pe.gob.onpe.sceorcbackend.model.dto.reporte.DigitalizacionResolucionDto;

public interface DigitalizacionResolucionService {

	public byte[] getReporteDigitalizacionResolucion(DigitalizacionResolucionDto filtro);
}
