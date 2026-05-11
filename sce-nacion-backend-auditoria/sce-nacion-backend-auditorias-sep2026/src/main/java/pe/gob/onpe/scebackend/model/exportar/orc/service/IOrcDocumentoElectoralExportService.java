package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.OrcDocElectoralExportDto;

public interface IOrcDocumentoElectoralExportService {

	public List<OrcDocElectoralExportDto> findAll();
	
}
