package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteAuditoriaDigitacionActaRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IReporteAuditoriaDigitacionService;
import pe.gob.onpe.sceorcbackend.security.dto.LoginUserHeader;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.sql.SQLException;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("reporte-auditoria-digitacion")
public class ReporteAuditoriaDigitacionController extends BaseController {

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    private final IReporteAuditoriaDigitacionService reporteVerificacionDigitacionService;

    public ReporteAuditoriaDigitacionController(IReporteAuditoriaDigitacionService reporteVerificacionDigitacionService) {
        this.reporteVerificacionDigitacionService = reporteVerificacionDigitacionService;
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getAuditoriaDigitacionPdf(
            @Valid
            @RequestBody ReporteAuditoriaDigitacionActaRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException, SQLException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        filtro.setSchema(schema);

        try {
            byte[] reporte = this.reporteVerificacionDigitacionService.reporteAuditoriaDigitacion(filtro);
            return getPdfResponse(reporte);
        } catch (IllegalArgumentException e) {
            return getErrorValidacionResponse(e.getMessage());
        } catch (Exception e) {
            throw e;
        }
    }
}
