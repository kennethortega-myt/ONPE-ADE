package pe.gob.onpe.scebackend.rest.controller.reporte;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.scebackend.model.dto.reportes.DetalleResumenTotal;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroResumenTotalDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.ResumenTotalService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("resumen-total")
public class ResumenTotalController extends BaseController{

	private final ResumenTotalService resumenTotalService;
	
	public ResumenTotalController(TokenDecoder tokenDecoder, ResumenTotalService resumenTotalService) {
        super(tokenDecoder);
        this.resumenTotalService = resumenTotalService;
	}
	
	@PostMapping("/")
    public ResponseEntity<GenericResponse> getResumenTotalCentroComputo(@Valid @RequestBody FiltroResumenTotalDto filtro) {
        List<DetalleResumenTotal> resumenTotal = this.resumenTotalService.resumenTotalCentroComputo(filtro);
        
        return getListResponse(resumenTotal);
    }
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse> getResumenTotalCentroComputoPDF(@Valid @RequestBody FiltroResumenTotalDto filtro) {
        byte[] resultado = this.resumenTotalService.getReporteResumenTotalPdf(filtro);
        
        return getPdfResponse(resultado);
    }
}
