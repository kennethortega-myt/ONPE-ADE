package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.DetDistritoElectoralEleccionExportDto;

public interface IDetDistritoElectoralEleccionExportService {

	public List<DetDistritoElectoralEleccionExportDto> findByCc(String codigo);
	
}
