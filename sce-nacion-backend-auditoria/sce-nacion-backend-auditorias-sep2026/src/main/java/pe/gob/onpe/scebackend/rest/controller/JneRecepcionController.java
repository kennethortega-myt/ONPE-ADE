package pe.gob.onpe.scebackend.rest.controller;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.scebackend.model.service.JneRecepcionService;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.SERVICE_JNE)
@RequestMapping("/jneRecepcion")
@RequiredArgsConstructor
@Controller
public class JneRecepcionController {

    private final JneRecepcionService service;

    @PostMapping(value = "/resolucionOficio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> recibirResolucion(
            @RequestPart(value = "filePdf", required = false) MultipartFile filePdf, @RequestPart("json") String json,
            @RequestPart("codigoEnvio") String codigoEnvio) {

        service.procesarRecepcion(filePdf, json, codigoEnvio);

        return ResponseEntity.ok(Map.of("mensaje", "Recepción registrada correctamente"));
    }

    @PostMapping(value = "/reenviarPendientesJNE")
    public ResponseEntity<Map<String, Object>> reenviarPendientesJNE() {

        service.reenviarPendientesJNE();
        return ResponseEntity.ok(Map.of("mensaje", "Reenvio ejecutado correctamente"));
    }
}
