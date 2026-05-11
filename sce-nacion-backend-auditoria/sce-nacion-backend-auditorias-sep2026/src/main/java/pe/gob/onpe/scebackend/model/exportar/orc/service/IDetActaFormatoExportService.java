package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetActaFormatoExportDto;

public interface IDetActaFormatoExportService {

	public List<DetActaFormatoExportDto> findByCc(String codigo);
	
}
