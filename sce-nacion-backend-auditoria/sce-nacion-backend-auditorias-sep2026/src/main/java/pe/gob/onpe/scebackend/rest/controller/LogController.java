package pe.gob.onpe.scebackend.rest.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ROLES_SCE_WEB)
@RestController
@Validated
@RequestMapping("/log")
public class LogController {

  private final ITabLogTransaccionalService logService;

    public LogController(ITabLogTransaccionalService logService) {
        this.logService = logService;
    }

    @GetMapping()
  public ResponseEntity<GenericResponse> listPaginated(@RequestParam(name = "size") Integer size, @RequestParam(name = "page") Integer page,
      @RequestParam(name = "error") String error){
    GenericResponse response = new GenericResponse();
    try {
      response.setSuccess(Boolean.TRUE);
      response.setData(this.logService.listPaginted(error, page, size));
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      response.setSuccess(Boolean.FALSE);
      response.setMessage(e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

}
