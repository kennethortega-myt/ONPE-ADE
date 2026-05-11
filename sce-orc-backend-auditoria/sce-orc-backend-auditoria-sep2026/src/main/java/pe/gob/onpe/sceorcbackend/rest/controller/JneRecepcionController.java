package pe.gob.onpe.sceorcbackend.rest.controller;

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

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.sceorcbackend.model.dto.response.RecepcionResponseDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.JneRecepcionService;
import pe.gob.onpe.sceorcbackend.model.stae.service.StaeService;

@RequestMapping("/jneRecepcion")
@RequiredArgsConstructor
@Controller
public class JneRecepcionController {

    private final JneRecepcionService service;
    private final StaeService staeService;

    private static final String HEADER_CC = "codigocc";

    @PostMapping(value = "/resolucionOficio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<RecepcionResponseDto> recibirResolucion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestHeader(HEADER_CC) String centroComputo,
            @RequestPart(value = "filePdf", required = false) MultipartFile filePdf, @RequestPart("json") String json,
            @RequestPart("codigoEnvio") String codigoEnvio) {

        if (!staeService.validarTokenStae(authorization, centroComputo)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new RecepcionResponseDto(false, "Token inválido"));
        }

        try {
            service.procesarRecepcion(filePdf, json, codigoEnvio);
            return ResponseEntity.ok(new RecepcionResponseDto(true, "Recepción registrada correctamente"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new RecepcionResponseDto(false, e.getMessage()));
        }
    }
}
