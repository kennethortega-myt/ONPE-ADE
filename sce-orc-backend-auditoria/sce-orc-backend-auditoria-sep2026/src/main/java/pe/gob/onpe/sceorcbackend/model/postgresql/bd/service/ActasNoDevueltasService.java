package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.dto.reporte.ActasNoDevueltasRequestDto;

public interface ActasNoDevueltasService {

	public byte[] getReporteActasNoDevueltas(ActasNoDevueltasRequestDto filtro);
}
