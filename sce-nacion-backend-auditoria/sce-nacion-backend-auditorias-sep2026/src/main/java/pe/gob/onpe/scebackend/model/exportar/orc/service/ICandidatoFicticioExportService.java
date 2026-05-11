package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.CandidatoFicticioExportDto;

public interface ICandidatoFicticioExportService {

	List<CandidatoFicticioExportDto> findAll();
	
	List<CandidatoFicticioExportDto> findByCc(String cc);
	
}
