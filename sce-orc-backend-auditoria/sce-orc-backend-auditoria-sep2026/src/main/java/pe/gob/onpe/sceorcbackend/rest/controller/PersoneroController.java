package pe.gob.onpe.sceorcbackend.rest.controller;

import java.util.Date;
import java.util.Objects;

import org.springframework.security.access.prepost.PreAuthorize;
import pe.gob.onpe.sceorcbackend.model.dto.PersoneroDTO;
import pe.gob.onpe.sceorcbackend.model.dto.PersoneroRequestDTO;
import pe.gob.onpe.sceorcbackend.model.dto.RegistroPersoneroDTO;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.padron.PadronDto;
import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionExecuteService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MesaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.PersoneroService;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

@RestController
@RequestMapping("/personero")
public class PersoneroController {

  Logger logger = LoggerFactory.getLogger(PersoneroController.class);

  private final TokenUtilService tokenUtilService;

  private final PersoneroService personeroService;

  private final MesaService mesaService;

  private final ActaTransmisionExecuteService actaTransmisionNacionStrategyService;

  private final ITabLogService logService;

  public PersoneroController(TokenUtilService tokenUtilService, PersoneroService personeroService, MesaService mesaService, ActaTransmisionExecuteService actaTransmisionNacionStrategyService, ITabLogService logService) {
    this.tokenUtilService = tokenUtilService;
    this.personeroService = personeroService;
    this.mesaService = mesaService;
      this.actaTransmisionNacionStrategyService = actaTransmisionNacionStrategyService;
      this.logService = logService;
  }

  @PreAuthorize(RoleAutority.VERIFICADOR)
  @GetMapping("/getRandomMesa")
  public ResponseEntity<GenericResponse<RegistroPersoneroDTO>> getRandomMesa(@RequestParam("idProceso") Long idProceso,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestParam("tipoFiltro") Integer tipoFiltro, @RequestParam("reprocesar") boolean reprocesar) {

    try {
      TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
      GenericResponse<RegistroPersoneroDTO> response =
          this.personeroService.getRandomMesa(tokenInfo.getNombreUsuario(), idProceso, tipoFiltro,  reprocesar);
      return ResponseEntity.status(HttpStatus.OK).body(response);

    } catch (Exception e) {
      logger.error(ConstantesComunes.MSJ_ERROR, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage(), null));
    }
  }

  @PreAuthorize(RoleAutority.VERIFICADOR)
  @GetMapping("/consulta-padron")
  public ResponseEntity<GenericResponse<PadronDto>> consultaPadron(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestParam("dni") String dni, @RequestParam("mesaId") Integer mesaId) {
    try {
      GenericResponse<PadronDto> response = personeroService.consultaPadronPorDni(dni, new TokenInfo(), mesaId);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (Exception e) {
      logger.error(ConstantesComunes.MSJ_ERROR, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage(), null));
    }
  }

  @PreAuthorize(RoleAutority.VERIFICADOR)
  @PostMapping("")
  public ResponseEntity<GenericResponse> guardarPersoneros(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestBody PersoneroRequestDTO requestDTO) {

    try {
      TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
      GenericResponse response = new GenericResponse();
      if (requestDTO.getPersoneros().isEmpty()) {
        PersoneroDTO personeroDTO = new PersoneroDTO();
        personeroDTO.setUsuarioCreacion(tokenInfo.getNombreUsuario());
        personeroDTO.setMesa(requestDTO.getMesa());
        this.personeroService.save(personeroDTO, requestDTO, tokenInfo);
      }
      requestDTO.getPersoneros().forEach(personeroDTO -> {
        if(Objects.isNull(personeroDTO.getId())){
          personeroDTO.setActivo(ConstantesComunes.ACTIVO);
          personeroDTO.setFechaCreacion(new Date());
        }
        personeroDTO.setUsuarioCreacion(tokenInfo.getNombreUsuario());
        this.personeroService.save(personeroDTO, requestDTO, tokenInfo);
      });
      try {
        this.actaTransmisionNacionStrategyService.sincronizar(requestDTO.getActaId(), requestDTO.getAcronimoProceso(),
                TransmisionNacionEnum.REGISTRO_PERSONEROS, tokenInfo.getNombreUsuario());

        this.logService.registrarLog(
                tokenInfo.getNombreUsuario(),
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                "La sincronización de los  personeros de la mesa " + requestDTO.getMesa().getMesa() + " se realizó con éxito",
                tokenInfo.getCodigoCentroComputo(),
                ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

      } catch (Exception ex) {
        logger.warn("Problemas al realizar la sincronización a Nación");
      }
      response.setSuccess(Boolean.TRUE);
      response.setMessage("Personeros registrados con éxito");
      return ResponseEntity.status(HttpStatus.OK).body(response);

    } catch (Exception e) {
      logger.error("Error: ", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage(), null));
    }
  }

  @PreAuthorize(RoleAutority.VERIFICADOR)
  @PutMapping("/saltar/{idMesa}")
  public ResponseEntity<GenericResponse> saltar(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @PathVariable("idMesa") Long idMesa) {

    try {
      GenericResponse response = new GenericResponse();
      this.mesaService.actualizarEstadoDigitalizaionPRisEdit(idMesa, "P");
      response.setSuccess(Boolean.TRUE);
      response.setMessage("Liberación con éxito");
      return ResponseEntity.status(HttpStatus.OK).body(response);

    } catch (Exception e) {
      logger.error("Error: ", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage(), null));
    }
  }

}
