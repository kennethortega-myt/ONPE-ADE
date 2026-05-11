package pe.gob.onpe.scebackend.rest.controller.reporte;

import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteRelacionPuestaCeroRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteRelacionPuestoCeroDto;
import pe.gob.onpe.scebackend.model.service.reporte.IReporteRelacionPuestaCeroService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;

import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

import java.util.List;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("reporte-relacion-puesta-cero")
public class ReporteRelacionPuestaCeroController extends BaseController {
    private final IReporteRelacionPuestaCeroService reporteRelacionPuestaCeroService;

    public ReporteRelacionPuestaCeroController(TokenDecoder tokenDecoder, IReporteRelacionPuestaCeroService reporteRelacionPuestaCeroService) {
        super(tokenDecoder);
        this.reporteRelacionPuestaCeroService = reporteRelacionPuestaCeroService;
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse> reporteRelacionPuestaCero(
            @Valid
            @RequestBody ReporteRelacionPuestaCeroRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        byte[] reporte = this.reporteRelacionPuestaCeroService.reporteRelacionPuestaCero(filtro);

        return getPdfResponse(reporte);
    }

    @PostMapping("/consulta-relacion-puesta-cero")
    public ResponseEntity<GenericResponse> consultarRelacionPuestaCero(
            @Valid
            @RequestBody ReporteRelacionPuestaCeroRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        List<ReporteRelacionPuestoCeroDto> lista = this.reporteRelacionPuestaCeroService.consultaReporteRelacionPuestaCero(filtro);

        return getListResponse(lista);
    }
}
