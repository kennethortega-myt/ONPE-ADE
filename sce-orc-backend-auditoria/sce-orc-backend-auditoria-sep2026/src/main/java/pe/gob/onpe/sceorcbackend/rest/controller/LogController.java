package pe.gob.onpe.sceorcbackend.rest.controller;

import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@CrossOrigin
@RequestMapping("/log")
public class LogController {

  @Autowired
  private ITabLogService logService;

  @GetMapping()
  public ResponseEntity<?> listPaginated(@RequestParam(name = "size") Integer size, @RequestParam(name = "page") Integer page,
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
