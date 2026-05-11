package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmDetCatalogoEstructuraExportDto;

public interface IAdmDetCatalogoEstructuraExportService {

	public List<AdmDetCatalogoEstructuraExportDto> findAll();
	
}
