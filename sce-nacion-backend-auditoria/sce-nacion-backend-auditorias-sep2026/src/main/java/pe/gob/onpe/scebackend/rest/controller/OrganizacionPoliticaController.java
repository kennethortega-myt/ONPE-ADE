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
import pe.gob.onpe.scebackend.model.service.OrganizacionPoliticaService;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("organizaciones-politicas")
public class OrganizacionPoliticaController extends BaseController{

	private final OrganizacionPoliticaService organizacionPoliticaService;
	
	public OrganizacionPoliticaController(TokenDecoder tokenDecoder, OrganizacionPoliticaService organizacionPoliticaService) {
        super(tokenDecoder);
        this.organizacionPoliticaService = organizacionPoliticaService;
	}
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse> getOrganizacionesPoliticasPdf(@Valid @RequestBody FiltroOrganizacionesPoliticasDto filtro) {
        byte[] organizaciones = this.organizacionPoliticaService.reporteOrganizacionesPoliticas(filtro);
        
        return getPdfResponse(organizaciones);
    }
	
}
