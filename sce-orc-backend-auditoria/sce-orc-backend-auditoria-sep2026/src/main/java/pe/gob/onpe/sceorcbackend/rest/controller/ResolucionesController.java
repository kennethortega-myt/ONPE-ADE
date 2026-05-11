package pe.gob.onpe.sceorcbackend.rest.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import pe.gob.onpe.sceorcbackend.exception.BadRequestException;
import pe.gob.onpe.sceorcbackend.exception.InternalServerErrorException;
import pe.gob.onpe.sceorcbackend.exception.NotFoundException;
import pe.gob.onpe.sceorcbackend.exception.utils.ResponseHelperException;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.request.ProcesarResolucionRequest;
import pe.gob.onpe.sceorcbackend.model.dto.request.UploadResolucionRequest;
import pe.gob.onpe.sceorcbackend.model.dto.request.resoluciones.ResolucionAsociadosRequest;
import pe.gob.onpe.sceorcbackend.model.dto.request.resoluciones.ResolucionDevueltasRequest;
import pe.gob.onpe.sceorcbackend.model.dto.response.DigitizationGetFilesResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.SearchFilterResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.ActaBean;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.AplicarActaBean;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.DigitizationListResolucionItem;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.ResumenResolucionesDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.SeguimientoOficioDTO;
import pe.gob.onpe.sceorcbackend.model.enums.TipoActaPDF;
import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabResolucion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionExecuteService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ArchivoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CabActaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.OficioService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ResolucionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.StorageService;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.resolucion.TabResolucionDTO;
import pe.gob.onpe.sceorcbackend.security.service.TokenUtilService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoActa;
import pe.gob.onpe.sceorcbackend.utils.ConstantesFormatos;
import pe.gob.onpe.sceorcbackend.utils.RoleAutority;
import pe.gob.onpe.sceorcbackend.utils.reimpresion.ReimpresionCargoDto;

@RestController
@RequestMapping("/resoluciones")
public class ResolucionesController {

    Logger logger = LoggerFactory.getLogger(ResolucionesController.class);

    private final ResolucionService resolucionService;

    private final OficioService oficioService;

    private final ArchivoService archivoService;

    private final StorageService storageService;

    private final TokenUtilService tokenUtilService;

    private final ActaTransmisionExecuteService actaTransmisionNacionStrategyService;

    private final CabActaService actaService;

    public ResolucionesController(ResolucionService resolucionService, OficioService oficioService,
            ArchivoService archivoService, StorageService storageService, TokenUtilService tokenUtilService,
            ActaTransmisionExecuteService actaTransmisionNacionStrategyService, CabActaService actaService) {
        this.resolucionService = resolucionService;
        this.oficioService = oficioService;
        this.archivoService = archivoService;
        this.storageService = storageService;
        this.tokenUtilService = tokenUtilService;
        this.actaTransmisionNacionStrategyService = actaTransmisionNacionStrategyService;
        this.actaService = actaService;
    }

    @GetMapping("/actas_envio_jee")
    public ResponseEntity<GenericResponse<Object>> obtenerActasEnvioJee(
            @RequestParam(required = false) Long idEleccion) {

        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new GenericResponse<>(true,
                            "Informacion resumida de los estados de las actas para envío al jurado",
                            this.resolucionService.obtenerActasEnvioJee(idEleccion)));
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(false, e.getMessage()));
        }
    }

    @GetMapping("/listResolucionesDigtal")
    public ResponseEntity<List<DigitizationListResolucionItem>> listResoluciones(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.resolucionService.listaResolucionesDigtal(tokenInfo.getNombreUsuario()));
    }

    @PreAuthorize(RoleAutority.SCE_SCANNER)
    @GetMapping("/total-digitalizadas")
    public ResponseEntity<List<DigitizationListResolucionItem>> listResolucionesParaEditar(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {

        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        return ResponseEntity.status(HttpStatus.OK)
                .body(this.resolucionService.listaResolucionesParaEditar(tokenInfo.getNombreUsuario()));
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
    @GetMapping("/resumen-resoluciones")
    public ResponseEntity<GenericResponse<ResumenResolucionesDto>> resumenResoluciones(
            @RequestParam(value = "numeroResolucion", required = false) String numeroResolucion) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(new GenericResponse<>(true, "Se listaron las resoluciones correctamente.",
                        this.resolucionService.resumenResoluciones(numeroResolucion)));

    }

    @GetMapping("")
    public ResponseEntity<GenericResponse<ResolucionAsociadosRequest>> getResolucion(
            @RequestParam(value = "id", required = false) Long id) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new GenericResponse<>(true, "Se obtuvo la información de la resolución correctamente.",
                            this.resolucionService.getResolucion(id)));
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(false, e.getMessage()));
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @GetMapping("/resoluciones-devueltas")
    public ResponseEntity<GenericResponse<SearchFilterResponse<ResolucionDevueltasRequest>>> getResolucionesDevueltas(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        try {
            SearchFilterResponse<ResolucionDevueltasRequest> resultado = resolucionService
                    .getResolucionesDevueltas(page, size);

            return ResponseEntity.status(HttpStatus.OK).body(new GenericResponse<>(true,
                    "Se obtuvo la información de las resoluciones devueltas correctamente.", resultado));
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(false, e.getMessage()));
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @GetMapping("/random-resolucion-verificacion")
    public ResponseEntity<GenericResponse<ResolucionAsociadosRequest>> getRandomResolucionParaVerificacion(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {

        GenericResponse<ResolucionAsociadosRequest> genericResponse = new GenericResponse<>(false, "");
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

            ResolucionAsociadosRequest resolucionAsociadosRequest = this.resolucionService
                    .getRandomResolucion(tokenInfo.getNombreUsuario());
            if (resolucionAsociadosRequest == null) {
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new GenericResponse<>(true, "No existen resoluciones pendientes de verificar.", null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(new GenericResponse<>(true,
                        "Se obtuvo la información de la resolución correctamente.", resolucionAsociadosRequest));
            }

        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            genericResponse.setMessage(e.getMessage());
            genericResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
        }

    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @GetMapping("/validar-acta-envio-jee")
    public ResponseEntity<GenericResponse<Object>> validarActaEnvioJee(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestParam String nroActa, @RequestParam String nroCopiaAndDig) {

        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

            GenericResponse<Object> genericResponse = this.resolucionService
                    .obtenerActaEnvioJee(tokenInfo.getCodigoCentroComputo(), nroActa, nroCopiaAndDig);
            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);

        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(false, e.getMessage()));
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/obtener-archivos-sobre")
    public ResponseEntity<GenericResponse<DigitizationGetFilesResponse>> obtenerArchivosSobre(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestBody ActaBean actaBean,
            @RequestParam String tipoSobre) {
        GenericResponse<DigitizationGetFilesResponse> response = new GenericResponse<>();
        try {
            TokenInfo tokenInfo = tokenUtilService.getInfo(authorization);
            response = oficioService.obtenerArchivosSobre(tokenInfo, actaBean, tipoSobre);
            return createResponseEntity(response);
        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage(e.getMessage());
            return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/transmitir-oficio")
    public ResponseEntity<GenericResponse<Boolean>> transmitirOficioActa(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestBody ActaBean actaBean) {

        GenericResponse<Boolean> response = new GenericResponse<>();
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            response = oficioService.transmitirOficio(actaBean.getActaId(), tokenInfo.getAbrevProceso(),
                    TransmisionNacionEnum.SOBRES_CELESTES, tokenInfo.getNombreUsuario());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (Exception e) {
            logger.error("Error inesperado al iniciar la transmisión del oficio", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(false, "Error al transmitir el oficio: " + e.getMessage()));
        }
    }

    @PostMapping("/verificar-documento-envio")
    public ResponseEntity<GenericResponse<Object>> verificarDocumentoEnvio(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestBody ActaBean actaBean,
            @RequestParam("tipoDocumento") String tipoDocumento) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            GenericResponse<Object> genericResponse = oficioService.verificarDocumentoEnvio(tokenInfo, actaBean,
                    tipoDocumento);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(Boolean.FALSE, e.getMessage()));
        }
    }

    private ResponseEntity<GenericResponse<DigitizationGetFilesResponse>> createResponseEntity(
            GenericResponse<DigitizationGetFilesResponse> response) {
        return ResponseEntity.status(response.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(response);
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @GetMapping("/validar-acta-devuelta-jee")
    public ResponseEntity<GenericResponse<Object>> validarActaDevueltaJee(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization,
            @RequestParam("nroActa") String nroActa, @RequestParam("nroCopiaAndDig") String nroCopiaAndDig) {

        GenericResponse<Object> genericResponse = new GenericResponse<>(false, "");
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            genericResponse = this.resolucionService.validarActaDevueltaJee(tokenInfo.getCodigoCentroComputo(), nroActa,
                    nroCopiaAndDig);
            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            genericResponse.setMessage(e.getMessage());
            genericResponse.setData(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(genericResponse);
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @GetMapping("/info-acta")
    public ResponseEntity<GenericResponse<Object>> getInfoActa(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam(value = "codTipoResolucion", required = false) Integer codTipoResolucion,
            @RequestParam("nroActaCopiaDig") String nroActaCopiaDig,
            @RequestParam(value = "idProceso", required = false) Long idProceso) {
        try {
            GenericResponse<Object> genericResponse = this.resolucionService.obtenerInfoActa(codTipoResolucion,
                    nroActaCopiaDig, idProceso);
            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(false, e.getMessage()));
        }
    }

    /**
     * CB_RESOLUCIONES_CONTROLLER_INFO_ACTA_PARA_ASOCIACION
     */
    @PreAuthorize(RoleAutority.VERIFICADOR)
    @GetMapping("/info-only-acta")
    public ResponseEntity<GenericResponse<List<ActaBean>>> getInfoActaParaAsociacionResoluciones(
            @RequestParam(value = "codTipoResolucion", required = false) Integer codTipoResolucion,
            @RequestParam("nroActaCopiaDig") String nroActaCopiaDig,
            @RequestParam(value = "idUbigeo", required = false) Long idUbigeo,
            @RequestParam(value = "idLocalVotacion", required = false) Long idLocalVotacion,
            @RequestParam(value = "idEleccion", required = false) Long idEleccion,
            @RequestParam(value = "idProceso", required = false) Long idProceso) {

        try {
            GenericResponse<List<ActaBean>> genericResponse = this.resolucionService
                    .getInfoActaParaAsociacionResoluciones(codTipoResolucion, nroActaCopiaDig, idUbigeo,
                            idLocalVotacion, idEleccion, idProceso);
            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(false, e.getMessage()));
        }
    }

    // Obtiene la información de un acta a verificar en la resolucion
    @PreAuthorize(RoleAutority.VERIFICADOR)
    @GetMapping("/info-acta-by-id")
    public ResponseEntity<GenericResponse<ActaBean>> getInfoActa(
            @RequestParam(value = "idActa", required = false) Long idActa) {
        try {
            GenericResponse<ActaBean> genericResponse = this.resolucionService.obtenerInfoActaById(idActa);
            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(false, e.getMessage()));
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/registrar-asociacion")
    public ResponseEntity<GenericResponse<Object>> registrarAsociacionConActas(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody ResolucionAsociadosRequest resolucionAsociadosRequest,
            @RequestParam(value = "idUbigeo", required = false) Long idUbigeo,
            @RequestParam(value = "idLocalVotacion", required = false) Long idLocalVotacion,
            @RequestParam(value = "idEleccion", required = false) Long idEleccion,
            @RequestParam(value = "idProceso", required = false) Long idProceso) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

            logger.info("Registrando asociación {}", resolucionAsociadosRequest);

            GenericResponse<Object> genericResponse = this.resolucionService.registrarAsociacionConActas(tokenInfo,
                    resolucionAsociadosRequest, idUbigeo, idLocalVotacion, idEleccion);

            logger.info("Respuesta service registro asociación {}", genericResponse);

            if (genericResponse.isSuccess()) {
                List<Long> actaIds = genericResponse.getActasId();
                logger.info("Actas Id a sincronizar asociación {}", genericResponse);
                this.actaTransmisionNacionStrategyService.sincronizar(actaIds, tokenInfo.getAbrevProceso(),
                        TransmisionNacionEnum.ASOCIACION_RESOL_TRANSMISION, tokenInfo.getNombreUsuario());
            }
            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(false, e.getMessage()));
        }
    }

    /**
     * Aplica la resolucion y retorna en la data la informacion de la siguiente acta
     * a resolver
     */
    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/aplicar-resolucion")
    public ResponseEntity<GenericResponse<AplicarActaBean>> aplicarResolucion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestBody ActaBean actaBean) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

            GenericResponse<AplicarActaBean> genericResponse = this.resolucionService.aplicarResolucion(tokenInfo,
                    actaBean);
            if (genericResponse.isSuccess()) {
                sincronizarAplicarResolucion(actaBean, tokenInfo);
            }
            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(false, e.getMessage()));
        }
    }

    private void sincronizarAplicarResolucion(ActaBean actaBean, TokenInfo tokenInfo) {
        try {
            this.actaTransmisionNacionStrategyService.sincronizar(actaBean.getActaId(), tokenInfo.getAbrevProceso(),
                    TransmisionNacionEnum.APLICAR_RES_TRANSMISION, tokenInfo.getNombreUsuario());
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
        }
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB)
    @PostMapping("/actualizar-estado-digitalizacion")
    public ResponseEntity<GenericResponse<TabResolucionDTO>> actualizarEstadoDigitalizacion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("idResolucion") Long idResolucion,
            @RequestParam("estadoDigitalizacion") String estadoDigitalizacion) {

        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            GenericResponse<TabResolucionDTO> genericResponse = this.resolucionService
                    .actualizarEstadoDigitalizacion(tokenInfo, idResolucion, estadoDigitalizacion);
            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(Boolean.FALSE, e.getMessage()));
        }
    }

    @PreAuthorize(RoleAutority.SCE_SCANNER)
    @PostMapping("/validar-para-edicion")
    public ResponseEntity<GenericResponse<TabResolucionDTO>> validacionParaEdicion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("numeroResolucion") String numeroResolucion) {

        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            GenericResponse<TabResolucionDTO> genericResponse = this.resolucionService.validarParaEdicion(tokenInfo,
                    numeroResolucion);
            return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(Boolean.FALSE, e.getMessage()));
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/anular-resolucion")
    public ResponseEntity<GenericResponse<TabResolucionDTO>> actualizarEstadoResolucion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("idResolucion") Long idResolucion) {
        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        this.resolucionService.anularResolucion(tokenInfo, idResolucion);
        return ResponseHelperException.createSuccessResponse("Se anuló la resolución correctamente.");
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/generar-cargo-entrega-v2")
    public ResponseEntity<GenericResponse<Object>> generarCargoEntregaActasEnviadasAlJurado(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestBody List<ActaBean> actaBeanList) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

            GenericResponse<Object> genericResponse = resolucionService.generarCargoEntrega(tokenInfo, actaBeanList);
            if (genericResponse.isSuccess()) {

                List<Long> actaIds = actaBeanList.stream().filter(
                        acta -> ConstantesEstadoActa.ESTADO_ACTA_PARA_ENVIO_AL_JURADO.equals(acta.getEstadoActa()))
                        .map(ActaBean::getActaId).toList();

                this.actaTransmisionNacionStrategyService.sincronizar(actaIds, tokenInfo.getAbrevProceso(),
                        TransmisionNacionEnum.ENVIADA_JEE_TRANSMISION, tokenInfo.getNombreUsuario());

            }
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(Boolean.FALSE, e.getMessage()));
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/generar-cargo-oficio")
    public ResponseEntity<GenericResponse<Object>> generarCargoEntregaOficio(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestBody ActaBean actaBean) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            GenericResponse<Object> genericResponse = resolucionService.generarCargoEntregaOficio(tokenInfo, actaBean);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(Boolean.FALSE, e.getMessage()));
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/generar-cargo-entrega-acta-devuelta")
    public ResponseEntity<GenericResponse<Object>> generarCargoEntregaActaDevuelta(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestBody List<ActaBean> actaBeanList) {

        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

            GenericResponse<Object> genericResponse1 = resolucionService.generarCargoEntregaActaDevuelta(tokenInfo,
                    actaBeanList);

            if (genericResponse1.isSuccess()) {

                List<Long> actaIds = actaBeanList != null ? actaBeanList.stream().map(ActaBean::getActaId).toList()
                        : Collections.emptyList();

                this.actaTransmisionNacionStrategyService.sincronizar(actaIds, tokenInfo.getAbrevProceso(),
                        TransmisionNacionEnum.DEVUELTA_JEE_TRANSMISION, tokenInfo.getNombreUsuario());

            }

            return new ResponseEntity<>(genericResponse1, HttpStatus.OK);

        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(Boolean.FALSE, e.getMessage()));
        }

    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/generar-cargo-entrega-infundada") // tambien se usa para cargo de entrega de actas anuladas por
                                                     // ubigeo
    public ResponseEntity<GenericResponse<Object>> generarCargoEntregaInfundada(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody ResolucionAsociadosRequest resolucionAsociadosRequest) {
        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            GenericResponse<Object> genericResponse1 = resolucionService.generarCargoEntregaInfundadas(tokenInfo,
                    resolucionAsociadosRequest);
            return new ResponseEntity<>(genericResponse1, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(Boolean.FALSE, e.getMessage()));
        }

    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/generar-cargo-entrega-mesa-no-instalada-ext-sin")
    public ResponseEntity<GenericResponse<Object>> generarCargoEntregaMesaNoInstalada(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody ResolucionAsociadosRequest resolucionAsociadosRequest) {

        try {

            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);

            GenericResponse<Object> genericResponse1 = resolucionService.generarCargoEntregaMesaNoInstaladas(tokenInfo,
                    resolucionAsociadosRequest);

            if (genericResponse1.isSuccess()) {
                List<Long> actaIds = resolucionAsociadosRequest != null
                        ? resolucionAsociadosRequest.getActasAsociadas().stream().map(ActaBean::getActaId).toList()
                        : Collections.emptyList();

                logger.info("actas a sincronizar {}", actaIds);

                this.actaTransmisionNacionStrategyService.sincronizar(actaIds, tokenInfo.getAbrevProceso(),
                        TransmisionNacionEnum.CARGONO_NOINSTAL_EXT_SINI_TRANSMISION, tokenInfo.getNombreUsuario());
            }

            return new ResponseEntity<>(genericResponse1, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(Boolean.FALSE, e.getMessage()));
        }

    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @PostMapping("/generar-oficio-acta-observada")
    public ResponseEntity<GenericResponse<Object>> generarOficioActaObservada(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization, @RequestBody List<ActaBean> actaBeanList) {

        try {
            TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
            GenericResponse<Object> genericResponse = oficioService.generarOficio(tokenInfo, actaBeanList);

            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(false, e.getMessage()));
        }
    }

    @PreAuthorize(RoleAutority.VERIFICADOR)
    @GetMapping("/reimpresion-cargos/{mesa}")
    public ResponseEntity<GenericResponse<List<ReimpresionCargoDto>>> reimpresionCargosController(
            @PathVariable("mesa") String mesa) {
        return ResponseHelperException.createSuccessResponse(
                "Se generó la lista de reimpresion de cargos correctamente.",
                this.resolucionService.reimpresionCargos(mesa));
    }

    @GetMapping("/file/{id}")
    public ResponseEntity<byte[]> getFile(@PathVariable("id") Long id) {
        try {
            Archivo archivo = this.archivoService.getArchivoById(id);
            if (archivo == null) {
                throw new NotFoundException(ConstantesComunes.MENSAJE_FILE_NOT_FOUND);
            }

            Resource res = this.storageService.loadFile(id.toString(), false);
            String contentType = archivo.getFormato();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            String.format("attachment; filename=\"%s\"", archivo.getNombre()))
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(Files.readAllBytes(res.getFile().toPath()));
        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to download the file. Error: " + e.getMessage());
        }
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
    @GetMapping("/file-resolucion-pdf/{id}")
    public ResponseEntity<byte[]> getFileResolucionPDF(@PathVariable("id") Long id) {
        try {

            Archivo archivo = this.archivoService.getArchivoById(id);
            if (archivo == null) {
                throw new NotFoundException(ConstantesComunes.MENSAJE_FILE_NOT_FOUND);
            }
            Resource res = this.storageService.loadFile(archivo.getGuid(), false);
            File fileTiff = res.getFile();
            String contentType = archivo.getFormato();
            if (contentType.equals("image/tiff")) {
                contentType = MediaType.APPLICATION_PDF_VALUE;
                String fileNamePdf = archivo.getNombre().substring(0, archivo.getNombre().length() - 4) + ".pdf";
                File pdf = this.storageService.convertTIFFToPDF(fileTiff, fileNamePdf);
                return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=\"" + fileNamePdf + "\"")
                        .contentType(MediaType.parseMediaType(contentType)).body(Files.readAllBytes(pdf.toPath()));
            } else {
                contentType = MediaType.APPLICATION_PDF_VALUE;
                String fileNamePdf = archivo.getNombre();
                return ResponseEntity.ok().header("Content-Disposition", "attachment; filename=\"" + fileNamePdf + "\"")
                        .contentType(MediaType.parseMediaType(contentType)).body(Files.readAllBytes(fileTiff.toPath()));
            }

        } catch (Exception e) {
            throw new InternalServerErrorException("Failed to download the file. Error: " + e.getMessage());
        }

    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
    @PostMapping("/file-resolucion-pdf-popup/{id}")
    public ResponseEntity<GenericResponse<Object>> getFileResolucionPDFPopup(@PathVariable("id") Long id) {
        GenericResponse<Object> genericResponse = new GenericResponse<>();
        Archivo archivo;

        try {

            archivo = this.archivoService.getArchivoById(id);
            if (archivo == null) {
                throw new NotFoundException(ConstantesComunes.MENSAJE_FILE_NOT_FOUND);
            }

            // buscar por GUID, si no existe intentamos por nombre
            Resource res = loadResourceByGuidOrName(archivo);

            File file = res.getFile();
            String encodedString;

            if (ConstantesFormatos.IMAGE_TIF_VALUE.equalsIgnoreCase(archivo.getFormato())) {
                String fileNamePdf = archivo.getNombre().replaceAll("(?i)\\.tif$", ".pdf");
                File pdf = this.storageService.convertTIFFToPDF(file, fileNamePdf);
                encodedString = Base64.getEncoder().encodeToString(Files.readAllBytes(pdf.toPath()));
            } else {
                encodedString = Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
            }

            genericResponse.setSuccess(true);
            genericResponse.setMessage("pdf");
            genericResponse.setData(encodedString);
            return ResponseEntity.ok(genericResponse);

        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            genericResponse.setSuccess(false);
            genericResponse.setMessage("No se generó el PDF correctamente.");
            genericResponse.setData(null);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }
    }

    private Resource loadResourceByGuidOrName(Archivo archivo) {
        try {
            return this.storageService.loadFile(archivo.getGuid(), false);
        } catch (Exception e) {
            try {
                return this.storageService.loadFile(archivo.getNombre(), false);
            } catch (MalformedURLException ex) {
                throw new InternalServerErrorException(ex.getMessage());
            }
        }
    }

    @PreAuthorize(RoleAutority.ROLES_SCE_WEB_MAS_REPORTES)
    @PostMapping("/file-pdf-popup-conv-stae")
    public ResponseEntity<GenericResponse<Object>> getFilePDFPopupConvStae(
            @RequestParam(value = "idArchivo", required = true) Long idArchivo,
            @RequestParam(value = "idActa", required = true) Long idActa) {

        GenericResponse<Object> genericResponse = new GenericResponse<>();
        Archivo archivo;

        try {
            archivo = this.archivoService.getArchivoById(idArchivo);
            if (archivo == null) {
                throw new NotFoundException(ConstantesComunes.MENSAJE_FILE_NOT_FOUND);
            }

            Resource res = loadResourceByGuidOrName(archivo);
            File file = res.getFile();
            String encodedString;

            if (ConstantesFormatos.IMAGE_TIF_VALUE.equalsIgnoreCase(archivo.getFormato())) {
                TipoActaPDF tipoActa = resolverTipoActaPDF(idActa);
                String fileNamePdf = archivo.getNombre().replaceAll("(?i)\\.tif$", ".pdf");
                File pdf = this.storageService.convertTIFFToPDFConvExtSTAE(file, fileNamePdf, tipoActa);
                encodedString = Base64.getEncoder().encodeToString(Files.readAllBytes(pdf.toPath()));
            } else {
                encodedString = Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
            }

            genericResponse.setSuccess(true);
            genericResponse.setMessage("pdf");
            genericResponse.setData(encodedString);
            return ResponseEntity.ok(genericResponse);

        } catch (Exception e) {
            logger.error(ConstantesComunes.MSJ_ERROR, e);
            genericResponse.setSuccess(false);
            genericResponse.setMessage("No se generó el PDF correctamente.");
            genericResponse.setData(null);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        }
    }

    @PreAuthorize(RoleAutority.SCE_SCANNER)
    @PostMapping("/uploadResolucionDigitalizada")
    public ResponseEntity<GenericResponse<TabResolucion>> uploadResolucionDigitalizada(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @Valid @ModelAttribute UploadResolucionRequest request) {

        TokenInfo tokenInfo = tokenUtilService.getInfo(authorization);

        this.resolucionService.digitalizarResolucion(tokenInfo, request.getIdResolucion(),
                request.getNumeroResolucion(), request.getNumeroPaginas(), request.getFile());

        return ResponseHelperException.createSuccessResponse(
                String.format("Resolución %s guardada correctamente", request.getNumeroResolucion()));
    }

    @GetMapping("/seguimientoJEE")
    public ResponseEntity<GenericResponse<List<SeguimientoOficioDTO>>> seguimientoJEE(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        try {
            TokenInfo tokenInfo = tokenUtilService.getInfo(authorization);

            List<SeguimientoOficioDTO> data = oficioService.obtenerSeguimiento(tokenInfo);
            return ResponseEntity.ok(new GenericResponse<>(true, "Seguimiento cargado correctamente", data));
        } catch (Exception e) {
            logger.error("Error al obtener seguimiento JEE-JNE", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(false, "Error al obtener seguimiento: " + e.getMessage()));
        }
    }

    @PostMapping("/procesar-resolucion-jne")
    public ResponseEntity<GenericResponse<Object>> procesarResolucionJEE(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestBody ProcesarResolucionRequest request) {
        try {
            TokenInfo tokenInfo = tokenUtilService.getInfo(authorization);
            GenericResponse<Object> genericResponse = oficioService.procesarResolucion(request, tokenInfo);
            return new ResponseEntity<>(genericResponse, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Error al procesar Resolucion JEE-JNE", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new GenericResponse<>(false, "Error al procesar Resolucion JEE : " + e.getMessage()));
        }
    }

    /**
     * Bloquear resolución y asignar usuario de asociación Usa FOR UPDATE SKIP
     * LOCKED para evitar que múltiples usuarios tomen la misma resolución
     *
     * Casos: 1. Si usuarioAsociacion es NULL → bloquea y asigna el usuario 2. Si
     * usuarioAsociacion NO es NULL y es diferente → BadRequestException 3. Si
     * usuarioAsociacion NO es NULL y es igual → retorna true (ya asignado)
     */
    @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
    @PostMapping("/bloquear-resolucion")
    public ResponseEntity<GenericResponse<Boolean>> bloquearResolucion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("idResolucion") Long idResolucion) {

        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        String usuario = tokenInfo.getNombreUsuario();

        GenericResponse<Boolean> genericResponse = this.resolucionService.bloquearYAsignarResolucion(idResolucion,
                usuario);

        return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
    }

    @PreAuthorize(RoleAutority.ADMINISTRADOR_CC)
    @PostMapping("/desbloquear-resolucion")
    public ResponseEntity<GenericResponse<Boolean>> desbloquearResolucion(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("idResolucion") Long idResolucion) {

        TokenInfo tokenInfo = this.tokenUtilService.getInfo(authorization);
        String usuario = tokenInfo.getNombreUsuario();

        GenericResponse<Boolean> genericResponse = this.resolucionService.desbloquearResolucion(idResolucion, usuario);

        return ResponseEntity.status(HttpStatus.OK).body(genericResponse);
    }

    private TipoActaPDF resolverTipoActaPDF(Long idActa) {
        Acta acta = this.actaService.findById(idActa)
                .orElseThrow(() -> new NotFoundException("No se encontró el acta con id: " + idActa));

        if (ConstantesComunes.SOLUCION_TECNOLOGICA_STAE.compareTo(acta.getSolucionTecnologica()) == 0) {
            return TipoActaPDF.STAE;
        }

        if (ConstantesComunes.SOLUCION_TECNOLOGICA_CONVENCIONAL.compareTo(acta.getSolucionTecnologica()) == 0) {
            String ubigeo = acta.getUbigeoEleccion().getUbigeo().getCodigo();
            if (ubigeo.startsWith("9")) {
                return TipoActaPDF.CONVENCIONAL_EXTRANJERO;
            }
            return TipoActaPDF.CONVENCIONAL;
        }

        throw new BadRequestException(
                "Tipo de solución tecnológica no soportado para generación de PDF: " + acta.getSolucionTecnologica());
    }

}
