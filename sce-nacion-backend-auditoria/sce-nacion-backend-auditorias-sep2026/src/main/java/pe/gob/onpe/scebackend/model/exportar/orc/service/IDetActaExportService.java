package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetActaExportDto;

public interface IDetActaExportService {

	List<DetActaExportDto> findByCc(String codigo);
	
}
