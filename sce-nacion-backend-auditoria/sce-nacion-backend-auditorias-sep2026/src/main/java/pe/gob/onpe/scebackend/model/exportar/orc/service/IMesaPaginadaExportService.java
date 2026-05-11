package pe.gob.onpe.scebackend.model.exportar.orc.service;

import pe.gob.onpe.scebackend.model.dto.PaginaOptDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.MesaExportDto;

public interface IMesaPaginadaExportService {

	PaginaOptDto<MesaExportDto> importarMesas(String cc, Long lastId, int tamanoPagina);
	
	int contar(String cc);
	
}
