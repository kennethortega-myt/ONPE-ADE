package pe.gob.onpe.scebackend.model.exportar.orc.service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.ExportAdmDto;

public interface IExportarAdmService {

	ExportAdmDto exportar(String proceso, String cc);
	
}
