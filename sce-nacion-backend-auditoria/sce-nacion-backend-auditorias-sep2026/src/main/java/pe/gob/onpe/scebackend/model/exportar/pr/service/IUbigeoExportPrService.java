package pe.gob.onpe.scebackend.model.exportar.pr.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.UbigeoExportDto;

public interface IUbigeoExportPrService {

	List<UbigeoExportDto> findAll();
	
}
