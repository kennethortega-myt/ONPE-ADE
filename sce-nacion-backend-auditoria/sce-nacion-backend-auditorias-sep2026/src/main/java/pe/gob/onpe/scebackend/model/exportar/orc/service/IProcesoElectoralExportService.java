package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.ProcesoElectoralExportDto;

public interface IProcesoElectoralExportService {

	List<ProcesoElectoralExportDto> findByCc(String codigo);
	
}
