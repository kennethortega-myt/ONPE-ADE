package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteTransmisionRequestDto;

public interface ReporteTransmisionService {

	public byte[] getReporteTransmision(ReporteTransmisionRequestDto filtro);
}
