package pe.gob.onpe.scebackend.rest.controller.reporte;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteRecepcionRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.ReporteRecepcionService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("recepcion")
public class ReporteRecepcionController extends BaseController{

	private final ReporteRecepcionService reporteRecepcionService;
	
	public ReporteRecepcionController(TokenDecoder tokenDecoder, ReporteRecepcionService reporteRecepcionService) {
        super(tokenDecoder);
        this.reporteRecepcionService = reporteRecepcionService;
	}
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse> getReporteTransmision(@Valid @RequestBody ReporteRecepcionRequestDto filtro) {
        byte[] resultado = this.reporteRecepcionService.getReporteRecepcion(filtro);
        
        return getPdfResponse(resultado);
    }
}
