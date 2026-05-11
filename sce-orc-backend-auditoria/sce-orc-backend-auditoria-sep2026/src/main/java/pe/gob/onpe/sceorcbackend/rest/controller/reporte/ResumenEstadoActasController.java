package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.sceorcbackend.model.dto.EstadoActasOdpeReporteDto;
import pe.gob.onpe.sceorcbackend.model.dto.FiltroEstadoActasOdpeDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.ResumenEstadoActasService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("resumen-estado-actas")
public class ResumenEstadoActasController extends BaseController{
    Logger logger = LoggerFactory.getLogger(ResumenEstadoActasController.class);

    private final ResumenEstadoActasService resumenEstadoActasService;

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    public ResumenEstadoActasController(ResumenEstadoActasService resumenEstadoActasService) {
        this.resumenEstadoActasService = resumenEstadoActasService;
    }

    @PostMapping("/")
    public ResponseEntity<GenericResponse<EstadoActasOdpeReporteDto>> getResumenEstadoActas(@Valid @RequestBody FiltroEstadoActasOdpeDto filtro) {
        filtro.setEsquema(schema);
        GenericResponse<EstadoActasOdpeReporteDto> genericResponse = new GenericResponse<>();
        EstadoActasOdpeReporteDto estadoActas = this.resumenEstadoActasService.getResumenListaEstadoActas(filtro);

        if(estadoActas != null) {
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setData(estadoActas);
            genericResponse.setMessage("Se listo correctamente");
        } else {
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage("No existen coincidencias para el filtro seleccionado");
        }

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getResumenEstadoActasPdf(@RequestBody FiltroEstadoActasOdpeDto filtro) {
        filtro.setEsquema(schema);
        byte[] resultado = this.resumenEstadoActasService.reporteResumenEstadoActas(filtro);

        return getPdfResponse(resultado);
    }
}
