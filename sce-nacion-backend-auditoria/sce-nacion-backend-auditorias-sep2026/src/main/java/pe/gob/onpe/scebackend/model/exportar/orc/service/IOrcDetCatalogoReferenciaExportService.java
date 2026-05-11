package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.OrcDetCatalogoReferenciaExportDto;

public interface IOrcDetCatalogoReferenciaExportService {

	public List<OrcDetCatalogoReferenciaExportDto> findAll();
	
}
