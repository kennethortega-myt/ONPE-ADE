package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.LocalVotacionExportDto;

public interface ILocalVotacionExportService {

	List<LocalVotacionExportDto> findByCc(String codigo);
	
}
