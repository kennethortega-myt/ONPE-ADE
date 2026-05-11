package pe.gob.onpe.scebackend.rest.controller.reporte;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.scebackend.model.dto.request.reporte.SistemasAutomatizadosRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.SistemasAutomatizadosService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("sistemas-automatizados")
public class SistemasAutomatizadosController extends BaseController{

	private final SistemasAutomatizadosService sistemasAutomatizadosService;
	
	public SistemasAutomatizadosController(TokenDecoder tokenDecoder, SistemasAutomatizadosService sistemasAutomatizadosService) {
		super(tokenDecoder);
		this.sistemasAutomatizadosService = sistemasAutomatizadosService;
	}
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse> getSistemasAutomatizadosPdf(@Valid @RequestBody SistemasAutomatizadosRequestDto filtro) {
        byte[] resultado = this.sistemasAutomatizadosService.getReporteSistemasAutomatizados(filtro);
        
        return getPdfResponse(resultado);
    }

}
