package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.dto.AmbitoElectoralDto;

public interface IAmbitoElectoralExportService {

	List<AmbitoElectoralDto> findByCc(String codigo);
	
}
