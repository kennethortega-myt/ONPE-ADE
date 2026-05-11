package pe.gob.onpe.scebackend.model.exportar.pr.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.pr.dto.LocalVotacionExportDto;

public interface ILocalVotacionExportPrService {

	List<LocalVotacionExportDto> findAll();
	
}
