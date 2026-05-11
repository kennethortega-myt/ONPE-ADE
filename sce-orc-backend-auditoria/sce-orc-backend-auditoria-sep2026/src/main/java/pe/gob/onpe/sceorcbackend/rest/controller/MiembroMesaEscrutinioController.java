package pe.gob.onpe.sceorcbackend.rest.controller;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import pe.gob.onpe.sceorcbackend.model.dto.MiembroMesaEscrutinioDTO;
import pe.gob.onpe.sceorcbackend.model.dto.RegistroMiembroMesaEscrutinioDTO;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.padron.PadronDto;
import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.*;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;

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

@Slf4j
@RestController
@RequestMapping("/mme")
public class MiembroMesaEscrutinioController {

  public static final String ERROR = "Error: ";
  private final TokenUtilService tokenUtilService;

  private final MiembroMesaEscrutinioService miembroMesaEscrutinioService;

  private final MesaService mesaService;

  private final ActaTransmisionExecuteService actaTransmisionNacionStrategyService;

  private final ITabLogService logService;

  public MiembroMesaEscrutinioController(TokenUtilService tokenUtilService, MiembroMesaEscrutinioService miembroMesaEscrutinioService,
                                         MesaService mesaService, ActaTransmisionExecuteService actaTransmisionNacionStrategyService, ITabLogService logService) {
    this.tokenUtilService = tokenUtilService;
    this.miembroMesaEscrutinioService = miembroMesaEscrutinioService;
    this.mesaService = mesaService;
      this.actaTransmisionNacionStrategyService = actaTransmisionNacionStrategyService;
      this.logService = logService;
  }

  @PreAuthorize(RoleAutority.VERIFICADOR)
  @GetMapping("/getRandomMesa")
  public ResponseEntity<GenericResponse<RegistroMiembroMesaEscrutinioDTO>> getRandomMesa(@RequestParam("idProceso") Long idProceso,
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestParam("tipoFiltro") Integer tipoFiltro, @RequestParam("reprocesar") boolean reprocesar) {

    try {
      TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
      GenericResponse<RegistroMiembroMesaEscrutinioDTO> response = this.miembroMesaEscrutinioService.getRandomMesa(tokenInfo.getNombreUsuario(), idProceso, tipoFiltro, reprocesar);
      return ResponseEntity.status(HttpStatus.OK).body(response);

    } catch (Exception e) {
      log.error(ERROR, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage(), null));
    }
  }

  @PreAuthorize(RoleAutority.VERIFICADOR)
  @GetMapping("/consulta-padron")
  public ResponseEntity<GenericResponse<PadronDto>> consultaPadron(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
      @RequestParam("dni") String dni, @RequestParam("mesaId") Integer mesaId, @RequestParam("primeraConsultaR") boolean primeraConsultaR) {
    try {
      GenericResponse<PadronDto> response = miembroMesaEscrutinioService.consultaPadronPorDni(dni, new TokenInfo(), mesaId, primeraConsultaR);
      return ResponseEntity.status(HttpStatus.OK).body(response);
    } catch (Exception e) {
      log.error(ERROR, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage(), null));
    }
  }

  @PreAuthorize(RoleAutority.VERIFICADOR)
  @PostMapping("")
  public ResponseEntity<GenericResponse> guardarMme(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestBody MiembroMesaEscrutinioDTO requestDTO) {

    try {
      TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
      GenericResponse response = new GenericResponse();
      requestDTO.setUsuarioCreacion(tokenInfo.getNombreUsuario());
      requestDTO.setFechaCreacion(new Date());
      this.miembroMesaEscrutinioService.save(requestDTO, tokenInfo);
      try {
        this.actaTransmisionNacionStrategyService.sincronizar(requestDTO.getActaId(), requestDTO.getAcronimoProceso(),
                TransmisionNacionEnum.REGISTRO_MIEMBRO_MESA_ESCRUTINIO, requestDTO.getUsuarioCreacion());

        this.logService.registrarLog(
                tokenInfo.getNombreUsuario(),
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                "Sincronizacion de miembros de mesa de escrutinio " +  requestDTO.getMesa().getMesa() + " se realizó con éxito.",
                tokenInfo.getCodigoCentroComputo(),
                ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

      } catch (Exception ex) {
        log.warn("Problemas al realizar la sincronización a Nación");
      }

      response.setSuccess(Boolean.TRUE);
      response.setMessage("Mesa de escrutinio registrado con éxito");
      return ResponseEntity.status(HttpStatus.OK).body(response);

    } catch (Exception e) {
      log.error(ERROR, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage(), null));
    }
  }

  @PreAuthorize(RoleAutority.VERIFICADOR)
  @PutMapping("/saltar/{idMesa}")
  public ResponseEntity<GenericResponse> saltar(
      @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @PathVariable("idMesa") Long idMesa) {

    try {
      GenericResponse response = new GenericResponse();
      this.mesaService.actualizarEstadoDigitalizaionMEisEdit(idMesa, "P");
      response.setSuccess(Boolean.TRUE);
      response.setMessage("Liberación con éxito");
      return ResponseEntity.status(HttpStatus.OK).body(response);

    } catch (Exception e) {
      log.error(ERROR, e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage(), null));
    }
  }

}
