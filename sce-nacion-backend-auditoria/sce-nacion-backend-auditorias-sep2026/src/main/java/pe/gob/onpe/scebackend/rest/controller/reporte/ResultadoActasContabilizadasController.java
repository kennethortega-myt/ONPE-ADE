package pe.gob.onpe.scebackend.rest.controller.reporte;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.scebackend.exeption.DataNoFoundException;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroResultadoContabilizadasDto;
import pe.gob.onpe.scebackend.model.dto.reportes.ResultadoActasContabilizadasDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.service.reportes.resultados.ResultadoActasContService;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("resultados-contabilizadas")
public class ResultadoActasContabilizadasController extends BaseController{

	private final ResultadoActasContService resultadoActasContService;
	
	public ResultadoActasContabilizadasController(TokenDecoder tokenDecoder, ResultadoActasContService resultadoActasContService) {
        super(tokenDecoder);
        this.resultadoActasContService = resultadoActasContService;
	}
	
	@PostMapping("/")
    public ResponseEntity<GenericResponse> getResultadoActasContabilizadas(@Valid @RequestBody FiltroResultadoContabilizadasDto filtro) {
        try {
            ResultadoActasContabilizadasDto resultados = this.resultadoActasContService.busquedaResultadosActasContabilizadas(filtro);
            return getObjectResponse(resultados);
        } catch (DataNoFoundException e) {
            return getErrorValidacionResponse(e.getMessage());
        } catch (Exception e) {
            throw e;
        }
    }
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse> getResultadoActasContabilizadasPdf(@Valid @RequestBody FiltroResultadoContabilizadasDto filtro) {
        try {
            byte[] resultado = this.resultadoActasContService.getReporteResultadoActasContabilizadas(filtro);
            return getPdfResponse(resultado);
        } catch (DataNoFoundException e) {
            return getErrorValidacionResponse(e.getMessage());
        } catch (Exception e) {
            throw e;
        }
    }
	
}
