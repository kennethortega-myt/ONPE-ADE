package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.CandidatoExportDto;

public interface ICandidatoExportService {

	List<CandidatoExportDto> findByCc(String codigo);
	
}
