package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetActaOpcionExportDto;

public interface IDetActaOpcionExportService {

	public List<DetActaOpcionExportDto> findByCc(String codigo);
	
}
