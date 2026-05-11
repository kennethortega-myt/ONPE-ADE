package pe.gob.onpe.sceorcbackend.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.onpe.sceorcbackend.model.dto.ParametroConexionDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;


@RestController
@RequestMapping("/parametro-conexion-cc")
public class ParametroConexionCcController {

	@PostMapping("/ping")
	public ResponseEntity<GenericResponse<String>> ping(@RequestBody ParametroConexionDto dto) {
		GenericResponse<String> response = new GenericResponse<>();
		try {
			response.setSuccess(Boolean.TRUE);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			response.setSuccess(Boolean.FALSE);
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
}
