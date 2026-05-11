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
import pe.gob.onpe.sceorcbackend.model.dto.reporte.DigitalizacionResolucionDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.DigitalizacionResolucionService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;


@RestController
@CrossOrigin
@RequestMapping("digitalizacion-resol")
public class DigitalizacionResolucionController extends BaseController{

	Logger logger = LoggerFactory.getLogger(DigitalizacionResolucionController.class);
	
	@Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;
	
	private final DigitalizacionResolucionService digitalizacionResolucionService;
	
	public DigitalizacionResolucionController(DigitalizacionResolucionService digitalizacionResolucionService) {
		this.digitalizacionResolucionService = digitalizacionResolucionService;
	}

	@PreAuthorize(RoleAutority.ACCESO_TOTAL)
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getAvanceDigitalizacionResolucionPdf(@Valid @RequestBody DigitalizacionResolucionDto filtro) {
        filtro.setEsquema(schema);
        byte[] resultado = this.digitalizacionResolucionService.getReporteDigitalizacionResolucion(filtro);
        
        return getPdfResponse(resultado);
    }
}
