package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroAvanceDigitalizacionDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.ReporteAvanceDigitalizacionService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;


@RestController
@CrossOrigin
@RequestMapping("avance-digitalizacion")
public class ReporteAvanceDigitalizacionActasController extends BaseController{

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

	Logger logger = LoggerFactory.getLogger(ReporteAvanceDigitalizacionActasController.class);
	
	private final ReporteAvanceDigitalizacionService reporteAvanceDigitalizacionService;
	
	public ReporteAvanceDigitalizacionActasController(ReporteAvanceDigitalizacionService reporteAvanceDigitalizacionService) {
		this.reporteAvanceDigitalizacionService = reporteAvanceDigitalizacionService;
	}

    @PreAuthorize(RoleAutority.ACCESO_TOTAL)
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getAvanceDigitalizacionPdf(@Valid @RequestBody FiltroAvanceDigitalizacionDto filtro) {
        filtro.setEsquema(schema);
        byte[] actas = this.reporteAvanceDigitalizacionService.reporteAvanceDigitalizacion(filtro);
        
        return getPdfResponse(actas);
    }
}
