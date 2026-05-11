package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.AdmDetCatalogoReferenciaExportDto;

public interface IAdmDetCatalogoReferenciaExportService {

	public List<AdmDetCatalogoReferenciaExportDto> findAll();
	
}
