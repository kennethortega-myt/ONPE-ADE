package pe.gob.onpe.scebackend.rest.controller.reporte;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroActasDigitalizadasDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.ReporteActasDigitalizadasService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("actas-digitalizadas")
public class ReporteActasDigitalizadasController extends BaseController{

	private final ReporteActasDigitalizadasService reporteActasDigitalizadasService;
	
	public ReporteActasDigitalizadasController(TokenDecoder tokenDecoder, ReporteActasDigitalizadasService reporteActasDigitalizadasService) {
		super(tokenDecoder);
        this.reporteActasDigitalizadasService = reporteActasDigitalizadasService;
	}
	
	@PostMapping("/base64-pdf")
    public ResponseEntity<GenericResponse> getActasDigitalizadasPdf(@Valid @RequestBody FiltroActasDigitalizadasDto filtro) {
        byte[] actas = this.reporteActasDigitalizadasService.reporteActasDigitalizadas(filtro);
        
        return getPdfResponse(actas);
    }
	
	@PostMapping("/base64-excel")
    public ResponseEntity<GenericResponse> getActasDigitalizadasExcel(@Valid @RequestBody FiltroActasDigitalizadasDto filtro) {
        byte[] actas = this.reporteActasDigitalizadasService.reporteActasDigitalizadasExcel(filtro);
        
        return getPdfResponse(actas);
    }
}
