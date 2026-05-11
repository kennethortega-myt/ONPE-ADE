package pe.gob.onpe.scebackend.rest.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.request.DetalleConfigRequestDTO;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.IDetalleConfiguracionDocumentoElectoralService;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

import java.util.Objects;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@Validated
@RequestMapping("/detalleConfiguracionDocumentoElectoral")
public class DetalleConfiguracionDocumentoElectoralController extends BaseController {

    private final IDetalleConfiguracionDocumentoElectoralService detalleConfiguracionDocumentoElectoralService;

    public DetalleConfiguracionDocumentoElectoralController(TokenDecoder tokenDecoder, IDetalleConfiguracionDocumentoElectoralService detalleConfiguracionDocumentoElectoralService) {
        super(tokenDecoder);
        this.detalleConfiguracionDocumentoElectoralService = detalleConfiguracionDocumentoElectoralService;
    }

    @PostMapping
    public ResponseEntity<GenericResponse> guardar(@RequestBody DetalleConfigRequestDTO detalle, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        GenericResponse genericResponse = new GenericResponse();
        LoginUserHeader user = getUserLogin(authorization);
        if (!detalle.getDetalles().isEmpty()) {
            this.detalleConfiguracionDocumentoElectoralService.guardarDetalleConfiguracion(detalle, user.getUsuario());
            genericResponse.setSuccess(Boolean.TRUE);
            genericResponse.setMessage("Se guardó la Información con éxito");
        }
        return new ResponseEntity<>(genericResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{idTipo}")
    public ResponseEntity<GenericResponse> detalleTipoDocumentoElectoralByTipo(@PathVariable("idTipo") Integer tipoELeccion,
                                                                               @RequestParam(name = "metodo", required = false) Integer metodo) {
        GenericResponse genericResponse = new GenericResponse();
        genericResponse.setSuccess(Boolean.TRUE);
        if (Objects.isNull(metodo)) {
            genericResponse.setData(this.detalleConfiguracionDocumentoElectoralService.obtenerDetalleByDetalleTipoEleccion(tipoELeccion));
        } else {
            genericResponse.setData(this.detalleConfiguracionDocumentoElectoralService.obtenerDetalleByDetalleTipoEleccionPaso3(tipoELeccion));
        }

        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }
}
