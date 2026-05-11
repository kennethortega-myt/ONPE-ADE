package pe.gob.onpe.scebackend.rest.controller;

import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.scebackend.model.dto.FiltroPuestaCeroDTO;
import pe.gob.onpe.scebackend.model.dto.PuestaCeroResponseDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.service.IPuestaCeroService;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.RoleAutority;
import pe.gob.onpe.scebackend.utils.SceConstantes;

@RestController
@RequestMapping("puesta-cero-nacion")
public class PuestaCeroController extends BaseController {

  Logger logger = LoggerFactory.getLogger(PuestaCeroController.class);

  private final IPuestaCeroService puestaCeroService;


    public PuestaCeroController(IPuestaCeroService puestaCeroService, 
    		TokenDecoder tokenDecoder) {
       super(tokenDecoder);
        this.puestaCeroService = puestaCeroService;
    }

  @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
    @PostMapping("/")
  public ResponseEntity<GenericResponse> puestaCero(
      @RequestBody FiltroPuestaCeroDTO filtro
  ) {
    GenericResponse genericResponse = new GenericResponse();
    PuestaCeroResponseDto resultado = null;
    try {
      logger.info("se ejecuto puestaCero()");
      this.puestaCeroService.activarBloqueo(filtro.getUsuario());
      resultado = this.puestaCeroService.puestaCero(filtro.getEsquema(), 0, filtro.getUsuario(), null, filtro.getAcronimo());
      genericResponse.setSuccess(resultado.isSuccess());
      genericResponse.setMessage(resultado.getMessage());
      genericResponse.setData(resultado);
      return new ResponseEntity<>(genericResponse, HttpStatus.OK);
    } catch (Exception e) {
      genericResponse.setSuccess(Boolean.FALSE);
      genericResponse.setData(resultado);
      genericResponse.setMessage(e.getMessage());
      return new ResponseEntity<>(genericResponse, HttpStatus.BAD_REQUEST);
    }
  }

  @PreAuthorize(RoleAutority.ADMINISTRADOR_NAC)
  @PostMapping("/reporte")
  public ResponseEntity<GenericResponse> getReportePuestaCero(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestHeader("X-Tenant-Id") String tentat,
      @RequestBody FiltroPuestaCeroDTO filtro
  ) {
    GenericResponse genericResponse = new GenericResponse();
    String ccc;
    String ncc;
    if (authorization != null) {
      String token = authorization.substring(SceConstantes.LENGTH_BEARER);
      Claims claims = this.tokenDecoder.decodeToken(token);
      ccc = claims.get("ccc", String.class);
      ncc = claims.get("ncc", String.class);
    } else {
      genericResponse.setMessage("Token Inválido");
      genericResponse.setSuccess(Boolean.FALSE);
      return ResponseEntity.status(genericResponse.isSuccess() ? HttpStatus.OK : HttpStatus.FORBIDDEN).body(genericResponse);
    }
    LoginUserHeader user = getUserLogin(authorization);
    byte[] reporte =
        this.puestaCeroService.reportePuestaCero(filtro, ccc, ncc, user.getUsuario());

    return getPdfResponse(reporte);
  }

}
