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
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AsistenciaMiembroMesaService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("asistencia-miembromesa")
public class AsistenciaMiembroMesaController extends BaseController{

	Logger logger = LoggerFactory.getLogger(AsistenciaMiembroMesaController.class);
	
	@Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;
	
	private final AsistenciaMiembroMesaService asistenciaMiembroMesaService;
	
	public AsistenciaMiembroMesaController(AsistenciaMiembroMesaService asistenciaMiembroMesaService) {
		this.asistenciaMiembroMesaService = asistenciaMiembroMesaService;
	}
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getAsistenciaMiembroMesaPdf(@Valid @RequestBody AsistenciaMiembroMesaRequestDto filtro) {
        filtro.setEsquema(schema);
        byte[] resultado = this.asistenciaMiembroMesaService.getReporteAsistenciaMiembroMesa(filtro);
        
        return getPdfResponse(resultado);
    }
}
