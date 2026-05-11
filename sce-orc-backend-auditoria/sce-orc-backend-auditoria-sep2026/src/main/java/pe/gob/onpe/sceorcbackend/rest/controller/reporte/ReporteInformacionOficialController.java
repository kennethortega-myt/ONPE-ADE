package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteInformacionOficialDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteInformacionOficialRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IReporteInformacionOficialService;
import pe.gob.onpe.sceorcbackend.security.dto.LoginUserHeader;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.util.List;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("reporte-informacion-oficial")
public class ReporteInformacionOficialController extends BaseController {

    private final IReporteInformacionOficialService reporteInformacionOficialService;

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    public ReporteInformacionOficialController(IReporteInformacionOficialService reporteInformacionOficialService) {
        this.reporteInformacionOficialService = reporteInformacionOficialService;
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getReporteInformacionOficial(
            @Valid
            @RequestBody ReporteInformacionOficialRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        filtro.setEsquema(schema);

        byte[] reporte = this.reporteInformacionOficialService.reporteInformacionOficial(filtro);

        return getPdfResponse(reporte);
    }
    @PostMapping("/consulta-informacion-oficial")
    public ResponseEntity<GenericResponse<List<ReporteInformacionOficialDto>>> getConsultaInformacionOficial(
            @Valid
            @RequestBody ReporteInformacionOficialRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        filtro.setEsquema(schema);

        List<ReporteInformacionOficialDto> listaInformacion = this.reporteInformacionOficialService.consultarInformacionOficial(filtro);

        GenericResponse<List<ReporteInformacionOficialDto>> genericResponse = new GenericResponse<>();

        if (listaInformacion != null && !listaInformacion.isEmpty()) {
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setMessage(MSG_REPORTE_GENERADO);
            genericResponse.setData(listaInformacion);
        } else {
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage(MSG_REPORTE_SIN_DATA);
        }

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        
    }
}
