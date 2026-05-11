package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.CabActaExportDto;



public interface IActaExportService {
	
	public List<CabActaExportDto> findByCc(String codigo);

}
