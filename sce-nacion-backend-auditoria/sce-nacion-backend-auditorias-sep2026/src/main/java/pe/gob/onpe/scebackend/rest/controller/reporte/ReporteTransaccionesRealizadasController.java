package pe.gob.onpe.scebackend.rest.controller.reporte;

import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.JRException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteTransaccionesRealizadasRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ListaUsuariosReporteTransaccionesDTO;
import pe.gob.onpe.scebackend.model.service.reporte.IReporteTransaccionesRealizadasService;
import pe.gob.onpe.scebackend.rest.controller.BaseController;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

import java.sql.SQLException;
import java.util.List;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@RequestMapping("reporte-transacciones-realizadas")
@Log4j2
public class ReporteTransaccionesRealizadasController extends BaseController {

    private final IReporteTransaccionesRealizadasService reporteTransaccionesRealizadasService;

    public ReporteTransaccionesRealizadasController(TokenDecoder tokenDecoder, IReporteTransaccionesRealizadasService reporteTransaccionesRealizadasService) {
        super(tokenDecoder);
        this.reporteTransaccionesRealizadasService = reporteTransaccionesRealizadasService;
    }

    @PostMapping("/listaUsuarios")
    public ResponseEntity<GenericResponse> listaUsuarios(@Valid @RequestBody ReporteTransaccionesRealizadasRequestDto filtro) {
        List<ListaUsuariosReporteTransaccionesDTO> lista = reporteTransaccionesRealizadasService.listaUsuarios(filtro);
        return getListResponse(lista);
    }

    @PostMapping("/base64")
    public ResponseEntity<GenericResponse> getTransaccionesRealizadasPdf(@Valid @RequestBody ReporteTransaccionesRealizadasRequestDto filtro, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws JRException, SQLException {
        byte[] resultado = this.reporteTransaccionesRealizadasService.reporte(filtro, authorization);
        return getPdfResponse(resultado);
    }
}
