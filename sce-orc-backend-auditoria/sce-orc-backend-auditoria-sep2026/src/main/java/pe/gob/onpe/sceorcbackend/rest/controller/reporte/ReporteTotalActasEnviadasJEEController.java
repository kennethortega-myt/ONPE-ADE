package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import java.sql.SQLException;

import org.springframework.http.HttpHeaders;
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
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteTotalEnviadaJEERequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IReporteTotalActasEnviadasJEEService;
import pe.gob.onpe.sceorcbackend.security.dto.LoginUserHeader;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("reporte-total-actas-enviadas-jee")
public class ReporteTotalActasEnviadasJEEController extends BaseController {
	
    private final IReporteTotalActasEnviadasJEEService reporteTotalActasEnviadasJEEService;

    public ReporteTotalActasEnviadasJEEController(IReporteTotalActasEnviadasJEEService reporteTotalActasEnviadasJEEService) {
        this.reporteTotalActasEnviadasJEEService = reporteTotalActasEnviadasJEEService;
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getReportePdf(
            @Valid
            @RequestBody ReporteTotalEnviadaJEERequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException, SQLException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        byte[] reporte = this.reporteTotalActasEnviadasJEEService.reporte(filtro, authorization);
        
        return getPdfResponse(reporte);
    }
}