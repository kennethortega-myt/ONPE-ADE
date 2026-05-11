package pe.gob.onpe.scebackend.rest.controller.reporte;

import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasSinOmisosRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.reporte.IReporteMesasSinOmisosService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;

import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("reporte-mesas-sin-omisos")
public class ReporteMesasSinOmisosController extends BaseController {

    private final IReporteMesasSinOmisosService reporteMesasSinOmisosService;

    public ReporteMesasSinOmisosController(TokenDecoder tokenDecoder, IReporteMesasSinOmisosService reporteMesasSinOmisosService) {
       super(tokenDecoder);
        this.reporteMesasSinOmisosService = reporteMesasSinOmisosService;
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse> getReporteMesasSinOmisos(
            @Valid
            @RequestBody ReporteMesasSinOmisosRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        byte[] reporte = this.reporteMesasSinOmisosService.reporteMesasSinOmisos(filtro);

        return getPdfResponse(reporte);
    }

}
