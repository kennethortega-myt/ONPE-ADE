package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmCatalogoExportDto;

public interface IAdmCatalogoExportService {

	public List<AdmCatalogoExportDto> findAll();
	
}
