package pe.gob.onpe.scebackend.model.exportar.pr.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.pr.dto.VwPrMesaExportDto;

public interface IVwPrMesaService {

	List<VwPrMesaExportDto> findAll();
	
}
