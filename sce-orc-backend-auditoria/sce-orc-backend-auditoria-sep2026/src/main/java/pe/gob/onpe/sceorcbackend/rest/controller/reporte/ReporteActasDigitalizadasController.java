package pe.gob.onpe.sceorcbackend.rest.controller.reporte;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroActasDigitalizadasDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.ReporteActasDigitalizadasService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;


@RestController
@CrossOrigin
@RequestMapping("actas-digitalizadas")
public class ReporteActasDigitalizadasController extends BaseController{

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

	private final ReporteActasDigitalizadasService reporteActasDigitalizadasService;
	
	public ReporteActasDigitalizadasController(ReporteActasDigitalizadasService reporteActasDigitalizadasService) {
		this.reporteActasDigitalizadasService = reporteActasDigitalizadasService;
	}

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
	@PostMapping("/base64-pdf")
    public ResponseEntity<GenericResponse<String>> getActasDigitalizadasPdf(@Valid @RequestBody FiltroActasDigitalizadasDto filtro) {
        filtro.setEsquema(schema);
        byte[] actas = this.reporteActasDigitalizadasService.reporteActasDigitalizadas(filtro);
        
        return getPdfResponse(actas);
    }

    @PreAuthorize(RoleAutority.ACCESO_TOTAL)
	@PostMapping("/base64-excel")
    public ResponseEntity<GenericResponse<String>> getActasDigitalizadasExcel(@Valid @RequestBody FiltroActasDigitalizadasDto filtro) {
        filtro.setEsquema(schema);
        byte[] actas = this.reporteActasDigitalizadasService.reporteActasDigitalizadasExcel(filtro);
        
        return getPdfResponse(actas);
    }
}
