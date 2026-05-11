package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.AgrupacionPoliticaFicticioExportDto;

public interface IAgrupacionPoliticaFicticioExportService {

	List<AgrupacionPoliticaFicticioExportDto> findAll();
	
	List<AgrupacionPoliticaFicticioExportDto> findByCc(String cc);
	
}
