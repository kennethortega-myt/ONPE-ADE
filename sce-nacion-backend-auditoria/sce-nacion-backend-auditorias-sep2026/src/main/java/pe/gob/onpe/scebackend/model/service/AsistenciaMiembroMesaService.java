package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.request.reporte.AsistenciaMiembroMesaRequestDto;

public interface AsistenciaMiembroMesaService {

	byte[] getReporteAsistenciaMiembroMesa(AsistenciaMiembroMesaRequestDto filtro);
}
