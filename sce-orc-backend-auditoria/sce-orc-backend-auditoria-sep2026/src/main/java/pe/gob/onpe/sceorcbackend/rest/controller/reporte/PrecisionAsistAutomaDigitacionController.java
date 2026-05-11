package pe.gob.onpe.sceorcbackend.rest.controller.reporte;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroPrecisionAsistAutomaDigitacionDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IPrecisionAsistAutomaDigitacionService;
import pe.gob.onpe.sceorcbackend.security.dto.LoginUserHeader;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
@RestController
@RequestMapping("precision-asist-automa-digitacion")
public class PrecisionAsistAutomaDigitacionController extends BaseController {

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    private final IPrecisionAsistAutomaDigitacionService precisionAsistAutomaDigitacionService;

    public PrecisionAsistAutomaDigitacionController(IPrecisionAsistAutomaDigitacionService precisionAsistAutomaDigitacionService) {
        this.precisionAsistAutomaDigitacionService = precisionAsistAutomaDigitacionService;
    }

    @PostMapping("/resumen/base64")
    public ResponseEntity<GenericResponse<String>> getPrecisionAsistAutomaDigitacionResumen(
            @RequestBody FiltroPrecisionAsistAutomaDigitacionDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
            ) {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        filtro.setEsquema(schema);

        byte[] resultado = this.precisionAsistAutomaDigitacionService.getPrecisionAsistAutomaDigitacionResumen(filtro);

        return getPdfResponse(resultado);
    }

    @PostMapping("/detalle/base64")
    public ResponseEntity<GenericResponse<String>> getPrecisionAsistAutomaDigitacionDetalle(
            @RequestBody FiltroPrecisionAsistAutomaDigitacionDto filtro,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
            ) {

        LoginUserHeader user = getUserLogin(authorization);
        filtro.setUsuario(user.getUsuario());
        filtro.setEsquema(schema);

        byte[] resultado = this.precisionAsistAutomaDigitacionService.getPrecisionAsistAutomaDigitacionDetalle(filtro);

        return getPdfResponse(resultado);
    }
}
