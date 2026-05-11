package pe.gob.onpe.sceorcbackend.rest.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import pe.gob.onpe.sceorcbackend.model.dto.JuradoElectoralEspecialDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.SearchFilterResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.JuradoElectoralService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@RestController
@RequestMapping("/juradoElectoral")
public class JuradoElectoralEspecialController {

  private final JuradoElectoralService juradoElectoralService;

  public JuradoElectoralEspecialController(JuradoElectoralService juradoElectoralService, TokenUtilService tokenUtilService) {
    this.juradoElectoralService = juradoElectoralService;
  }


  @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
  @GetMapping("")
  public ResponseEntity<GenericResponse<SearchFilterResponse<JuradoElectoralEspecialDto>>> list(
		 @RequestParam Integer size, 
		 @RequestParam Integer page,
         @RequestParam String filter,
         @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
	  
	GenericResponse<SearchFilterResponse<JuradoElectoralEspecialDto>> response = new GenericResponse<>();
	
    try {
    	if (page == null || page < 0 || size == null || size <= 0) {
            response.setSuccess(Boolean.FALSE);
            response.setMessage("Parámetros de paginación inválidos");
            return ResponseEntity.badRequest().body(response);
        }
    	SearchFilterResponse<JuradoElectoralEspecialDto> data =
                juradoElectoralService.listPaginted(filter, page, size);
    	response.setSuccess(Boolean.TRUE);
        response.setData(data);
        return ResponseEntity.ok(response);
    } catch (Exception e) {
    	response.setSuccess(Boolean.FALSE);
        response.setMessage("Error al obtener la lista: " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
  }

  @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
  @PostMapping("/save")
  public ResponseEntity<GenericResponse<Void>> saveDetalle(@RequestBody JuradoElectoralEspecialDto dto) {
      GenericResponse<Void> response = new GenericResponse<>();
      try {
          juradoElectoralService.save(dto);
          response.setSuccess(Boolean.TRUE);
          return new ResponseEntity<>(response, HttpStatus.OK);
      } catch (Exception e) {
          response.setSuccess(Boolean.FALSE);
          response.setMessage(e.getMessage());
          return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
      }
  } 
}
