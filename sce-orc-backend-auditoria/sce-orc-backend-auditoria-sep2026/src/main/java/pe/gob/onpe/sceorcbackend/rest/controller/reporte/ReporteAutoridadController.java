package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroOrganizacionesPoliticasDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.AutoridadReporteService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("autoridades-consulta")
public class ReporteAutoridadController extends BaseController{

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

	Logger logger = LoggerFactory.getLogger(ReporteAutoridadController.class);
	
	private final AutoridadReporteService autoridadReporteService;
	
	public ReporteAutoridadController(AutoridadReporteService autoridadReporteService) {
		this.autoridadReporteService = autoridadReporteService;
	}
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getAutoridadesEnConsultaPdf(@Valid @RequestBody FiltroOrganizacionesPoliticasDto filtro) {
        filtro.setSchema(schema);
        
        byte[] reporte = this.autoridadReporteService.reporteAutoridadesEnConsulta(filtro);
        
        return getPdfResponse(reporte);
        
    }
}
