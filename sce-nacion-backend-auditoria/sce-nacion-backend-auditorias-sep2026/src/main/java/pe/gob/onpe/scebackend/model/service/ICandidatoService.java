package pe.gob.onpe.scebackend.model.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.CandidatoExportDto;

public interface ICandidatoService {

	List<CandidatoExportDto> listarCandidatosPorCc(String cc);
	
}
