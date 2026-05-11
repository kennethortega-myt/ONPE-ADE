package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.OpcionVotoExportDto;

public interface IOpcionVotoExportService {

	public List<OpcionVotoExportDto> findAll();
}
