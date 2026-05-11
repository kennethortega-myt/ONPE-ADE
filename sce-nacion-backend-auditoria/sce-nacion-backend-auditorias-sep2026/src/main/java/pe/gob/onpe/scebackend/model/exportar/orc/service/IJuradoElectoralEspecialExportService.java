package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.TabJuradoElectoralEspecialExportDto;

public interface IJuradoElectoralEspecialExportService {

	List<TabJuradoElectoralEspecialExportDto> findByCc(String codigo);
	
}
