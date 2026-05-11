package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroOrganizacionesPoliticasDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.CandidatoReporteService;
import pe.gob.onpe.sceorcbackend.exception.DataNoFoundException;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("candidatos-org-politica")
public class ReporteCandidatosController extends BaseController{

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

	Logger logger = LoggerFactory.getLogger(ReporteCandidatosController.class);
	
	private final CandidatoReporteService candidatoReporteService;
	
	public ReporteCandidatosController(CandidatoReporteService candidatoReporteService) {
		this.candidatoReporteService = candidatoReporteService;
	}
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getCandidatosPdf(@Valid @RequestBody FiltroOrganizacionesPoliticasDto filtro) {
        filtro.setSchema(schema);

        try {
            byte[] reporte = this.candidatoReporteService.reporteCandidatosPorOrgPol(filtro);
            return getPdfResponse(reporte);
        } catch (DataNoFoundException e) {
            return getErrorValidacionResponse(e.getMessage());
        } catch (Exception e) {
            throw e;
        }
    }
}
