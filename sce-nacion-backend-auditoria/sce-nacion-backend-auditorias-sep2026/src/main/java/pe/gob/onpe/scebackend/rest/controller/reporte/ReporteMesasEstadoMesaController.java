package pe.gob.onpe.scebackend.rest.controller.reporte;

import jakarta.validation.Valid;
import lombok.extern.java.Log;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasEstadoMesaRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.IReporteMesasEstadoMesaService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;

import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("reporte-mesas-estado-mesa")
@Log
public class ReporteMesasEstadoMesaController extends BaseController {
    private final IReporteMesasEstadoMesaService reporteMesasEstadoMesaService;

    public ReporteMesasEstadoMesaController(TokenDecoder tokenDecoder, IReporteMesasEstadoMesaService reporteMesasEstadoMesaService) {
       super(tokenDecoder);
        this.reporteMesasEstadoMesaService = reporteMesasEstadoMesaService;
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse> getAvanceMesaPdf(
            @Valid
            @RequestBody ReporteMesasEstadoMesaRequestDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        byte[] reporte = this.reporteMesasEstadoMesaService.reporteMesasEstadoMesa(filtro);

        return getPdfResponse(reporte);
    }
}
