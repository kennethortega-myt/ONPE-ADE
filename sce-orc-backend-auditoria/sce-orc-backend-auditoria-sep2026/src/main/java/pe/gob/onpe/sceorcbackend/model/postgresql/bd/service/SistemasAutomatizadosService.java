package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.dto.reporte.SistemasAutomatizadosRequestDto;

public interface SistemasAutomatizadosService {

	public byte[] getReporteSistemasAutomatizados(SistemasAutomatizadosRequestDto filtro);
}
