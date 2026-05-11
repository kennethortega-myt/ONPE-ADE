package pe.gob.onpe.scebackend.rest.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.scebackend.model.dto.MesaComboDto;
import pe.gob.onpe.scebackend.model.dto.PuestaCeroActaDto;
import pe.gob.onpe.scebackend.model.service.IConfiguracionProcesoElectoralService;
import pe.gob.onpe.scebackend.model.service.PuestaCeroActaService;
import pe.gob.onpe.scebackend.security.dto.GenericResponse;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@Slf4j
@RestController
@RequestMapping("/puesta-cero-acta")
public class PuestaCeroActaController extends BaseController {

    private final PuestaCeroActaService puestaCeroActaService;

    private final IConfiguracionProcesoElectoralService confProcesoService;

    public PuestaCeroActaController(TokenDecoder tokenDecoder, PuestaCeroActaService puestaCeroActaService,
            IConfiguracionProcesoElectoralService confProcesoService) {
        super(tokenDecoder);
        this.puestaCeroActaService = puestaCeroActaService;
        this.confProcesoService = confProcesoService;
    }

    @GetMapping("/{codMesa}")
    public ResponseEntity<GenericResponse<MesaComboDto>> getMesa(@PathVariable String codMesa,
            @RequestHeader("X-Tenant-Id") String tenant) {
        GenericResponse<MesaComboDto> response = new GenericResponse<>();

        String schema = confProcesoService.getEsquema(tenant);
        var mesaOpt = this.puestaCeroActaService.buscarMesaPuestaCeroActa(schema, codMesa);
        if (mesaOpt.isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Mesa no encontrada");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        response.setSuccess(true);
        response.setData(mesaOpt.get());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/save")
    @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    public ResponseEntity<GenericResponse<Boolean>> procesarPuestaCeroActa(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestHeader("X-Tenant-Id") String tenant,
            @Valid @RequestBody PuestaCeroActaDto data) {
        LoginUserHeader user = getUserLogin(authorization);

        GenericResponse<Boolean> response = new GenericResponse<>();

        try {
            String schema = confProcesoService.getEsquema(tenant);
            this.puestaCeroActaService.procesarPuestaCeroActa(schema, data, user.getUsuario());
            response.setSuccess(true);
            response.setMessage("Operación realizada con éxito");
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.warn("Error en reinicio de acta, save", e);
            response.setSuccess(false);
            response.setMessage("Error interno del sistema");
            return ResponseEntity.internalServerError().body(response);
        }
    }

}
