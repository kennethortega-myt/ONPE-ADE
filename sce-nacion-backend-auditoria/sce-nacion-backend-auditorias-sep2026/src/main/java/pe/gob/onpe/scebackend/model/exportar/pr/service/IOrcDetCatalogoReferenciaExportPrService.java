package pe.gob.onpe.scebackend.model.exportar.pr.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.pr.dto.DetCatalogoReferenciaExportDto;

public interface IOrcDetCatalogoReferenciaExportPrService {

	public List<DetCatalogoReferenciaExportDto> findAll();
	
}
