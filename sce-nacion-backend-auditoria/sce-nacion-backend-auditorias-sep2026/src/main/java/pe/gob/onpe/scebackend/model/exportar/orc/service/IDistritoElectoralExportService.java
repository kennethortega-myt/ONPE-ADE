package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DistritoElectoralExportDto;

public interface IDistritoElectoralExportService {

	List<DistritoElectoralExportDto> findByCc(String codigo);
	
}
