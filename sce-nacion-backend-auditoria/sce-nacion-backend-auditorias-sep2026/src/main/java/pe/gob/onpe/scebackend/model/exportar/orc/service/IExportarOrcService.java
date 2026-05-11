package pe.gob.onpe.scebackend.model.exportar.orc.service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.ExportOrcDto;

public interface IExportarOrcService {

	ExportOrcDto exportar(String proceso, String cc);
	
}
