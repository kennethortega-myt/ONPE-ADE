package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.CabActaFormatoExportDto;

public interface ICabActaFormatoExportService {

	public List<CabActaFormatoExportDto> findByCc(String codigo);
	
}
