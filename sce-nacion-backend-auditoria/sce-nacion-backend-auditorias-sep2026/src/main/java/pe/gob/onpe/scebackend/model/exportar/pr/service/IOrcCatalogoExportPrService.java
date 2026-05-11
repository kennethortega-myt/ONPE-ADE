package pe.gob.onpe.scebackend.model.exportar.pr.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.pr.dto.CatalogoExportDto;

public interface IOrcCatalogoExportPrService {

	public List<CatalogoExportDto> findAll();
	
}
