package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.VersionExportDto;

public interface IVersionExportService {

	List<VersionExportDto> findAll();
	
}
