package pe.gob.onpe.scebackend.rest.controller.reporte;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.scebackend.model.dto.request.reporte.AutoridadesRevocadasRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.AutoridadesRevocadasService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("autoridades-revocadas")
public class AutoridadesRevocadasController extends BaseController{

	private final AutoridadesRevocadasService autoridadesRevocadasService;
	
	public AutoridadesRevocadasController(TokenDecoder tokenDecoder, AutoridadesRevocadasService autoridadesRevocadasService) {
        super(tokenDecoder);
        this.autoridadesRevocadasService = autoridadesRevocadasService;
	}
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse> getAutoridadesRevocadasPdf(@Valid @RequestBody AutoridadesRevocadasRequestDto filtro) {
        byte[] resultado = this.autoridadesRevocadasService.getReporteAutoridadesRevocadas(filtro);
        
        return getPdfResponse(resultado);
    }
}
