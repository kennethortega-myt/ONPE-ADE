package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.AgrupacionPoliticaExportDto;

public interface IAgrupacionPoliticaExportService {

	List<AgrupacionPoliticaExportDto> findByCc(String codigo);
	
}
