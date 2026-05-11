package pe.gob.onpe.scebackend.rest.controller.reporte;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.scebackend.model.dto.reportes.DigitalizacionResolucionDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.DigitalizacionResolucionService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("digitalizacion-resol")
public class DigitalizacionResolucionController extends BaseController{

	private final DigitalizacionResolucionService digitalizacionResolucionService;
	
	public DigitalizacionResolucionController(TokenDecoder tokenDecoder, DigitalizacionResolucionService digitalizacionResolucionService) {
        super(tokenDecoder);
        this.digitalizacionResolucionService = digitalizacionResolucionService;
	}
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse> getAvanceDigitalizacionResolucionPdf(@Valid @RequestBody DigitalizacionResolucionDto filtro) {
        byte[] resultado = this.digitalizacionResolucionService.getReporteDigitalizacionResolucion(filtro);
        
        return getPdfResponse(resultado);
    }
}
