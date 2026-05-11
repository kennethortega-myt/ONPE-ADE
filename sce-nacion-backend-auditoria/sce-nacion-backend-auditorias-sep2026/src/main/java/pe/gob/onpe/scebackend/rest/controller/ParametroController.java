package pe.gob.onpe.scebackend.rest.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.DetParametroDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.IDetParametroService;
import pe.gob.onpe.scebackend.model.service.IParametroService;
import pe.gob.onpe.scebackend.utils.RoleAutority;

@PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
@RestController
@RequestMapping("/parametro")
public class ParametroController {

  private final IParametroService parametroService;

  private final IDetParametroService detParametroService;

  public ParametroController(IParametroService parametroService, IDetParametroService detParametroService) {
    this.parametroService = parametroService;
    this.detParametroService = detParametroService;
  }

  @GetMapping("/find")
  public ResponseEntity<GenericResponse> getParametro(@RequestParam(name = "parametro", required = false) String parametro) {
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

  @GetMapping("")
  public ResponseEntity<GenericResponse> list(@RequestParam(name = "size") Integer size, @RequestParam(name = "page") Integer page,
      @RequestParam(name = "filter") String filter) {
    GenericResponse response = new GenericResponse();
    try {
      response.setSuccess(Boolean.TRUE);
      response.setData(this.parametroService.listPaginted(filter, page, size));
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (Exception e) {
      response.setSuccess(Boolean.FALSE);
      response.setMessage(e.getMessage());
      return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/detalle")
  public ResponseEntity<GenericResponse> list(@RequestParam(name = "idParametro") Long idParametro) {
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

  @PostMapping("/detalle")
  public ResponseEntity<GenericResponse> saveDetalle(@RequestBody DetParametroDto detParametroDto) {
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
