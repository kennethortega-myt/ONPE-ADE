package pe.gob.onpe.scebackend.rest.controller.reporte;

import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteAuditoriaDigitacionActaRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.reporte.IReporteAuditoriaDigitacionService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;

import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

import java.sql.SQLException;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("reporte-auditoria-digitacion")
public class ReporteAuditoriaDigitacionController extends BaseController {

    private final IReporteAuditoriaDigitacionService reporteVerificacionDigitacionService;

    public ReporteAuditoriaDigitacionController(TokenDecoder tokenDecoder, IReporteAuditoriaDigitacionService reporteVerificacionDigitacionService) {
        super(tokenDecoder);
        this.reporteVerificacionDigitacionService = reporteVerificacionDigitacionService;
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse> getAuditoriaDigitacionPdf(
            @Valid
            @RequestBody ReporteAuditoriaDigitacionActaRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException, SQLException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

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
