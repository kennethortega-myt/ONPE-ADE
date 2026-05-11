package pe.gob.onpe.scebackend.model.service;

import pe.gob.onpe.scebackend.model.dto.FiltroOrganizacionesPoliticasDto;

public interface CandidatoReporteService {

	byte[] reporteCandidatosPorOrgPol(FiltroOrganizacionesPoliticasDto filtro);
	
}
