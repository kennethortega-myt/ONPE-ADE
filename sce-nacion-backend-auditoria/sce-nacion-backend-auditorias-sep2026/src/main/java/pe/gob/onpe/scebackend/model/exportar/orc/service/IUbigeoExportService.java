package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.UbigeoExportDto;

public interface IUbigeoExportService {

	List<UbigeoExportDto> findByCc(String codigo);
	
}
