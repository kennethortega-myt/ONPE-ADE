package pe.gob.onpe.scebackend.model.service;

import java.util.List;

import pe.gob.onpe.scebackend.model.dto.OrganizacionPoliticaReporteDto;
import pe.gob.onpe.scebackend.model.dto.reportes.ProbableCandidatoElecto;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ProbableCandidatoRequestDto;

public interface ProbablesCandidatosElectosService {

	List<OrganizacionPoliticaReporteDto> listarAgrupolPorDE(ProbableCandidatoRequestDto filtro);
	
	List<ProbableCandidatoElecto> listarProbablesCandidatosElectos(ProbableCandidatoRequestDto filtro);
	
	byte[] getReporteProbablesCandidatos(ProbableCandidatoRequestDto filtro);
}
