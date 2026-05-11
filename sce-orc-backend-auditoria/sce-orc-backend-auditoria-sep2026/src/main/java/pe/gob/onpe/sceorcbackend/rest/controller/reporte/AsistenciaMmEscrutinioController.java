package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.AsistenciaMiembroMesaRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AsistenciaMmEscrutinioService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("asistencia-escrutinio")
public class AsistenciaMmEscrutinioController extends BaseController{

	Logger logger = LoggerFactory.getLogger(AsistenciaMmEscrutinioController.class);
	
	@Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;
	
	private final AsistenciaMmEscrutinioService asistenciaMmEscrutinioService;
	
	public AsistenciaMmEscrutinioController(AsistenciaMmEscrutinioService asistenciaMmEscrutinioService) {
		this.asistenciaMmEscrutinioService = asistenciaMmEscrutinioService;
	}
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getAsistenciaMmEscrutinioPdf(@Valid @RequestBody AsistenciaMiembroMesaRequestDto filtro) {
        filtro.setEsquema(schema);
        byte[] resultado = this.asistenciaMmEscrutinioService.getReporteAsistenciaMmEscrutinio(filtro);
        
        return getPdfResponse(resultado);
    }
}
