package pe.gob.onpe.scebackend.model.exportar.orc.service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.ExportarDto;

public interface IExportarService {

	ExportarDto exportar(String proceso, String cc);
	
}
