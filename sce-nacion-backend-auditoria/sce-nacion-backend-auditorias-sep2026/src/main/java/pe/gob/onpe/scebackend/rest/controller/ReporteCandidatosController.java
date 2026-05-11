package pe.gob.onpe.scebackend.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.scebackend.exeption.DataNoFoundException;
import pe.gob.onpe.scebackend.model.dto.FiltroOrganizacionesPoliticasDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.CandidatoReporteService;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("candidatos-org-politica")
public class ReporteCandidatosController extends BaseController{

	private final CandidatoReporteService candidatoReporteService;
	
	public ReporteCandidatosController(TokenDecoder tokenDecoder, CandidatoReporteService candidatoReporteService) {
        super(tokenDecoder);
        this.candidatoReporteService = candidatoReporteService;
	}

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse> getCandidatosPdf(@Valid @RequestBody FiltroOrganizacionesPoliticasDto filtro) {
        try {
            byte[] reporte = this.candidatoReporteService.reporteCandidatosPorOrgPol(filtro);
            return getPdfResponse(reporte);
        } catch (DataNoFoundException e) {
            return getErrorValidacionResponse(e.getMessage());
        } catch (Exception e) {
            throw e;
        }
    }
}
