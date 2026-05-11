package pe.gob.onpe.scebackend.model.exportar.pr.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DistritoElectoralExportDto;

public interface IDistritoElectoralExportPrService {

	List<DistritoElectoralExportDto> findAll();
	
}
