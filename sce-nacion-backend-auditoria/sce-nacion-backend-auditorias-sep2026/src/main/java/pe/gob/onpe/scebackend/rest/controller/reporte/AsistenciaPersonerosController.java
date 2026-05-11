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
import pe.gob.onpe.scebackend.model.service.AsistenciaPersonerosService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("asistencia-personeros")
public class AsistenciaPersonerosController extends BaseController{

	private final AsistenciaPersonerosService asistenciaPersonerosService;
	
	public AsistenciaPersonerosController(TokenDecoder tokenDecoder, AsistenciaPersonerosService asistenciaPersonerosService) {
		super(tokenDecoder);
		this.asistenciaPersonerosService = asistenciaPersonerosService;
	}
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse> getAsistenciaPersonerosPdf(@Valid @RequestBody AsistenciaMiembroMesaRequestDto filtro) {
        byte[] resultado = this.asistenciaPersonerosService.getReporteAsistenciaPersoneros(filtro);
        
        return getPdfResponse(resultado);
    }
}
