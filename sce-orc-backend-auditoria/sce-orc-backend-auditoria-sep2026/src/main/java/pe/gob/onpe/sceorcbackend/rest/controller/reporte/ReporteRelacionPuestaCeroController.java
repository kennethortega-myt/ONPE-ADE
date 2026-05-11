package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteRelacionPuestaCeroRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.ReporteRelacionPuestoCeroDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IReporteRelacionPuestaCeroService;
import pe.gob.onpe.sceorcbackend.security.dto.LoginUserHeader;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("reporte-relacion-puesta-cero")
public class ReporteRelacionPuestaCeroController extends BaseController {

	@Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;
	
	private final IReporteRelacionPuestaCeroService reporteRelacionPuestaCeroService;

    public ReporteRelacionPuestaCeroController(IReporteRelacionPuestaCeroService reporteRelacionPuestaCeroService) {
        this.reporteRelacionPuestaCeroService = reporteRelacionPuestaCeroService;
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> reporteRelacionPuestaCero(
            @Valid
            @RequestBody ReporteRelacionPuestaCeroRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        filtro.setEsquema(schema);
        byte[] reporte = this.reporteRelacionPuestaCeroService.reporteRelacionPuestaCero(filtro);
        
        return getPdfResponse(reporte);
    }

    @PostMapping("/consulta-relacion-puesta-cero")
    public ResponseEntity<GenericResponse<List<ReporteRelacionPuestoCeroDto>>> consultarRelacionPuestaCero(
            @Valid
            @RequestBody ReporteRelacionPuestaCeroRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        filtro.setEsquema(schema);
        List<ReporteRelacionPuestoCeroDto> listaPuestaCero = this.reporteRelacionPuestaCeroService.consultaReporteRelacionPuestaCero(filtro);
        
        HttpStatus httpStatus = HttpStatus.OK;
        GenericResponse<List<ReporteRelacionPuestoCeroDto>> genericResponse = new GenericResponse<>();

        if (listaPuestaCero != null && !listaPuestaCero.isEmpty()) {
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setMessage(MSG_REPORTE_GENERADO);
            genericResponse.setData(listaPuestaCero);
        } else {
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage(MSG_REPORTE_SIN_DATA);
        }

        return new ResponseEntity<>(genericResponse, httpStatus);
    }
}
