package pe.gob.onpe.scebackend.rest.controller.reporte;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ActasNoDevueltasRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.ActasNoDevueltasService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("actas-no-devueltas")
public class ActasNoDevueltasController extends BaseController{

	private final ActasNoDevueltasService actasNoDevueltasService;
	
	public ActasNoDevueltasController(TokenDecoder tokenDecoder, ActasNoDevueltasService actasNoDevueltasService) {
        super(tokenDecoder);
        this.actasNoDevueltasService = actasNoDevueltasService;
	}
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse> getActasNoDevueltasPdf(@Valid @RequestBody ActasNoDevueltasRequestDto filtro) {
        byte[] resultado = this.actasNoDevueltasService.getReporteActasNoDevueltas(filtro);
        
        return getPdfResponse(resultado);
    }
	
}
