package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.ExportarRequestDto;

public interface ITabInicializacionCentroComputoService {

	void guardarInicializarCc(ExportarRequestDto request);
	
}
