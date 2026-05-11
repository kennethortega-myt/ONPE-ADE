package pe.gob.onpe.scebackend.rest.controller.reporte;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.scebackend.model.dto.reportes.EstadoActasOdpeReporteDto;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroEstadoActasOdpeDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.ReporteEstadoActasOdpeService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("estado-actas-odpe")
public class EstadoActasOdpeController extends BaseController{

	private final ReporteEstadoActasOdpeService reporteEstadoActasOdpeService;

    public EstadoActasOdpeController(ReporteEstadoActasOdpeService reporteEstadoActasOdpeService, TokenDecoder tokenDecoder) {
        super(tokenDecoder);
        this.reporteEstadoActasOdpeService = reporteEstadoActasOdpeService;
    }
	
	@PostMapping("/")
    public ResponseEntity<GenericResponse> getEstadoActasOdpe(@Valid @RequestBody FiltroEstadoActasOdpeDto filtro) {
        GenericResponse genericResponse = new GenericResponse();
        EstadoActasOdpeReporteDto estadoActas = this.reporteEstadoActasOdpeService.getListaEstadoActasOdpe(filtro);
        
        HttpStatus httpStatus = null;
        if(estadoActas != null) {
        	genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setData(estadoActas);
            genericResponse.setMessage("Se listo correctamente");
            httpStatus = HttpStatus.OK;
        } else {
        	genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage("No existen coincidencias para el filtro seleccionado");
            httpStatus = HttpStatus.OK;
        }
        
        return new ResponseEntity<>(genericResponse, httpStatus);
    }
	
	@PostMapping("/base64")
    public ResponseEntity<GenericResponse> getEstadoActasOdpePdf(@Valid @RequestBody FiltroEstadoActasOdpeDto filtro) {
        byte[] resultado = this.reporteEstadoActasOdpeService.reporteEstadoActasOdpe(filtro);
        
        return getPdfResponse(resultado);
    }
	
}
