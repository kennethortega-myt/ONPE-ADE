package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteRecepcionRequestDto;

public interface ReporteRecepcionService {

	byte[] getReporteRecepcion(ReporteRecepcionRequestDto filtro);
}
