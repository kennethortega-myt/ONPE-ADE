package pe.gob.onpe.scebackend.rest.controller.reporte;

import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteOmisosRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.reporte.IReporteOmisosService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;

import jakarta.validation.Valid;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("reporte-omisos")
public class ReporteOmisosController extends BaseController {

    private final IReporteOmisosService reporteOmisosService;

    public ReporteOmisosController(TokenDecoder tokenDecoder, IReporteOmisosService reporteOmisosService) {
       super(tokenDecoder);
        this.reporteOmisosService = reporteOmisosService;
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse> getReporteElectoresOmisos(
            @Valid
            @RequestBody ReporteOmisosRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());

        byte[] reporte = this.reporteOmisosService.reporteOmisos(filtro);

        return getPdfResponse(reporte);
    }

}
