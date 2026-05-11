package pe.gob.onpe.scebackend.rest.controller.reporte;

import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteTotalEnviadaJEERequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.reporte.IReporteTotalActasEnviadasJEEService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;

import jakarta.validation.Valid;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

import java.sql.SQLException;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("reporte-total-actas-enviadas-jee")
@Log4j2
public class ReporteTotalActasEnviadasJEEController extends BaseController {
    private final IReporteTotalActasEnviadasJEEService reporteTotalActasEnviadasJEEService;

    public ReporteTotalActasEnviadasJEEController(TokenDecoder tokenDecoder, IReporteTotalActasEnviadasJEEService reporteTotalActasEnviadasJEEService) {
        super(tokenDecoder);
        this.reporteTotalActasEnviadasJEEService = reporteTotalActasEnviadasJEEService;
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse> getReportePdf(
            @Valid
            @RequestBody ReporteTotalEnviadaJEERequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException, SQLException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        byte[] reporte = this.reporteTotalActasEnviadasJEEService.reporte(filtro, authorization);

        return getPdfResponse(reporte);
    }
}
