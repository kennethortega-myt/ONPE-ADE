package pe.gob.onpe.sceorcbackend.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.sceorcbackend.exception.BadRequestException;
import pe.gob.onpe.sceorcbackend.exception.utils.ResponseHelperException;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.otrosdocumentos.DetOtroDocumentoDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.otrosdocumentos.OtroDocumentoDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.otrosdocumentos.ResumenOtroDocumentoDto;
import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionExecuteService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CabOtroDocumentoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.digitalizacion.DigtalDTO;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;
import pe.gob.onpe.sceorcbackend.utils.SceUtils;

import java.util.List;

@RestController
@RequestMapping("/otros-documentos")
public class OtrosDocumentosController {

    Logger logger = LoggerFactory.getLogger(OtrosDocumentosController.class);

    private final TokenUtilService tokenUtilService;

    private final CabOtroDocumentoService cabOtroDocumentoService;

    private final ActaRepository actaRepository;

    private final ActaTransmisionExecuteService actaTransmisionNacionStrategyService;


    public OtrosDocumentosController(
            TokenUtilService tokenUtilService,
            CabOtroDocumentoService cabOtroDocumentoService,
            ActaRepository actaRepository,
            ActaTransmisionExecuteService actaTransmisionNacionStrategyService) {
        this.tokenUtilService = tokenUtilService;
        this.cabOtroDocumentoService = cabOtroDocumentoService;
        this.actaRepository = actaRepository;
        this.actaTransmisionNacionStrategyService = actaTransmisionNacionStrategyService;
    }

    /**
     * Method to upload scanned document digitization
     */
    @PreAuthorize(RoleAutority.SCE_SCANNER)
    @PostMapping("/upload-digtal")
    public ResponseEntity<GenericResponse<DigtalDTO>> uploadDigtal(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam(name = "idOtroDocumento", required = false) Long idOtroDocumento,
            @RequestParam("file") MultipartFile file,
            @RequestParam("numeroDocumento") String numeroDocumento,
            @RequestParam("numeroPaginas") Integer numeroPaginas,
            @RequestParam("abreviaturaDocumento") String abreviaturaDocumento) {

        logger.info("Otros documentos - Upload digitization -> numeroDocumento: {}, abreviaturaDocumento: {}.", numeroDocumento, abreviaturaDocumento);
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        if (SceUtils.tieneEspaciosEnExtremos(numeroDocumento)) {
            throw new BadRequestException(String.format(
                    "El número de documento tiene espacios en blanco al inicio y/o final: '%s'.", numeroDocumento));
        }

        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Debe adjuntar un archivo para la resolución.");
        }
        this.cabOtroDocumentoService.registrarNuevoDocumento(tokenInfo, idOtroDocumento, numeroDocumento, abreviaturaDocumento, numeroPaginas, file);
        return ResponseHelperException.createSuccessResponse("Documento digitalizado correctamente.");
    }

    @PreAuthorize(RoleAutority.CONTROL_DIGITAL)
    @GetMapping("/control-digitalizacion")
    public ResponseEntity<List<OtroDocumentoDto>> controlDigitalizacion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("abreviaturaDocumento") String abreviaturaDocumento) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        return ResponseEntity.status(HttpStatus.OK).body(this.cabOtroDocumentoService.listarControlDigitalizacion(tokenInfo.getNombreUsuario(), abreviaturaDocumento));
    }

    @PreAuthorize(RoleAutority.SCE_SCANNER)
    @GetMapping("/total-digitalizadas")
    public ResponseEntity<List<OtroDocumentoDto>> listarOtrosDocumentosDigitalizados(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        return ResponseEntity.status(HttpStatus.OK).body(this.cabOtroDocumentoService.listarOtrosDocumentosDigitalizados(tokenInfo.getNombreUsuario()));
    }

    @PreAuthorize(RoleAutority.CONTROL_DIGITAL)
    @PostMapping("/actualizar-estado-digitalizacion")
    public ResponseEntity<GenericResponse<Boolean>> actualizarEstadoDigitalizacion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("idOtroDocumento") Long idOtroDocumento,
            @RequestParam("estadoDigitalizacion") String estadoDigitalizacion) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        this.cabOtroDocumentoService.actualizarEstadoDigitalizacion(tokenInfo.getNombreUsuario(), idOtroDocumento, estadoDigitalizacion);
        return ResponseHelperException.createSuccessResponse("Se actualizó el estado del documento correctamente.");
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @GetMapping("/resumen")
    public ResponseEntity<GenericResponse<ResumenOtroDocumentoDto>> resumenResoluciones(
            @RequestParam(value = "numeroDocumento", required = false) String numeroDocumento,
            @RequestParam(value = "estadoDocumento", required = false) String estadoDocumento,
            @RequestParam(value = "estadoDigitalizacion", required = false) String estadoDigitalizacion
    ) {
        return ResponseHelperException.createSuccessResponse("Se listaron los otros documentos correctamente.",
                this.cabOtroDocumentoService.resumenOtrosDocumentos(numeroDocumento, estadoDocumento, estadoDigitalizacion));
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/validar-mesa")
    public ResponseEntity<GenericResponse<DetOtroDocumentoDto>> validarMesaParaAsociacion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody DetOtroDocumentoDto detOtroDocumentoDto
    ) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        return ResponseHelperException.createSuccessResponse("Se validó con éxito los parámetros ingresados.",
                this.cabOtroDocumentoService.validarMesaParaAsociacion(tokenInfo, detOtroDocumentoDto));
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/registrar-asociacion")
    public ResponseEntity<GenericResponse<Boolean>> registrarAsociacion(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                                                               @RequestBody OtroDocumentoDto otroDocumentoDto) {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            logger.info("Registrando asociación {}", otroDocumentoDto);
            this.cabOtroDocumentoService.registrarAsociacion(tokenInfo, otroDocumentoDto);
            return ResponseHelperException.createSuccessResponse("Se registró la asociación correctamente.");
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/procesar-asociacion")
    public ResponseEntity<GenericResponse<Boolean>> procesarAsociacion(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                                                                @RequestBody OtroDocumentoDto otroDocumentoDto) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        logger.info("Procesando asociación {}", otroDocumentoDto);
        this.cabOtroDocumentoService.procesarAsociacion(tokenInfo, otroDocumentoDto);

        transmitirActasAsociadasADenuncias(otroDocumentoDto, tokenInfo);

        return ResponseHelperException.createSuccessResponse("Se procesó el documento correctamente.");
    }

    private void transmitirActasAsociadasADenuncias(OtroDocumentoDto otroDocumentoDto, TokenInfo tokenInfo) {
        if (otroDocumentoDto.getDetOtroDocumentoDtoList() == null || otroDocumentoDto.getDetOtroDocumentoDtoList().isEmpty()) {
            logger.warn("No hay detalles de documentos para transmitir denuncias");
            return;
        }

        for (DetOtroDocumentoDto dto : otroDocumentoDto.getDetOtroDocumentoDtoList()) {
            this.actaRepository.findActaPrincipalByMesa(dto.getNumeroMesa())
                    .stream()
                    .findFirst()
                    .ifPresentOrElse(
                            acta -> {
                                logger.info("Transmitiendo denuncias de la mesa {}, acta principal {}.", dto.getNumeroMesa(), acta.getId());
                                sincronizar(acta.getId(), tokenInfo.getNombreUsuario(), tokenInfo.getAbrevProceso());
                            },
                            () -> logger.warn("No se encontró acta principal para la mesa {}.", dto.getNumeroMesa())
                    );
        }
    }

    private void sincronizar(Long idActa, String usuario, String proceso) {
        try {
            this.actaTransmisionNacionStrategyService.sincronizar(idActa, proceso, TransmisionNacionEnum.DENUNCIAS, usuario);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MENSAJE_LOGGER_ERROR_STACK, e);
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/anular")
    public ResponseEntity<GenericResponse<Boolean>> anularDocumento(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
                                                                       @RequestBody OtroDocumentoDto otroDocumentoDto) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        logger.info("Anulando documento {}", otroDocumentoDto);
        this.cabOtroDocumentoService.anularDocumento(tokenInfo, otroDocumentoDto);
        return ResponseHelperException.createSuccessResponse("Se anuló el documento correctamente.");
    }

}
