package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteTransmisionRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ReporteTransmisionService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("transmision")
public class ReporteTransmisionController extends BaseController{

	Logger logger = LoggerFactory.getLogger(ReporteTransmisionController.class);
	
	@Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;
	
	private final ReporteTransmisionService reporteTransmisionService;
	
	public ReporteTransmisionController(ReporteTransmisionService reporteTransmisionService) {
		this.reporteTransmisionService = reporteTransmisionService;
	}
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getReporteTransmision(@Valid @RequestBody ReporteTransmisionRequestDto filtro) {
        filtro.setEsquema(schema);
        byte[] resultado = this.reporteTransmisionService.getReporteTransmision(filtro);
        
        return getPdfResponse(resultado);
    }
	
}
