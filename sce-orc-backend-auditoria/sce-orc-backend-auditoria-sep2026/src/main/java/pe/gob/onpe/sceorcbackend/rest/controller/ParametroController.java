package pe.gob.onpe.sceorcbackend.rest.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.sceorcbackend.model.dto.DetParametroDto;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.DetParametroService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ParametroService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@RestController
@RequestMapping("/parametro")
public class ParametroController {

  private final ParametroService parametroService;

  private final DetParametroService detParametroService;

  private final TokenUtilService tokenUtilService;

  public ParametroController(ParametroService parametroService, DetParametroService detParametroService, TokenUtilService tokenUtilService) {
    this.parametroService = parametroService;
    this.detParametroService = detParametroService;
    this.tokenUtilService = tokenUtilService;
  }

  @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
  @GetMapping("/find")
  public ResponseEntity<?> getParametro(@RequestParam(name = "parametro", required = false) String parametro) {
    GenericResponse response = new GenericResponse();
    try {
      response.setSuccess(Boolean.TRUE);
      response.setData(this.parametroService.obtenerParametro(parametro));
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      response.setSuccess(Boolean.FALSE);
      response.setMessage(e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
  @GetMapping("")
  public ResponseEntity<?> list(@RequestParam(name = "size") Integer size, @RequestParam(name = "page") Integer page,
      @RequestParam(name = "filter") String filter,
                                @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization
                                ) {
    GenericResponse response = new GenericResponse();
    try {
      TokenInfo tokenInfo = tokenUtilService.getInfo(authorization);
      response.setSuccess(Boolean.TRUE);
      response.setData(this.parametroService.listPaginted(tokenInfo,filter, page, size));
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      response.setSuccess(Boolean.FALSE);
      response.setMessage(e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
  @GetMapping("/detalle")
  public ResponseEntity<?> list(@RequestParam(name = "idParametro") Long idParametro) {
    GenericResponse response = new GenericResponse();
    try {
      response.setSuccess(Boolean.TRUE);
      response.setData(this.detParametroService.listDetalleByParametro(idParametro));
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      response.setSuccess(Boolean.FALSE);
      response.setMessage(e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
  @PostMapping("/detalle")
  public ResponseEntity<?> saveDetalle(@RequestBody DetParametroDto detParametroDto) {
    GenericResponse response = new GenericResponse();
    try {
      response.setSuccess(Boolean.TRUE);
      this.detParametroService.save(detParametroDto);
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      response.setSuccess(Boolean.FALSE);
      response.setMessage(e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
    @PutMapping("/estado")
    public ResponseEntity<?> actualizarEstado(@RequestBody DetParametroDto detParametroDto) {
        GenericResponse response = new GenericResponse();
        try {
            response.setSuccess(Boolean.TRUE);
            this.detParametroService.actualizarEstado(detParametroDto.getActivo(), detParametroDto.getUsuarioCreacion(),
                    detParametroDto.getId());
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            response.setSuccess(Boolean.FALSE);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
