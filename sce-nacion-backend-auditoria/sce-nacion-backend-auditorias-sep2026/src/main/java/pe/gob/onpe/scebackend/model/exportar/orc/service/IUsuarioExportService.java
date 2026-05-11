package pe.gob.onpe.scebackend.model.exportar.orc.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.exportar.orc.dto.UsuarioExportDto;

public interface IUsuarioExportService {

	public List<UsuarioExportDto> findByCc(String codigo);
	
}
