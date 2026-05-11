package pe.gob.onpe.scebackend.rest.controller.reporte;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroAvanceDigitalizacionDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.ReporteAvanceDigitalizacionService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("avance-digitalizacion")
public class ReporteAvanceDigitalizacionActasController extends BaseController{

	private final ReporteAvanceDigitalizacionService reporteAvanceDigitalizacionService;
	
	public ReporteAvanceDigitalizacionActasController(TokenDecoder tokenDecoder, ReporteAvanceDigitalizacionService reporteAvanceDigitalizacionService) {
		super(tokenDecoder);
		this.reporteAvanceDigitalizacionService = reporteAvanceDigitalizacionService;
	}
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse> getAvanceDigitalizacionPdf(@Valid @RequestBody FiltroAvanceDigitalizacionDto filtro) {
        
        byte[] actas = this.reporteAvanceDigitalizacionService.reporteAvanceDigitalizacion(filtro);
        
        return getPdfResponse(actas);
    }
}
