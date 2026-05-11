package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.OrcSeccionExportDto;

public interface IOrcSeccionExportService {

	public List<OrcSeccionExportDto> findAll();
	
}
