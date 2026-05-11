package pe.gob.onpe.scebackend.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.ConsultaMiembroMesaService;
import pe.gob.onpe.scebackend.security.service.TabRefreshTokenService;
import pe.gob.onpe.scebackend.security.service.TokenUtilService;
import pe.gob.onpe.scebackend.utils.RoleAutority;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

@RestController
@Validated
@CrossOrigin
@RequestMapping("/consulta")
public class ConsultaMiembroMesaController {

    Logger logger = LoggerFactory.getLogger(ConsultaMiembroMesaController.class);

    private final ConsultaMiembroMesaService consultaMiembroMesaService;

    public ConsultaMiembroMesaController(
            ConsultaMiembroMesaService consultaMiembroMesaService) {
        this.consultaMiembroMesaService = consultaMiembroMesaService;
    }

    @GetMapping("/miembro-mesa")
    public ResponseEntity<?> consultaMiembroMesa(
            @RequestHeader(value = ConstantesComunes.API_KEY_NACION, required = false) String apiKeyNacion,
            @RequestParam(value = "numeroMesa", required = false) Integer numeroMesa,
            @RequestParam(value = "dni", required = false) String dni) {

        GenericResponse genericResponse = this.consultaMiembroMesaService.consultarMiembrosMesa(numeroMesa, dni);

        return new ResponseEntity<>(genericResponse, genericResponse.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST);

    }

    @GetMapping("/ubigeo-proceso")
    public ResponseEntity<?> consultaUbigeoProceso(
        @RequestHeader(value = ConstantesComunes.API_KEY_NACION, required = false) String apiKeyNacion,
        @RequestHeader(value = "X-Tenant-Id") String acronimo) {

        GenericResponse genericResponse = this.consultaMiembroMesaService.getUbigeoByAcronimo(acronimo);
        return new ResponseEntity<>(genericResponse, genericResponse.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }

}
