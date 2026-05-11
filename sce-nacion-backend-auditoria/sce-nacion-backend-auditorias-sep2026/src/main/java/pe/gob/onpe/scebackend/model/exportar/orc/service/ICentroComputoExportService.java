package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.CentroComputoExportDto;

public interface ICentroComputoExportService {

	List<CentroComputoExportDto> findByCc(String codigo);
	
}
