package pe.gob.onpe.sceorcbackend.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.queue.PredictionActaRequest;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionExecuteService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CabActaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.VerificationService;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.verification.VerificationActaDTO;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoActa;

import java.util.Optional;

@RestController
@RequestMapping("/prediction")
public class PredictionController {

    private static final Logger logger = LoggerFactory.getLogger(PredictionController.class);

    private final VerificationService verificationService;
    private final CabActaService cabActaService;
    private final ActaTransmisionExecuteService actaTransmisionExecuteService;

    public PredictionController(VerificationService verificationService,
                                CabActaService cabActaService,
                                ActaTransmisionExecuteService actaTransmisionExecuteService) {
        this.verificationService = verificationService;
        this.cabActaService = cabActaService;
        this.actaTransmisionExecuteService = actaTransmisionExecuteService;
    }

    @PostMapping("/procesar-resultado")
    public ResponseEntity<GenericResponse<Boolean>> procesarResultado(
            @RequestBody PredictionActaRequest request) {
        try {
            logger.info("[PREDICTION] Recibido resultado modelo actaId={}, usuario={}", request.getActaId(), request.getNombreUsuario());
            if (logger.isDebugEnabled()) {
                try {
                    logger.debug("[PREDICTION] Body recibido: {}", new ObjectMapper().writeValueAsString(request));
                } catch (Exception ex) {
                    logger.debug("[PREDICTION] Body recibido: {}", request);
                }
            }

            VerificationActaDTO body = request.getBody();

            TokenInfo tokenInfo = new TokenInfo();
            tokenInfo.setNombreUsuario(request.getNombreUsuario());
            tokenInfo.setCodigoCentroComputo(request.getCodigoCentroComputo());
            tokenInfo.setAbrevProceso(request.getAbrevProceso());

            GenericResponse<Boolean> respuesta = this.verificationService.guardar(body, tokenInfo);

            logger.info("[PREDICTION] Resultado guardar actaId={}, success={}", request.getActaId(), respuesta.isSuccess());

            // Sincronizar despues de que el modelo guardo exitosamente
            if (respuesta.isSuccess() && !respuesta.getActasId().isEmpty()) {
                Long actaId = respuesta.getActasId().getFirst();
                Optional<Acta> optActa = this.cabActaService.findById(actaId);
                optActa.ifPresent(acta -> sincronizarActa(acta, request.getNombreUsuario(), request.getAbrevProceso()));
            }

            return ResponseEntity.status(respuesta.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(respuesta);
        } catch (Exception e) {
            logger.error("[PREDICTION] Error procesando resultado actaId={}: {}", request.getActaId(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(false, "Error: " + e.getMessage(), false));
        }
    }

    private void sincronizarActa(Acta acta, String usuario, String proceso) {
        try {
            switch (acta.getEstadoActa()) {
                case ConstantesEstadoActa.ESTADO_ACTA_SEGUNDA_VERIFICACION ->
                    actaTransmisionExecuteService.sincronizar(acta.getId(), proceso, TransmisionNacionEnum.SEGUNDA_VERI_TRANSMISION, usuario);
                case ConstantesEstadoActa.ESTADO_ACTA_PROCESADA ->
                    actaTransmisionExecuteService.sincronizar(acta.getId(), proceso, TransmisionNacionEnum.PROC_NORMAL_VERI_TRANSMISION, usuario);
                case ConstantesEstadoActa.ESTADO_ACTA_PARA_ENVIO_AL_JURADO ->
                    actaTransmisionExecuteService.sincronizar(acta.getId(), proceso, TransmisionNacionEnum.PROC_OBS_VERI_TRANSMISION, usuario);
                case ConstantesEstadoActa.ESTADO_ACTA_DIGITACIONES_POR_VERIFICAR ->
                    actaTransmisionExecuteService.sincronizar(acta.getId(), proceso, TransmisionNacionEnum.PROC_POR_CORREGIR_VERI_TRANSMISION, usuario);
                default ->
                    actaTransmisionExecuteService.sincronizar(acta.getId(), proceso, TransmisionNacionEnum.VERIFICACION_TRANSMISION, usuario);
            }
        } catch (Exception e) {
            logger.error("[PREDICTION] Error sincronizando actaId={}: {}", acta.getId(), e.getMessage(), e);
        }
    }
}
