package pe.gob.onpe.scebackend.model.exportar.pr.service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.CandidatoExportDto;

import java.util.List;


public interface ICandidatoExportPrService {

	List<CandidatoExportDto> findAll();
	
}
