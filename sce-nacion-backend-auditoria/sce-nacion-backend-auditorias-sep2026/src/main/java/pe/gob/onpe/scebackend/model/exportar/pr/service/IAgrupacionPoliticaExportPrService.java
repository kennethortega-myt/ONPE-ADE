package pe.gob.onpe.scebackend.model.exportar.pr.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.AgrupacionPoliticaExportDto;

public interface IAgrupacionPoliticaExportPrService {

	List<AgrupacionPoliticaExportDto> findAll();
	
}
