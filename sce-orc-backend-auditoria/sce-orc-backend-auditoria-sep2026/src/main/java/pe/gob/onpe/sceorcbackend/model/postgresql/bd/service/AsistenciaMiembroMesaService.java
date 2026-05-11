package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.dto.reporte.AsistenciaMiembroMesaRequestDto;

public interface AsistenciaMiembroMesaService {

	public byte[] getReporteAsistenciaMiembroMesa(AsistenciaMiembroMesaRequestDto filtro);

}
