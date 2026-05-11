package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.AgrupacionPoliticaRealExportDto;

public interface IAgrupacionPoliticaRealExportService {

	List<AgrupacionPoliticaRealExportDto> findAll();
	
	List<AgrupacionPoliticaRealExportDto> findByCc(String cc);
	
}
