package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.FormatoExportDto;

public interface IFormatoExportService {

	public List<FormatoExportDto> findByCc(String codigo);
	
}
