package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.OrcCatalogoExportDto;

public interface IOrcCatalogoExportService {

	public List<OrcCatalogoExportDto> findAll();
	
}
