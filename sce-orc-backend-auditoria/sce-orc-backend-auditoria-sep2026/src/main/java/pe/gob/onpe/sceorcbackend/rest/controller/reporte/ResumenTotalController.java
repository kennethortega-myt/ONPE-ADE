package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.DetalleResumenTotal;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroResumenTotalDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.ResumenTotalService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.util.List;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("resumen-total")
public class ResumenTotalController extends BaseController{

	Logger logger = LoggerFactory.getLogger(ResumenTotalController.class);

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

	private final ResumenTotalService resumenTotalService;
	
	public ResumenTotalController(ResumenTotalService resumenTotalService) {
		this.resumenTotalService = resumenTotalService;
	}
	
	@PostMapping("/")
    public ResponseEntity<GenericResponse<List<DetalleResumenTotal>>> getResumenTotalCentroComputo(@Valid @RequestBody FiltroResumenTotalDto filtro) {
        filtro.setEsquema(schema);
        List<DetalleResumenTotal> resumenTotal = this.resumenTotalService.resumenTotalCentroComputo(filtro);
        
        GenericResponse<List<DetalleResumenTotal>> genericResponse = new GenericResponse<>();

        if (resumenTotal != null && !resumenTotal.isEmpty()) {
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setMessage(MSG_REPORTE_GENERADO);
            genericResponse.setData(resumenTotal);
        } else {
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage(MSG_REPORTE_SIN_DATA);
        }

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getResumenTotalCentroComputoPDF(@Valid @RequestBody FiltroResumenTotalDto filtro) {
        filtro.setEsquema(schema);
        byte[] resultado = this.resumenTotalService.getReporteResumenTotalPdf(filtro);
        
        return getPdfResponse(resultado);
    }
}
