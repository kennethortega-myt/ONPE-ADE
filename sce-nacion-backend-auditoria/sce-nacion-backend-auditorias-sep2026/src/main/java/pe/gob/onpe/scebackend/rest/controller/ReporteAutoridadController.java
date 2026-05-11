package pe.gob.onpe.scebackend.rest.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.scebackend.model.dto.FiltroOrganizacionesPoliticasDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.AutoridadReporteService;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("autoridades-consulta")
public class ReporteAutoridadController extends BaseController{

	private final AutoridadReporteService autoridadReporteService;
	
	public ReporteAutoridadController(TokenDecoder tokenDecoder, AutoridadReporteService autoridadReporteService) {
        super(tokenDecoder);
        this.autoridadReporteService = autoridadReporteService;
	}
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse> getAutoridadesEnConsultaPdf(@Valid @RequestBody FiltroOrganizacionesPoliticasDto filtro) {
        byte[] reporte = this.autoridadReporteService.reporteAutoridadesEnConsulta(filtro);
        
        return getPdfResponse(reporte);
    }
}
