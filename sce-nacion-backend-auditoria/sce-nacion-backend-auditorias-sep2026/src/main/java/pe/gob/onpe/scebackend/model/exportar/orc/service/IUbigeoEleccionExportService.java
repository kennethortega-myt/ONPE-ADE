package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.UbigeoEleccionDto;

public interface IUbigeoEleccionExportService {

	List<UbigeoEleccionDto> findByCc(String codigo);
	
}
