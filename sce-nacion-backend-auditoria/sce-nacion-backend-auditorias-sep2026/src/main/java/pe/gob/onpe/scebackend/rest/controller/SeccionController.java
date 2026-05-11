package pe.gob.onpe.scebackend.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.request.DatosGeneralesRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.ISeccionService;
import pe.gob.onpe.scebackend.utils.RoleAutority;
import pe.gob.onpe.scebackend.utils.SceConstantes;

import java.util.HashMap;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@Validated
@RequestMapping("/seccion")
public class SeccionController {
	

	private final ISeccionService seccionService;

    public SeccionController(ISeccionService seccionService) {
        this.seccionService = seccionService;
    }

    @PostMapping()
	public ResponseEntity<GenericResponse> save(@RequestBody DatosGeneralesRequestDto paramInputDto) throws GenericException {
		GenericResponse response = new GenericResponse();
		try{
			response.setData(seccionService.save2(paramInputDto));
			response.setSuccess(Boolean.TRUE);
			response.setMessage("Se registró correctamente");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}catch (Exception e){
			response.setSuccess(Boolean.FALSE);
			response.setMessage(e.getMessage());
			return new ResponseEntity<>(response, HttpStatus.CONFLICT);
		}
	}

	@GetMapping()
	public ResponseEntity<GenericResponse> listSecciones() {
		GenericResponse genericResponse = new GenericResponse();
		genericResponse.setSuccess(Boolean.TRUE);
		genericResponse.setData(this.seccionService.listAll());
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
	}

	@PutMapping()
	public ResponseEntity<HashMap<String, Object>> update(@RequestBody DatosGeneralesRequestDto paramInputDto) {
		// Implement

		seccionService.save(paramInputDto);

		HashMap<String, Object> resultado = new HashMap<>();
		resultado.put("resultado", 1);
		resultado.put("mensaje", "Se actualizo correctamente");

		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<HashMap<String, Object>> delete(@PathVariable(name = "id") Integer id) {
		// Implement

		seccionService.updateStatus(SceConstantes.INACTIVO, id);

		HashMap<String, Object> resultado = new HashMap<>();
		resultado.put("resultado", 1);
		resultado.put("mensaje", "Se elimino correctamente");

		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}

}
