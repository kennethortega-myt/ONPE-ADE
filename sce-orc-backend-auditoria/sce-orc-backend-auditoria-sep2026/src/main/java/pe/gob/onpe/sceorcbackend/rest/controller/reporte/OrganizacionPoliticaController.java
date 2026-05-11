package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroOrganizacionesPoliticasDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.OrganizacionPoliticaService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("organizaciones-politicas")
public class OrganizacionPoliticaController extends BaseController{

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

	private final OrganizacionPoliticaService organizacionPoliticaService;
	
	public OrganizacionPoliticaController(OrganizacionPoliticaService organizacionPoliticaService) {
		this.organizacionPoliticaService = organizacionPoliticaService;
	}
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getOrganizacionesPoliticasPdf(@Valid @RequestBody FiltroOrganizacionesPoliticasDto filtro) {
        filtro.setSchema(schema);
        byte[] organizaciones = this.organizacionPoliticaService.reporteOrganizacionesPoliticas(filtro);

        return getPdfResponse(organizaciones);
    }
	
}
