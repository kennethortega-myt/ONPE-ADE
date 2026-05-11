package pe.gob.onpe.scebackend.model.exportar.pr.service;


import pe.gob.onpe.scebackend.model.exportar.pr.dto.EleccionViewExportDto;

public interface IEleccionExportPrService {

	EleccionViewExportDto findAll(Integer flag);
	
}
