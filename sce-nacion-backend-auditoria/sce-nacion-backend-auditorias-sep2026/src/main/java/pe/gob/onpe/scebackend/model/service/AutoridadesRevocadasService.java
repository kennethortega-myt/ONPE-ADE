package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.request.reporte.AutoridadesRevocadasRequestDto;

public interface AutoridadesRevocadasService {

	byte[] getReporteAutoridadesRevocadas(AutoridadesRevocadasRequestDto filtro);
}
