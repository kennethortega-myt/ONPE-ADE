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
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteDetalleAvanceRegistroUbigeoRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IReporteDetalleAvanceRegistroUbigeoService;
import pe.gob.onpe.sceorcbackend.security.dto.LoginUserHeader;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@CrossOrigin
@RequestMapping("reporte-detalle-avance-registro-ubigeo")
public class ReporteDetalleAvanceRegistroUbigeoController extends BaseController {

    private final IReporteDetalleAvanceRegistroUbigeoService reporteDetalleAvanceRegistroUbigeoService;

    public ReporteDetalleAvanceRegistroUbigeoController(
            IReporteDetalleAvanceRegistroUbigeoService reporteDetalleAvanceRegistroUbigeoService) {
        this.reporteDetalleAvanceRegistroUbigeoService = reporteDetalleAvanceRegistroUbigeoService;
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse<String>> getReportePdf(
            @Valid
            @RequestBody ReporteDetalleAvanceRegistroUbigeoRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException, SQLException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        byte[] reporte = this.reporteDetalleAvanceRegistroUbigeoService.reporte(filtro, authorization);

        return getPdfResponse(reporte);
    }
}
