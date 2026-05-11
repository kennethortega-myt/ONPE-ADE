package pe.gob.onpe.sceorcbackend.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.queue.NewActa;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ProcesoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MaeProcesoElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.digitalizacion.DigtalDTO;
import pe.gob.onpe.sceorcbackend.model.queue.RabbitMqSender;
import pe.gob.onpe.sceorcbackend.model.service.DigitalizacionService;
import pe.gob.onpe.sceorcbackend.model.stae.service.StaeService;



@RequestMapping("/extranjero")
@Controller
public class ExtranjeroController {

    Logger logger = LoggerFactory.getLogger(ExtranjeroController.class);

    private final DigitalizacionService digitalizacionService;
    private final RabbitMqSender rabbitMqSender;
    private final StaeService staeService;
    private final MaeProcesoElectoralService maeProcesoElectoralService;

    private static final String HEADER_CC   = "codigocc";
    private static final String HEADER_USER = "user";

    public ExtranjeroController(
            DigitalizacionService digitalizacionService,
            RabbitMqSender rabbitMqSender,
            StaeService staeService,
            MaeProcesoElectoralService maeProcesoElectoralService) {
        this.digitalizacionService = digitalizacionService;
        this.rabbitMqSender = rabbitMqSender;
        this.staeService = staeService;
        this.maeProcesoElectoralService= maeProcesoElectoralService;
    }

    @PostMapping(value = "/uploadActaDigitization", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse<Void>> uploadActaExtranjero(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestHeader(HEADER_CC) String centroComputo,
            @RequestHeader(HEADER_USER) String usuario,
            @RequestPart(value ="filePdf" , required = true) MultipartFile filePdf,
            @RequestPart(value ="fileTif", required = true) MultipartFile fileTif,
            @RequestPart(value ="numeroActa") String numeroActa,
            @RequestPart(value ="copia") String copia,
            @RequestPart(value ="digitoChequeo") String digitoChequeo) {

        GenericResponse<Void> genericResponse = new GenericResponse<>();
        boolean autenticacion = this.staeService.validarTokenStae(authorization, centroComputo);
        if(autenticacion) {
            try {
                ProcesoElectoral procesoElectoral = this.maeProcesoElectoralService.findByActivo();
                String actaCompleta = numeroActa + copia + digitoChequeo;

                TokenInfo tokenInfo = new TokenInfo();
                tokenInfo.setNombreUsuario(usuario);
                tokenInfo.setCodigoCentroComputo(centroComputo);
                tokenInfo.setAbrevProceso(procesoElectoral.getAcronimo());

                DigtalDTO digtalDTO = digitalizacionService.digitalizarActaExtranjero(
                        fileTif, filePdf, actaCompleta, tokenInfo);

                if (digtalDTO != null) {
                  enviarNewActa(NewActa.from(
                          digtalDTO.getIdActa(),
                          digtalDTO.getIdArchivo(),
                          digtalDTO.getTipoArchivo(),
                          tokenInfo
                  ));

                  enviarNewActa(NewActa.from(
                          digtalDTO.getIdActa(),
                          digtalDTO.getIdArchivoAis(),
                          digtalDTO.getTipoArchivoAis(),
                          tokenInfo
                  ));
                }
                genericResponse.setSuccess(Boolean.TRUE);
                return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
            }catch (Exception e) {
                genericResponse.setSuccess(Boolean.FALSE);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
            }
        }else {
            genericResponse.setMessage("Token Inválido");
            genericResponse.setSuccess(Boolean.FALSE);
            return ResponseEntity.status(genericResponse.isSuccess() ? HttpStatus.OK : HttpStatus.FORBIDDEN)
                    .body(genericResponse);
        }

    }

    private void enviarNewActa(NewActa queueMessage) {
        if (queueMessage.getFileId() == null || queueMessage.getType() == null) {
          logger.warn("Archivo o tipoArchivo nulo para actaId={}, no se enviará a la cola",
                  queueMessage.getActaId());
          return;
        }

        logger.info("Enviando a la cola validación {}", queueMessage);
        rabbitMqSender.sendNewActa(queueMessage);
      }
}
