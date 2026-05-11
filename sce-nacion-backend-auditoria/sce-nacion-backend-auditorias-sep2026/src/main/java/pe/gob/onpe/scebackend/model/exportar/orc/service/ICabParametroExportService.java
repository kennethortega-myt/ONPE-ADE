package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.CabParametroExportDto;

public interface ICabParametroExportService {

	List<CabParametroExportDto> findAll();
	
}
