package pe.gob.onpe.scebackend.model.exportar.orc.service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmDetCatalogoEstructuraExportDto;

import java.util.List;

public interface IOrcDetCatalogoEstructuraExportService {

	public List<AdmDetCatalogoEstructuraExportDto> findAll();
	
}
