package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;

import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroOrganizacionesPoliticasDto;

public interface AutoridadReporteService {

	public byte[] reporteAutoridadesEnConsulta(FiltroOrganizacionesPoliticasDto filtro);
}
