package pe.gob.onpe.scebackend.rest.controller;

import java.util.HashMap;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.request.DocumentoElectoralRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.ICatalogoService;
import pe.gob.onpe.scebackend.model.service.IDocumentoElectoralService;
import pe.gob.onpe.scebackend.utils.RoleAutority;
import pe.gob.onpe.scebackend.utils.SceConstantes;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@Validated
@RequestMapping("/documento")
public class DocumentoElectoralController {

	private final IDocumentoElectoralService documentoElectoralService;

	private final ICatalogoService catalogoService;

    public DocumentoElectoralController(IDocumentoElectoralService documentoElectoralService, ICatalogoService catalogoService) {
        this.documentoElectoralService = documentoElectoralService;
        this.catalogoService = catalogoService;
    }

    @PostMapping()
	public ResponseEntity<GenericResponse> save(@Valid @RequestBody DocumentoElectoralRequestDto paramInputDto, @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization)  {
		// Implement
		GenericResponse response = new GenericResponse();
		try{
			response.setData(documentoElectoralService.save(paramInputDto));
			response.setSuccess(Boolean.TRUE);
			response.setMessage("Se registró correctamente");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}catch (GenericException e){
			response.setSuccess(Boolean.FALSE);
			response.setMessage(e.getMensaje());
			return new ResponseEntity<>(response, HttpStatus.CONFLICT);
		}
	}

	@GetMapping()
	public ResponseEntity<GenericResponse> listarDocumentoElectoral()  {
		GenericResponse genericResponse = new GenericResponse();
		genericResponse.setSuccess(Boolean.TRUE);
		genericResponse.setData(this.documentoElectoralService.listAll());
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
	}

	@PutMapping()
	public ResponseEntity<HashMap<String, Object>> update(@RequestBody DocumentoElectoralRequestDto paramInputDto,
														  @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) throws GenericException {
		// Implement

		documentoElectoralService.save(paramInputDto);

		HashMap<String, Object> resultado = new HashMap<>();
		resultado.put("resultado", 1);
		resultado.put("mensaje", "Se actualizo correctamente");

		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<HashMap<String, Object>> delete(@PathVariable(name = "id") Integer id,@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization)  {
		// Implement

		documentoElectoralService.updateStatus(SceConstantes.INACTIVO, id);

		HashMap<String, Object> resultado = new HashMap<>();
		resultado.put("resultado", 1);
		resultado.put("mensaje", "Se elimino correctamente");

		return new ResponseEntity<>(resultado, HttpStatus.OK);
	}

	@GetMapping("/catalogos")
	public ResponseEntity<GenericResponse> listCatalogos(){
		GenericResponse genericResponse = new GenericResponse();
		genericResponse.setSuccess(Boolean.TRUE);
		genericResponse.setData(this.catalogoService.listaCalogos("tab_documento_electoral"));

		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
	}

	@GetMapping("/configGeneral")
	public ResponseEntity<GenericResponse> listarDocumentoElectoralConfigGeneral()  {
		GenericResponse genericResponse = new GenericResponse();
		genericResponse.setSuccess(Boolean.TRUE);
		genericResponse.setData(this.documentoElectoralService.listAllConfiguracionGeneral());
		return new ResponseEntity<>(genericResponse, HttpStatus.OK);
	}

	@PostMapping("/configGeneral")
	public ResponseEntity<GenericResponse> saveConfigGeneral(@RequestBody List<DocumentoElectoralRequestDto> lista,@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization)  {
		// Implement
		GenericResponse response = new GenericResponse();
		this.documentoElectoralService.saveConfigGeneral(lista);
		response.setSuccess(Boolean.TRUE);
		response.setMessage("Se registró correctamente");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
