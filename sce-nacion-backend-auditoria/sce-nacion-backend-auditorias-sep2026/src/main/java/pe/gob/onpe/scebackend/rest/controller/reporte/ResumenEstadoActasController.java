package pe.gob.onpe.scebackend.rest.controller.reporte;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.onpe.scebackend.model.dto.reportes.EstadoActasOdpeReporteDto;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroEstadoActasOdpeDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.ResumenEstadoActasService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("resumen-estado-actas")
public class ResumenEstadoActasController extends BaseController {

    private final ResumenEstadoActasService resumenEstadoActasService;

    public ResumenEstadoActasController(ResumenEstadoActasService resumenEstadoActasService, TokenDecoder tokenDecoder) {
        super(tokenDecoder);
        this.resumenEstadoActasService = resumenEstadoActasService;
    }

    @PostMapping("/")
    public ResponseEntity<GenericResponse> getResumenEstadoActas(@Valid @RequestBody FiltroEstadoActasOdpeDto filtro) {
        GenericResponse genericResponse = new GenericResponse();
        EstadoActasOdpeReporteDto estadoActas = this.resumenEstadoActasService.getListaResumenEstadoActas(filtro);

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
    public ResponseEntity<GenericResponse> getResumenEstadoActasPdf(@Valid @RequestBody FiltroEstadoActasOdpeDto filtro) {
        byte[] resultado = this.resumenEstadoActasService.reporteResumenEstadoActas(filtro);

        return getPdfResponse(resultado);
    }
}
