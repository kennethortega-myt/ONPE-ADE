package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.dto.reporte.AutoridadesRevocadasRequestDto;

public interface AutoridadesRevocadasService {

	public byte[] getReporteAutoridadesRevocadas(AutoridadesRevocadasRequestDto filtro);
}
