package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;


import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroOrganizacionesPoliticasDto;

public interface CandidatoReporteService {

	public byte[] reporteCandidatosPorOrgPol(FiltroOrganizacionesPoliticasDto filtro);
	
}
