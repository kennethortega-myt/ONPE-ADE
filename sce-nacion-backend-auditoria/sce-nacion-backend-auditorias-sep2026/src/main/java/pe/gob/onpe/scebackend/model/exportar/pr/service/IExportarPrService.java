package pe.gob.onpe.scebackend.model.exportar.pr.service;


import pe.gob.onpe.scebackend.model.exportar.pr.dto.ExportarPrDto;
import pe.gob.onpe.scebackend.model.exportar.pr.dto.ExportarPrRequestDto;

public interface IExportarPrService {

	ExportarPrDto exportar(ExportarPrRequestDto request);
	
}
