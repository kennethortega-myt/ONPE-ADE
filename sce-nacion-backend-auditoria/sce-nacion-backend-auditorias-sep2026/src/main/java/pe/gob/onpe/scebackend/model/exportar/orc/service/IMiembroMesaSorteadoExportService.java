package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.MiembroMesaSorteadoExportDto;

public interface IMiembroMesaSorteadoExportService {

	List<MiembroMesaSorteadoExportDto> findByCc(String codigo);
	
}
