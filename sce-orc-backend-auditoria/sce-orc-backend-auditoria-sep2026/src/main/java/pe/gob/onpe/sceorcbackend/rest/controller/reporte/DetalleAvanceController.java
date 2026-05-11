package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroDetalleAvanceDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IDetalleAvanceService;
import pe.gob.onpe.sceorcbackend.security.dto.LoginUserHeader;
import pe.gob.onpe.sceorcbackend.security.TokenDecoder;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.sql.SQLException;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@RequestMapping("detalle-avance")
public class DetalleAvanceController extends BaseController {
    private final IDetalleAvanceService detalleAvanceService;

    public DetalleAvanceController(IDetalleAvanceService detalleAvanceService) {
        this.detalleAvanceService = detalleAvanceService;
    }

    @PostMapping("/miembros-mesa-escrutinio/base64")
    public ResponseEntity<GenericResponse<String>> getPdfMiembrosMesaEscrutinio(
            @Valid
            @RequestBody FiltroDetalleAvanceDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException, SQLException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        byte[] reporte = this.detalleAvanceService.reporteMiembrosMesaEscrutinio(filtro, authorization);

        return getPdfResponse(reporte);
    }

    @PostMapping("/personeros/base64")
    public ResponseEntity<GenericResponse<String>> getPdfPersoneros(
            @Valid
            @RequestBody FiltroDetalleAvanceDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException, SQLException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        byte[] reporte = this.detalleAvanceService.reportePersoneros(filtro, authorization);

        return getPdfResponse(reporte);

    }
}
