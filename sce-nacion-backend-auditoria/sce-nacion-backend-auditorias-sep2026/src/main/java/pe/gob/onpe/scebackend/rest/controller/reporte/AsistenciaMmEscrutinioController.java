package pe.gob.onpe.scebackend.rest.controller.reporte;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.scebackend.model.dto.request.reporte.AsistenciaMiembroMesaRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.AsistenciaMmEscrutinioService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("asistencia-escrutinio")
public class AsistenciaMmEscrutinioController extends BaseController{

	private final AsistenciaMmEscrutinioService asistenciaMmEscrutinioService;
	
	public AsistenciaMmEscrutinioController(TokenDecoder tokenDecoder, AsistenciaMmEscrutinioService asistenciaMmEscrutinioService) {
        super(tokenDecoder);
        this.asistenciaMmEscrutinioService = asistenciaMmEscrutinioService;
	}
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse> getAsistenciaMmEscrutinioPdf(@Valid @RequestBody AsistenciaMiembroMesaRequestDto filtro) {
        byte[] resultado = this.asistenciaMmEscrutinioService.getReporteAsistenciaMmEscrutinio(filtro);
        
        return getPdfResponse(resultado);
    }
}
