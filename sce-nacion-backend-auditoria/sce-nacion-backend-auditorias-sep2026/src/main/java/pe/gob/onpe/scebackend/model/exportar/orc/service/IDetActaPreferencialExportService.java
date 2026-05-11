package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetActaPreferencialExportDto;

public interface IDetActaPreferencialExportService {

	public List<DetActaPreferencialExportDto> findByCc(String codigo);
	
}
