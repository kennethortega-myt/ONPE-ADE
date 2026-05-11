package pe.gob.onpe.sceorcbackend.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pe.gob.onpe.sceorcbackend.exception.utils.ResponseHelperException;
import pe.gob.onpe.sceorcbackend.model.dto.MesaDTO;
import pe.gob.onpe.sceorcbackend.model.dto.ReprocesarMesaResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.mesas.DigitizationListMesasItem;
import pe.gob.onpe.sceorcbackend.model.dto.response.otrosdocumentos.OtroDocumentoDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.ActaBean;
import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionExecuteService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CabActaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MesaService;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/mesa")
public class MesaController {

    Logger logger = LoggerFactory.getLogger(MesaController.class);

    private final TokenUtilService tokenUtilService;
    private final MesaService mesaService;
    private final ActaTransmisionExecuteService actaTransmisionNacionStrategyService;
    private final CabActaService cabActaService;

    public MesaController(TokenUtilService tokenUtilService, MesaService mesaService, ActaTransmisionExecuteService actaTransmisionNacionStrategyService, CabActaService cabActaService) {
        this.tokenUtilService = tokenUtilService;
        this.mesaService = mesaService;
        this.actaTransmisionNacionStrategyService = actaTransmisionNacionStrategyService;
        this.cabActaService = cabActaService;
    }

    @PreAuthorize(RoleAutority.CONTROL_DIGITAL)
    @GetMapping("/listListaElectoresDigtal")
    public ResponseEntity<List<DigitizationListMesasItem>> listLE(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        return ResponseEntity.status(HttpStatus.OK).body(this.mesaService.listListaElectoresDigtal(tokenInfo.getNombreUsuario()));
    }

    @PreAuthorize(RoleAutority.CONTROL_DIGITAL)
    @GetMapping("/listMimebrosMesaDigtal")
    public ResponseEntity<List<DigitizationListMesasItem>> listMM(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            return ResponseEntity.status(HttpStatus.OK).body(this.mesaService.listMiembrosMesaDigtal(tokenInfo.getNombreUsuario()));
        } catch (Exception e) {
            logger.error("Error /mesa/listMimebrosMesaDigtal", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }



    @PreAuthorize(RoleAutority.SCE_SCANNER)
    @GetMapping("/list-le-scescanner")
    public ResponseEntity<List<OtroDocumentoDto>> listarListaElectoresScanner(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        return ResponseEntity.status(HttpStatus.OK).body(this.mesaService.listarListaElectoresScanner(tokenInfo.getNombreUsuario()));
    }

    @PreAuthorize(RoleAutority.SCE_SCANNER)
    @GetMapping("/list-mm-scescanner")
    public ResponseEntity<List<OtroDocumentoDto>> listarMiembrosElectoresScanner(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        return ResponseEntity.status(HttpStatus.OK).body(this.mesaService.listarMiembrosMesaScanner(tokenInfo.getNombreUsuario()));
    }

    /**
     * tipoDoc "LE" para lista de electores
     */
    @PreAuthorize(RoleAutority.CONTROL_DIGITAL)
    @PostMapping("/approve")
    public ResponseEntity<GenericResponse<Boolean>> approveMesa(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("mesaId") Long mesaId, @RequestParam("tipoDoc") String tipoDocumento) {

        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        this.mesaService.approveMesa(mesaId, tipoDocumento, tokenInfo.getNombreUsuario(), tokenInfo.getCodigoCentroComputo());
        return ResponseHelperException.createSuccessResponse("Se aprobó el documento correctamente.");
    }

    /**
     * tipoDoc "HA" para lista de asistencia
     */
    @PreAuthorize(RoleAutority.CONTROL_DIGITAL)
    @PostMapping("/reject")
    public ResponseEntity<GenericResponse<Boolean>> rejectMesa(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                              @RequestParam("mesaId") Long mesaId, @RequestParam("tipoDoc") String tipoDocumento) {

        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        this.mesaService.rejectMesa(mesaId, tipoDocumento, tokenInfo.getNombreUsuario());
        return ResponseHelperException.createSuccessResponse("Se rechazó el documento correctamente.");

    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @GetMapping("/validaHabilitarActasStae")
    public ResponseEntity<GenericResponse<List<ActaBean>>> validaHabilitarContingenciaStae(
            @RequestParam("mesa") String mesa) {

        return ResponseHelperException.createSuccessResponse(
                String.format("Se listó las actas STAE para la mesa %s.", mesa),
                this.mesaService.validaHabilitarContingenciaStae(mesa)
        );
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/habilitarActasStae")
    public ResponseEntity<GenericResponse<ActaBean>> habilitarContingenciaStae(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody List<ActaBean> actaBeanList) {

        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        this.mesaService.habilitarContingenciaStae(actaBeanList, tokenInfo.getNombreUsuario());
        return ResponseHelperException.createSuccessResponse("Se actualizó el tipo de transmisión para las actas seleccionadas.");

    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @GetMapping("/buscarReprocesarMesa")
    public ResponseEntity<GenericResponse<ReprocesarMesaResponseDto>> buscarReprocesarMesa(@RequestParam("codMesa") String  codMesa) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(this.mesaService.buscarMesaReprocesar(codMesa));
        } catch (Exception e) {
            logger.error("Error /mesa/buscarReprocesarMesa", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/buscarEliminarOmiso")
    public ResponseEntity<GenericResponse<MesaDTO>> buscarEliminarOmiso(@RequestParam("codMesa") String  codMesa) {
        try {
            return ResponseEntity.status(HttpStatus.OK).body(this.mesaService.buscarMesaEliminarOmiso(codMesa));
        } catch (Exception e) {
            logger.error("Error /mesa/buscarEliminarOmiso", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, e.getMessage(), null));
        }
    }

    @PostMapping("/saveEliminarOmiso")
    public ResponseEntity<GenericResponse<Boolean>> guardarEliminarOmiso(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestBody List<MesaDTO> data
    ) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

        try{
            GenericResponse<Boolean> genericResponse = new GenericResponse<>();
            this.mesaService.procesarEliminarOmisosMesa(data, tokenInfo.getNombreUsuario(), tokenInfo.getCodigoCentroComputo());
            genericResponse.setSuccess(true);
            for(MesaDTO mesaDTO : data){
                sincronizarMesa(mesaDTO.getId(),TransmisionNacionEnum.OMISOS_LISTA_ELECTORES_VOTANTES ,tokenInfo.getNombreUsuario(), tokenInfo.getAbrevProceso());
                sincronizarMesa(mesaDTO.getId(),TransmisionNacionEnum.OMISOS_MIEMBRO_MESA ,tokenInfo.getNombreUsuario(), tokenInfo.getAbrevProceso());
            }
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }catch (Exception e){
            return ResponseEntity.status( HttpStatus.INTERNAL_SERVER_ERROR).body(new GenericResponse<>(false, "Servicio no disponible"));
        }
    }

    private void sincronizarMesa(Long idMesa, TransmisionNacionEnum transmisionNacionEnum, String usuario, String proceso) {
        try {
            List<Acta> actaList = this.cabActaService.findByMesaId(idMesa);
            if(!actaList.isEmpty()){
                this.actaTransmisionNacionStrategyService.sincronizar(actaList.getFirst().getId(), proceso, transmisionNacionEnum, usuario);
            }
        } catch (Exception e) {
            logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR_STACK, e);
        }
    }

}
