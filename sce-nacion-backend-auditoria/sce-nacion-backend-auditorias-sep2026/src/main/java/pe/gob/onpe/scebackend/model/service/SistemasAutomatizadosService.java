package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.request.reporte.SistemasAutomatizadosRequestDto;

public interface SistemasAutomatizadosService {

	byte[] getReporteSistemasAutomatizados(SistemasAutomatizadosRequestDto filtro);
}
