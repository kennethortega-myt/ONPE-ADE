package pe.gob.onpe.scebackend.rest.controller.reporte;

import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteInformacionOficialRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteInformacionOficialDto;
import pe.gob.onpe.scebackend.model.service.reporte.IReporteInformacionOficialService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;

import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

import java.util.List;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("reporte-informacion-oficial")
public class ReporteInformacionOficialController extends BaseController {

    private final IReporteInformacionOficialService reporteInformacionOficialService;

    public ReporteInformacionOficialController(TokenDecoder tokenDecoder, IReporteInformacionOficialService reporteInformacionOficialService) {
       super(tokenDecoder);
        this.reporteInformacionOficialService = reporteInformacionOficialService;
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse> getReporteInformacionOficial(
            @Valid
            @RequestBody ReporteInformacionOficialRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        byte[] reporte = this.reporteInformacionOficialService.reporteInformacionOficial(filtro);

        return getPdfResponse(reporte);
    }
    @PostMapping("/consulta-informacion-oficial")
    public ResponseEntity<GenericResponse> getConsultaInformacionOficial(
            @Valid
            @RequestBody ReporteInformacionOficialRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        List<ReporteInformacionOficialDto> lista = this.reporteInformacionOficialService.consultarInformacionOficial(filtro);

        return getListResponse(lista);
    }
}
