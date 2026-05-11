package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.request.reporte.BarreraElectoralRequestDto;

public interface BarreraElectoralService {

	public byte[] getReporteBarreraElectoral(BarreraElectoralRequestDto filtro);
}
