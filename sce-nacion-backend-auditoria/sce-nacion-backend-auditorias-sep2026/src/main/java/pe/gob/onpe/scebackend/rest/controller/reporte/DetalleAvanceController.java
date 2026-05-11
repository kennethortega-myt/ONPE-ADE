package pe.gob.onpe.scebackend.rest.controller.reporte;

import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroDetalleAvanceDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.reporte.IDetalleAvanceService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

import java.sql.SQLException;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("detalle-avance")
public class DetalleAvanceController extends BaseController {
    private final IDetalleAvanceService detalleAvanceService;


    public DetalleAvanceController(TokenDecoder tokenDecoder, IDetalleAvanceService detalleAvanceService) {
        super(tokenDecoder);
        this.detalleAvanceService = detalleAvanceService;
    }

    @PostMapping("/miembros-mesa-escrutinio/base64")
    public ResponseEntity<GenericResponse> getPdfMiembrosMesaEscrutinio(
            @Valid
            @RequestBody FiltroDetalleAvanceDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException, SQLException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        byte[] reporte = this.detalleAvanceService.reporteMiembrosMesaEscrutinio(filtro, authorization);

        return getPdfResponse(reporte);
    }

    @PostMapping("/personeros/base64")
    public ResponseEntity<GenericResponse> getPdfPersoneros(
            @Valid
            @RequestBody FiltroDetalleAvanceDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException, SQLException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        byte[] reporte = this.detalleAvanceService.reportePersoneros(filtro, authorization);

        return getPdfResponse(reporte);

    }
}
