package pe.gob.onpe.scebackend.model.exportar.orc.service;

import pe.gob.onpe.scebackend.model.dto.PaginaDto;
import pe.gob.onpe.scebackend.model.dto.PaginaOptDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.PadronElectoralExportOrcDto;
import pe.gob.onpe.scebackend.model.exportar.orc.dto.PadronElectoralExportPrDto;


public interface IPadronElectoralExportService {

	PaginaDto<PadronElectoralExportPrDto>  importarPadronesPr(int numeroPagina, int tamanoPagina);
	PaginaDto<PadronElectoralExportOrcDto> importarPadronesOrc(String cc, int numeroPagina, int tamanoPagina);
	int contabilizar(String cc);
	PaginaOptDto<PadronElectoralExportOrcDto> importarOpPadronesOrc(String cc, Long lastId, int tamanoPagina);
	
}
