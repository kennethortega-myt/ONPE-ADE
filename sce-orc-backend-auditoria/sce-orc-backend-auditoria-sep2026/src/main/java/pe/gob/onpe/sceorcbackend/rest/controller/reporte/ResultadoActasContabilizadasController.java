package pe.gob.onpe.sceorcbackend.rest.controller.reporte;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import pe.gob.onpe.sceorcbackend.exception.DataNoFoundException;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroResultadoContabilizadasDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ResultadoActasContabilizadasDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.service.reportes.resultados.ResultadoActasContService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("resultados-contabilizadas")
public class ResultadoActasContabilizadasController extends BaseController{

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    private final ResultadoActasContService resultadoActasContService;

    public ResultadoActasContabilizadasController(ResultadoActasContService resultadoActasContService) {
        this.resultadoActasContService = resultadoActasContService;
    }

    @PostMapping("/")
    public ResponseEntity<GenericResponse<ResultadoActasContabilizadasDto>> getResultadoActasContabilizadas(@Valid @RequestBody FiltroResultadoContabilizadasDto filtro) {
        filtro.setEsquema(schema);
        GenericResponse<ResultadoActasContabilizadasDto> genericResponse = new GenericResponse<>();
        try {
            ResultadoActasContabilizadasDto actas = this.resultadoActasContService.busquedaResultadosActasContabilizadas(filtro);
            
            if (actas != null) {
                genericResponse.setSuccess(Boolean.TRUE);
                genericResponse.setMessage(MSG_REPORTE_GENERADO);
                genericResponse.setData(actas);
                return new ResponseEntity<>(genericResponse, HttpStatus.OK);
            } else {
                genericResponse.setSuccess(Boolean.FALSE);
                genericResponse.setMessage(MSG_REPORTE_SIN_DATA);
                return new ResponseEntity<>(genericResponse, HttpStatus.OK);
            }
            
        } catch (DataNoFoundException e) {
        	genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage(MSG_REPORTE_SIN_DATA);
            
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
            
        } catch (Exception e) {
            throw e;
        }
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getResultadoActasContabilizadasPdf(@Valid @RequestBody FiltroResultadoContabilizadasDto filtro) {
        filtro.setEsquema(schema);
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
