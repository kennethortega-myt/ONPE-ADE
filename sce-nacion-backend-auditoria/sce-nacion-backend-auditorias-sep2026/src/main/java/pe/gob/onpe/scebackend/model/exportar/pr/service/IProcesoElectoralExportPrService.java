package pe.gob.onpe.scebackend.model.exportar.pr.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.ProcesoElectoralExportDto;

public interface IProcesoElectoralExportPrService {

	List<ProcesoElectoralExportDto> findAll();
	
}
