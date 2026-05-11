package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;


import pe.gob.onpe.scebackend.model.exportar.orc.dto.VersionModeloExportDto;

public interface IVersionModeloExportService {

	List<VersionModeloExportDto> findAll();
	
}
