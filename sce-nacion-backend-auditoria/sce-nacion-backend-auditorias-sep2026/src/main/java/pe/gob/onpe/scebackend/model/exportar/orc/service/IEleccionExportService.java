package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.EleccionExportDto;

public interface IEleccionExportService {

	
	List<EleccionExportDto> findByCc(String codigo);
	
}
