package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.MesaExportDto;

public interface IMesaExportService {

	List<MesaExportDto> findByCc(String codigo);
	
}
