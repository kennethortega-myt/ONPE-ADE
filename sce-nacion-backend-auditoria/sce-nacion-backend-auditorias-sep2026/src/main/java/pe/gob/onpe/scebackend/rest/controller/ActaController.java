package pe.gob.onpe.scebackend.rest.controller;

import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.CabActaDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.impl.ActaService;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
@RestController
@Validated
@RequestMapping("/acta")
public class ActaController {

	Logger logger = LoggerFactory.getLogger(ActaController.class);
	private final ActaService actaService;

    public ActaController(ActaService actaService) {
        this.actaService = actaService;
    }

    @PatchMapping("/")
    public ResponseEntity<GenericResponse> save(@RequestBody CabActaDto acta) throws FileNotFoundException {
        GenericResponse genericResponse = new GenericResponse();
        CabActaDto actaDto = this.actaService.save(acta);
        genericResponse.setSuccess(Boolean.TRUE);
        genericResponse.setData(actaDto);
        return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    }

	@GetMapping("/trazabilidad/{mesa-copia-dig}")
	public ResponseEntity<GenericResponse> trazabilidadActa(@PathVariable("mesa-copia-dig") String mesaCopiaDigito) {
		GenericResponse genericResponse= this.actaService.trazabilidadActa(mesaCopiaDigito);
		return ResponseEntity.status(HttpStatus.OK).body(genericResponse);

	}


	
	@GetMapping(value = "/recibir-stae",produces = "application/json")
	public ResponseEntity<String> recibirStae(@RequestParam("idActa") Long idActa) {
		logger.info("recibir-stae : {}",idActa);
		try {
			String transmisionRecepcionDto = this.actaService.transmisionRecepcionDto(idActa);
			return new ResponseEntity<>(transmisionRecepcionDto, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>("ko", HttpStatus.UNPROCESSABLE_ENTITY);
		}
	}
}
