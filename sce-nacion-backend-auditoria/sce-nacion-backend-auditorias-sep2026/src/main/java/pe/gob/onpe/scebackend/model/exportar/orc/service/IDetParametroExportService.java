package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetParametroExportDto;

public interface IDetParametroExportService {

	List<DetParametroExportDto> findAll();
	
}
