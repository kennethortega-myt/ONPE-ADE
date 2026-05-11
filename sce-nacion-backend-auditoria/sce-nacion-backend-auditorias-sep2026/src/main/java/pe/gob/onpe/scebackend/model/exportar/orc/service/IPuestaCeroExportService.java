package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.PuestaCeroExportDto;

public interface IPuestaCeroExportService {

	List<PuestaCeroExportDto> findByCc(String codigo);
	
}
