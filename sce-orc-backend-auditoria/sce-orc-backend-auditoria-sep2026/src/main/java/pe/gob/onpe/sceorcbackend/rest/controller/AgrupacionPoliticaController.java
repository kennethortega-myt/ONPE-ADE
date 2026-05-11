package pe.gob.onpe.sceorcbackend.rest.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import pe.gob.onpe.sceorcbackend.model.dto.ComboResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.AgrupacionPolitica;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AgrupacionPoliticaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.util.List;

@RestController
@RequestMapping("agrupacion-politica")
public class AgrupacionPoliticaController {

  Logger logger = LoggerFactory.getLogger(AgrupacionPoliticaController.class);


  private final AgrupacionPoliticaService agrupacionPoliticaService;

  public AgrupacionPoliticaController(AgrupacionPoliticaService agrupacionPoliticaService) {
    this.agrupacionPoliticaService = agrupacionPoliticaService;
  }

  @PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
  @GetMapping("")
  public ResponseEntity<GenericResponse<List<AgrupacionPolitica>>> list(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
    try {
      GenericResponse<List<AgrupacionPolitica>> response = new GenericResponse<>();
      response.setData(this.agrupacionPoliticaService.findAll());
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (Exception e) {
      logger.error("Error: ",e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage(), null));
    }

  }

  @PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
  @GetMapping("/combo")
  public ResponseEntity<GenericResponse<List<ComboResponse>>> listCombo(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
    try {
      GenericResponse<List<ComboResponse>> response = new GenericResponse<>();
      response.setData(this.agrupacionPoliticaService.listCombo());
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (Exception e) {
      logger.error("Error: ",e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage(), null));
    }

  }
}
