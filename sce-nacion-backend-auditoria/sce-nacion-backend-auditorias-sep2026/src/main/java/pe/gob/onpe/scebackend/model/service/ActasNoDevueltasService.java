package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.request.reporte.ActasNoDevueltasRequestDto;

public interface ActasNoDevueltasService {

	byte[] getReporteActasNoDevueltas(ActasNoDevueltasRequestDto filtro);
}
