package pe.gob.onpe.scebackend.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.ExtranjeroService;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.SERVICE_EXTERNO)
@RequestMapping("/extranjero")
@Controller
public class ExtranjeroController {

    Logger logger = LoggerFactory.getLogger(ExtranjeroController.class);

    private static final String HEADER_CC = "codigocc";

    private final ExtranjeroService extranjeroService;

    public ExtranjeroController(ExtranjeroService extranjeroService) {
        this.extranjeroService = extranjeroService;
    }

    @PostMapping(value = "/uploadActaDigitization", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<GenericResponse> recibirActa(@RequestHeader(HEADER_CC) String centroComputo,
            @RequestPart(value = "filePdf", required = true) MultipartFile filePdf,
            @RequestPart(value = "fileTif", required = true) MultipartFile fileTif, @RequestParam String numeroActa,
            @RequestParam String copia, @RequestParam String digitoChequeo, @RequestParam String usuario) {

        GenericResponse genericResponse = new GenericResponse();
        try {
            extranjeroService.enviarActaOrc(filePdf, fileTif, numeroActa, copia, digitoChequeo, usuario, centroComputo);
            genericResponse.setSuccess(Boolean.TRUE);
            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
        } catch (Exception e) {
            logger.error("Error al procesar la solicitud en recibirActa", e);
            genericResponse.setSuccess(Boolean.FALSE);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
        }
    }

}
