package pe.gob.onpe.sceorcbackend.rest.controller;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.sceorcbackend.model.dto.MesaDTO;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.request.PuestaCeroActaDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.PuestaCeroActaService;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/puesta-cero-acta")
public class PuestaCeroActaController {

    private final TokenUtilService tokenUtilService;

    private final PuestaCeroActaService puestaCeroActaService;

    @GetMapping("/{codMesa}")
    public ResponseEntity<GenericResponse<MesaDTO>> getMesa(@PathVariable String codMesa) {
        GenericResponse<MesaDTO> response = new GenericResponse<>();

        var mesaOpt = this.puestaCeroActaService.buscarMesaPuestaCeroActa(codMesa);
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
    @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
    public ResponseEntity<GenericResponse<Boolean>> procesarPuestaCeroActa(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @Valid @RequestBody PuestaCeroActaDto data) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

        GenericResponse<Boolean> response = new GenericResponse<>();

        try {
            this.puestaCeroActaService.procesarPuestaCeroActa(data, tokenInfo);
            response.setSuccess(true);
            response.setMessage(ConstantesComunes.TEXTO_OPERACION_EXITOSA);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            log.warn(e.getMessage(), e);
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.warn("Error en Reinicio acta, save", e);
            response.setSuccess(false);
            response.setMessage("Error interno del sistema");
            return ResponseEntity.internalServerError().body(response);
        }
    }

}
