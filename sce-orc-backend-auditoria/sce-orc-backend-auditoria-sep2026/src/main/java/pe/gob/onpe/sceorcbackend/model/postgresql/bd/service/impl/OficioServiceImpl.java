package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.sceorcbackend.exception.BadRequestException;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.request.ProcesarResolucionRequest;
import pe.gob.onpe.sceorcbackend.model.dto.request.resoluciones.ResolucionAsociadosRequest;
import pe.gob.onpe.sceorcbackend.model.dto.response.DigitizationGetFilesResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.ActaBean;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.ActaOficioBean;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.DetActaOficioBean;
import pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones.SeguimientoOficioDTO;
import pe.gob.onpe.sceorcbackend.model.enums.AccionResolucionJEEnum;
import pe.gob.onpe.sceorcbackend.model.enums.EstadoOficioEnum;
import pe.gob.onpe.sceorcbackend.model.enums.TransmisionNacionEnum;
import pe.gob.onpe.sceorcbackend.model.mapper.ActaOficioReporteMapper;
import pe.gob.onpe.sceorcbackend.model.mapper.SeguimientoOficioMapper;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.services.DetTipoEleccionDocumentoElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ActaCeleste;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CentroComputo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaOficio;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaResolucion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.JuradoElectoralEspecial;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Oficio;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ProcesoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabResolucion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaCelesteRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ArchivoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaFormatoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaOficioRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaResolucionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.JuradoElectoralEspecialRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.OficioRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.TabResolucionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ActaTransmisionExecuteService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.CentroComputoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.MaeProcesoElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.OficioService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ResolucionService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.StorageService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.utils.ActaOficioValidator;
import pe.gob.onpe.sceorcbackend.utils.ConstantesCatalogo;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoActa;
import pe.gob.onpe.sceorcbackend.utils.ConstantesEstadoResolucion;
import pe.gob.onpe.sceorcbackend.utils.ConstantesOficio;
import pe.gob.onpe.sceorcbackend.utils.OficioUtils;
import pe.gob.onpe.sceorcbackend.utils.PathUtils;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

@Service
@RequiredArgsConstructor
public class OficioServiceImpl implements OficioService {

    Logger logger = LoggerFactory.getLogger(OficioServiceImpl.class);

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final OficioRepository oficioRepository;
    private final DetActaOficioRepository detActaOficioRepository;
    private final ActaRepository actaRepository;
    private final ActaCelesteRepository actaCelesteRepository;
    private final MaeProcesoElectoralService procesoElectoralService;
    private final UtilSceService utilSceService;
    private final DetActaFormatoRepository detActaFormatoRepository;
    private final StorageService storageService;
    private final ActaTransmisionExecuteService actaTransmisionNacionStrategyService;
    private final CentroComputoService centroComputoService;
    private final ArchivoRepository archivoRepository;
    private final JuradoElectoralEspecialRepository juradoElectoralEspecialRepository;
    private final DetTipoEleccionDocumentoElectoralService detTipoEleccionDocumentoElectoralService;
    private final TabResolucionRepository tabResolucionRepository;
    private final ResolucionService resolucionService;
    private final DetActaResolucionRepository detActaResolucionRepository;

    private <T> GenericResponse<T> ok(String message, T data) {
        return new GenericResponse<>(true, message, data);
    }

    private <T> GenericResponse<T> fail(String message) {
        return new GenericResponse<>(false, message);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public GenericResponse<Object> generarOficio(TokenInfo tokenInfo, List<ActaBean> actaBeanList) throws IOException {
        List<DetActaOficioBean> actasValidas = ActaOficioValidator.filtrarActasValidas(actaBeanList, actaRepository,
                actaCelesteRepository, detActaFormatoRepository);
        if (actasValidas.isEmpty()) {
            String mensaje = actaBeanList.size() == 1 ? ConstantesOficio.MSG_ACTA_NO_VALIDA
                    : ConstantesOficio.MSG_ACTAS_NO_VALIDAS;
            return fail(mensaje);
        }
        Long actaId = actaBeanList.get(0).getActaId();

        Optional<DetActaOficio> detOficioOpt = detActaOficioRepository
                .findFirstByActa_IdOrderByFechaCreacionDesc(actaId);
        if (detOficioOpt.isPresent()) {
            Oficio oficio = detOficioOpt.get().getOficio();
            if (oficio != null && oficio.getArchivo() != null) {
                Long archivoId = oficio.getArchivo().getId();
                Archivo archivo = archivoRepository.findById(archivoId).orElse(null);
                if (archivo != null) {
                    String base64 = OficioUtils.convertToBase64(
                            PathUtils.normalizePath(this.storageService.getPathUpload(), archivo.getGuid()));
                    return ok(archivo.getNombre(), base64);
                } else {
                    return fail(ConstantesOficio.MSG_ARCHIVO_NO_ENCONTRADO);
                }
            } else {
                return fail(ConstantesOficio.MSG_OFICIO_SIN_ARCHIVO);
            }
        }

        ProcesoElectoral proceso = procesoElectoralService.findByActivo();
        Date fechaActual = new Date();

        Oficio oficio = crearOficio(tokenInfo, fechaActual);
        guardarDetalleActas(actasValidas, oficio, tokenInfo.getNombreUsuario(), fechaActual);

        List<ActaOficioBean> listaReporte = ActaOficioReporteMapper.construirListaReporte(actaBeanList, actasValidas);
        byte[] pdf = guardarOficioPdf(oficio, listaReporte, proceso, tokenInfo);

        return ok(oficio.getNombreOficio(), Base64.getEncoder().encodeToString(pdf));
    }

    private Oficio crearOficio(TokenInfo tokenInfo, Date fechaActual) {
        CentroComputo cc = obtenerCentroComputo(tokenInfo);
        tokenInfo.setCodigoAmbito(cc.getId().toString());
        Oficio oficio = Oficio.builder().nombreOficio(ConstantesComunes.VACIO).centroComputo(cc.getId().intValue())
                .estado(ConstantesOficio.ESTADO_OFICIO_PENDIENTE).activo(ConstantesComunes.ACTIVO)
                .usuarioCreacion(tokenInfo.getNombreUsuario()).fechaCreacion(fechaActual).build();

        oficio = oficioRepository.save(oficio);

        String numeroOficio = OficioUtils.generarNumeroOficio(String.format("%06d", oficio.getId()), tokenInfo);
        oficio.setNombreOficio(numeroOficio);
        return oficioRepository.save(oficio);
    }

    private void guardarDetalleActas(List<DetActaOficioBean> actas, Oficio oficio, String usuario, Date fecha) {
        for (DetActaOficioBean acta : actas) {
            DetActaOficio detalle = new DetActaOficio();
            detalle.setOficio(oficio);
            detalle.setActa(acta.getActaPlomo());
            detalle.setActaCeleste(acta.getActaCeleste());
            detalle.setCabActaFormato(acta.getCabActaFormato());
            detalle.setActivo(ConstantesComunes.ACTIVO);
            detalle.setUsuarioCreacion(usuario);
            detalle.setFechaCreacion(fecha);
            detActaOficioRepository.save(detalle);
        }
    }

    private byte[] guardarOficioPdf(Oficio oficio, List<ActaOficioBean> listaReporte, ProcesoElectoral proceso,
            TokenInfo tokenInfo) {
        byte[] pdf = generarOficioPDF(oficio, listaReporte, proceso, tokenInfo);

        if (pdf == null || pdf.length == 0) {
            throw new IllegalArgumentException(ConstantesOficio.MSG_ARCHIVO_NO_EXISTE);
        }

        String nombreArchivo = oficio.getNombreOficio().replaceAll("[^a-zA-Z0-9.-]", "_") + ".pdf";
        Archivo archivo = utilSceService.guardarArchivoPdf(pdf, nombreArchivo, tokenInfo);
        if (archivo == null) {
            throw new IllegalArgumentException(ConstantesOficio.MSG_ARCHIVO_NO_SAVE);
        }

        Date ahora = new Date();
        oficio.setArchivo(archivo);
        oficio.setUsuarioModificacion(tokenInfo.getNombreUsuario());
        oficio.setFechaModificacion(ahora);
        oficioRepository.save(oficio);
        return pdf;
    }

    private byte[] generarOficioPDF(Oficio oficio, List<ActaOficioBean> actas, ProcesoElectoral proceso,
            TokenInfo tokenInfo) {
        try {
            Map<String, Object> parametros = new HashMap<>();
            ClassLoader loader = getClass().getClassLoader();
            parametros.put(ConstantesComunes.REPORT_PARAM_URL_IMAGE, loader.getResourceAsStream(
                    ConstantesComunes.PATH_IMAGE_COMMON + ConstantesComunes.REPORT_PARAM_IMAGEN_ONPE));
            parametros.put(ConstantesComunes.REPORT_PARAM_PIXEL_TRANSPARENTE, loader.getResourceAsStream(
                    ConstantesComunes.PATH_IMAGE_COMMON + ConstantesComunes.REPORT_PARAM_IMAGEN_PIXEL_TRANSPARENTE));

            CentroComputo cc = obtenerCentroComputo(tokenInfo);
            JuradoElectoralEspecial jee = juradoElectoralEspecialRepository.findByCodigoCentroComputo(cc.getCodigo())
                    .orElseThrow(() -> new IllegalArgumentException(ConstantesOficio.MSG_NO_JEE + cc.getCodigo()));

            parametros.put(ConstantesComunes.REPORT_PARAM_SIN_VALOR_OFICIAL, utilSceService.getSinValorOficial());
            parametros.put(ConstantesComunes.OFICIO_FECHA, OficioUtils.obtenerFechaOficio(null));
            parametros.put(ConstantesComunes.OFICIO_NUMERO, oficio.getNombreOficio());

            String destinatario = String.join(" ", jee.getApellidoPaternoRepresentante(),
                    jee.getApellidoMaternoRepresentante(), jee.getNombresRepresentante()).toUpperCase();
            parametros.put(ConstantesComunes.OFICIO_DESTINATARIO, destinatario);
            parametros.put(ConstantesComunes.OFICIO_CARGO, "JURADO ELECTORAL ESPECIAL " + jee.getNombre());
            parametros.put(ConstantesComunes.OFICIO_DIRECCION, jee.getDireccion().toUpperCase());
            parametros.put(ConstantesComunes.OFICIO_ASUNTO,
                    ConstantesOficio.ASUNTO_OFICIO_BODY + " " + proceso.getAcronimo());
            parametros.put(ConstantesComunes.OFICIO_NOMBRE_PROCESO, proceso.getNombre());
            parametros.put(ConstantesComunes.OFICIO_CODIGO_VERIFICACION, "YUXFLJA");

            int cantidadActas = actas.size();
            parametros.put(ConstantesComunes.OFICIO_CANTIDAD_ACTAS, String.valueOf(cantidadActas));
            parametros.put(ConstantesComunes.OFICIO_CANTIDAD_TEXTO, OficioUtils.convertirNumeroATexto(cantidadActas));

            String reportePath = ConstantesComunes.OFICIO_ACTAS_OBSERVADAS_JRXML
                    + ConstantesComunes.EXTENSION_REPORTES_JASPER;

            return Funciones.generarReporte(this.getClass(), actas, reportePath, parametros);
        } catch (Exception e) {
            String errorMsg = String.format("%s | OficioId=%s | Proceso=%s | Usuario=%s",
                    ConstantesOficio.MSG_ERROR_PDF, oficio != null ? oficio.getId() : "null",
                    proceso != null ? proceso.getAcronimo() : "null",
                    tokenInfo != null ? tokenInfo.getNombreUsuario() : "null");
            logger.error(errorMsg, e);
            throw new IllegalStateException(errorMsg, e);
        }
    }

    @Override
    public void save(Oficio oficio) {
        this.oficioRepository.save(oficio);
    }

    @Override
    public void saveAll(List<Oficio> oficios) {
        this.oficioRepository.saveAll(oficios);
    }

    @Override
    public void deleteAll() {
        this.oficioRepository.deleteAll();
    }

    @Override
    public List<Oficio> findAll() {
        return this.oficioRepository.findAll();
    }

    @Override
    public GenericResponse<DigitizationGetFilesResponse> obtenerArchivosSobre(TokenInfo tokenInfo, ActaBean actaBean,
            String tipoSobre) {
        GenericResponse<DigitizationGetFilesResponse> response = new GenericResponse<>();
        try {
            DigitizationGetFilesResponse data = esSobrePlomo(tipoSobre) ? procesarSobrePlomo(actaBean, tipoSobre)
                    : procesarSobreCeleste(actaBean, tipoSobre);

            response.setSuccess(true);
            response.setData(data);
            return response;
        } catch (Exception e) {
            return fail(ConstantesOficio.MSG_ERROR_SOBRE + e.getMessage());
        }
    }

    private Archivo obtenerArchivoPorId(String idArchivo) {
        if (idArchivo == null)
            return null;
        try {
            return archivoRepository.findById(Long.valueOf(idArchivo)).orElse(null);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public GenericResponse<Boolean> transmitirOficio(Long idActa, String proceso, TransmisionNacionEnum estadoEnum,
            String usuario) {
        GenericResponse<Boolean> response = new GenericResponse<>();
        try {
            Optional<DetActaOficio> detOficioOpt = detActaOficioRepository
                    .findFirstByActa_IdOrderByFechaCreacionDesc(idActa);

            if (detOficioOpt.isEmpty()) {
                return fail(ConstantesOficio.MSG_OFICIO_ACTA_NO_ENCONTRADO + idActa);
            }

            Oficio oficio = detOficioOpt.get().getOficio();
            if (oficio == null) {
                return fail(ConstantesOficio.MSG_OFICIO_ACTA_NO_ENCONTRADO + idActa);
            }

            if (oficio.getEstado().equals(ConstantesEstadoActa.ESTADO_ACTA_ENVIADA_A_JEE)) {
                return fail(ConstantesOficio.MSG_OFICIO_ENVIADO);
            }

            Acta actaPloma = detOficioOpt.get().getActa();
            if (actaPloma == null) {
                return fail(ConstantesOficio.MSG_ACTA_NO_ENCONTRADA + idActa);
            }

            if (actaPloma.getEstadoActa().equals(ConstantesEstadoActa.ESTADO_ACTA_ENVIADA_A_JEE)) {
                return fail(ConstantesOficio.MSG_ACTA_ENVIADA);
            }

            Date fechaActual = new Date();

            oficio.setEstado(ConstantesEstadoActa.ESTADO_ACTA_ENVIADA_A_JEE);
            oficio.setFechaEnvio(fechaActual);
            oficio.setFechaModificacion(fechaActual);
            oficio.setUsuarioModificacion(usuario);
            oficioRepository.save(oficio);

            actaPloma.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_ENVIADA_A_JEE);
            actaPloma.setFechaModificacion(fechaActual);
            actaPloma.setUsuarioModificacion(usuario);
            actaRepository.save(actaPloma);

            actaTransmisionNacionStrategyService.sincronizar(idActa, proceso, estadoEnum, usuario);

            response.setSuccess(true);
            response.setData(true);
            response.setMessage(ConstantesOficio.MSG_OFICIO_EXITOSO);
        } catch (Exception ex) {
            return fail(ConstantesOficio.MSG_ERROR_TRANSMISION + ex.getMessage());
        }

        return response;
    }

    @Override
    public GenericResponse<Object> verificarDocumentoEnvio(TokenInfo tokenInfo, ActaBean actaBean,
            String tipoDocumento) {
        try {
            if (actaBean == null || actaBean.getActaId() == null) {
                return fail(ConstantesOficio.MSG_DATOS_INVALIDOS);
            }
            if (tipoDocumento.equals(ConstantesOficio.TIPO_DOCUMENTO_OFICIO)) {
                return verificarOficio(actaBean.getActaId());
            } else if (tipoDocumento.equals(ConstantesOficio.TIPO_DOCUMENTO_CARGO)) {
                return verificarCargo(actaBean.getActaId());
            } else if (tipoDocumento.equals(ConstantesOficio.TIPO_DOCUMENTO_RESOLUCION_JNE)) {
                return verificarResolucion(actaBean.getActaId());
            }
            return fail(ConstantesOficio.MSG_TIPO_DOC_INVALIDO);
        } catch (Exception e) {
            return fail(ConstantesOficio.MSG_ERROR_INTERNO);
        }
    }

    private GenericResponse<Object> verificarOficio(Long actaId) {
        return detActaOficioRepository.findFirstByActa_IdOrderByFechaCreacionDesc(actaId).map(det -> det.getOficio())
                .filter(ofi -> ofi != null && ofi.getArchivo() != null)
                .map(ofi -> cargarArchivoBase64(ofi.getArchivo().getId(), ConstantesOficio.MSG_ARCHIVO_NO_ENCONTRADO))
                .orElse(fail(ConstantesOficio.MSG_NO_OFICIO));
    }

    private GenericResponse<Object> verificarCargo(Long actaId) {
        return detActaFormatoRepository.findByActa_Id(actaId).stream()
                .filter(daf -> daf.getCabActaFormato() != null && daf.getCabActaFormato().getFormato() != null
                        && ConstantesCatalogo.N_CODIGO_CARGO_ENTREGA_ENVIO_JEE
                                .equals(daf.getCabActaFormato().getFormato().getTipoFormato())
                        && ConstantesComunes.ACTIVO.equals(daf.getActivo()))
                .findFirst().map(daf -> daf.getCabActaFormato().getArchivoFormatoPdf())
                .filter(archivo -> archivo != null)
                .map(archivo -> cargarArchivoBase64(archivo.getId(), ConstantesOficio.MSG_NO_ARCHIVO_CARGO))
                .orElse(fail(ConstantesOficio.MSG_NO_CARGO));
    }

    private GenericResponse<Object> verificarResolucion(Long actaId) {
        return detActaOficioRepository.findFirstByActa_IdOrderByFechaCreacionDesc(actaId)
                .map(DetActaOficio::getArchivoJNE).filter(Objects::nonNull)
                .map(jne -> cargarArchivoBase64(jne.getId(), ConstantesOficio.MSG_NO_ARCHIVO_RESOLUCION_JNE))
                .orElse(fail(ConstantesOficio.MSG_NO_RESOLUCION_JNE));
    }

    private GenericResponse<Object> cargarArchivoBase64(Long archivoId, String errorMsg) {
        Archivo archivo = archivoRepository.findById(archivoId).orElse(null);
        if (archivo != null) {
            try {
                String base64 = OficioUtils.convertToBase64(
                        PathUtils.normalizePath(this.storageService.getPathUpload(), archivo.getGuid()));
                return ok(archivo.getNombre(), base64);
            } catch (IOException e) {
                logger.error(ConstantesOficio.MSG_ERROR_BASE64, e);
                return fail(ConstantesOficio.MSG_ERROR_BASE64);
            }
        } else {
            return fail(errorMsg);
        }
    }

    @Override
    public List<SeguimientoOficioDTO> obtenerSeguimiento(TokenInfo tokenInfo) {
        List<SeguimientoOficioDTO> lista = new ArrayList<>();
        try {
            CentroComputo cc = obtenerCentroComputo(tokenInfo);
            List<Oficio> oficios = oficioRepository.findByCentroComputo(cc.getId().intValue());

            for (Oficio oficio : oficios) {
                DetActaOficio detalle = detActaOficioRepository.findByOficio_Id(oficio.getId()).stream().findFirst()
                        .orElse(null);

                if (detalle == null)
                    continue;

                lista.add(SeguimientoOficioMapper.build(oficio, detalle, detTipoEleccionDocumentoElectoralService));
            }
            return lista;
        } catch (Exception e) {
            logger.error(ConstantesOficio.MSG_ERROR_SEGUIMIENTO, e);
            return lista;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GenericResponse<Object> procesarResolucion(ProcesarResolucionRequest request, TokenInfo tokenInfo) {
        if (request == null || request.getSeguimiento() == null) {
            return new GenericResponse<>(false, "Request inválido", null);
        }

        final AccionResolucionJEEnum accion;
        try {
            accion = AccionResolucionJEEnum.valueOf(request.getAccion());
        } catch (Exception e) {
            return new GenericResponse<>(false, "Acción inválida", null);
        }

        try {
            SeguimientoOficioDTO seguimiento = request.getSeguimiento();
            Integer idOficio = Math.toIntExact(seguimiento.getIdOficio());

            Oficio oficio = oficioRepository.findById(idOficio)
                    .orElseThrow(() -> new BadRequestException(ConstantesOficio.MSG_OFICIO_NO_ENCONTRADO));

            DetActaOficio detOficio = detActaOficioRepository.findByOficio_Id(oficio.getId()).stream().findFirst()
                    .orElseThrow(() -> new BadRequestException(ConstantesOficio.MSG_NO_DET_OFICIO));

            if (AccionResolucionJEEnum.APROBAR.equals(accion)) {
                Archivo archivoResolucion = obtenerArchivoPorId(seguimiento.getArchivoJNE());
                if (archivoResolucion == null) {
                    throw new BadRequestException(ConstantesOficio.MSG_NO_ARCHIVO_RESOLUCION_JNE);
                }

                Resource recurso = this.storageService.loadFile(archivoResolucion.getGuid(), false);
                File resolucionFile = recurso.getFile();

                int paginas = OficioUtils.obtenerNumeroPaginas(resolucionFile);

                String usuario = tokenInfo.getNombreUsuario();

                TabResolucion resolucionJEE = digitalizarResolucion(usuario, seguimiento.getNumeroResolucion(), paginas,
                        archivoResolucion);
                resolucionJEE.setEstadoDigitalizacion(ConstantesEstadoResolucion.DIGTAL_APROBADO);
                resolucionJEE.setEstadoResolucion(ConstantesEstadoResolucion.EN_PROCESO);
                resolucionJEE.setProcedencia(ConstantesCatalogo.CATALOGO_PROCEDENCIA_JEE_COD);
                resolucionJEE.setNumeroExpediente(seguimiento.getNumeroExpediente());
                resolucionJEE.setNumeroResolucion(seguimiento.getNumeroResolucion());
                resolucionJEE.setFechaResolucion(seguimiento.getFechaRespuesta());
                resolucionJEE.setTipoResolucion(ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_ENVIADAS_A_JEE);
                resolucionJEE.setAudFechaModificacion(new Date());
                resolucionJEE.setAudUsuarioModificacion(tokenInfo.getNombreUsuario());
                tabResolucionRepository.save(resolucionJEE);

                GenericResponse<Object> resp = registrarAsociacionConActas(resolucionJEE, oficio, tokenInfo);
                if (!resp.isSuccess()) {
                    throw new BadRequestException(resp.getMessage());
                }
                detOficio.setResolucion(resolucionJEE);
                oficio.setEstado(EstadoOficioEnum.RESOLUCION_GENERADA.getCodigo());
            } else if (AccionResolucionJEEnum.RECHAZAR.equals(accion)) {
                oficio.setEstado(EstadoOficioEnum.RECHAZADO_ONPE.getCodigo());
                detOficio.setObservacion(request.getObservacion());
            }
            detActaOficioRepository.save(detOficio);
            oficioRepository.save(oficio);
            return new GenericResponse<>(true, "Operación realizada correctamente", null);
        } catch (Exception e) {
            logger.error(ConstantesOficio.MSG_ERROR_INTERNO, e);
            return new GenericResponse<>(false, ConstantesOficio.MSG_ERROR_INTERNO, null);
        }
    }

    private TabResolucion digitalizarResolucion(String usuario, String numeroResolucion, Integer numeroPaginas,
            Archivo archivo) {

        this.utilSceService.validarNumeroResolucion(numeroResolucion);

        Optional<TabResolucion> tabResolucionOptional = this.tabResolucionRepository
                .findByNumeroResolucionAndActivoAndEstadoResolucionNot(numeroResolucion, ConstantesComunes.ACTIVO,
                        ConstantesEstadoResolucion.ANULADO)
                .stream().findFirst();

        if (tabResolucionOptional.isPresent()) {
            throw new BadRequestException(
                    String.format(ConstantesOficio.MSG_NUMERO_RESOLUCION_JNE_FOUND, numeroResolucion));
        }

        archivo.setNombreOriginal(numeroResolucion);
        archivo.setNombre(numeroResolucion + ".PDF");
        archivoRepository.save(archivo);

        return this.resolucionService.saveResolucion(numeroResolucion, numeroPaginas, archivo, usuario);
    }

    private CentroComputo obtenerCentroComputo(TokenInfo tokenInfo) {
        String codigoCC = tokenInfo.getCodigoCentroComputo();
        return centroComputoService.findByCodigo(codigoCC)
                .orElseThrow(() -> new IllegalArgumentException(ConstantesOficio.MSG_NO_CC + codigoCC));
    }

    private DigitizationGetFilesResponse procesarSobrePlomo(ActaBean actaBean, String tipoSobre) throws IOException {

        return actaBean.isStaeIntegrada() ? procesarPlomoStae(actaBean, tipoSobre)
                : procesarPlomoNormal(actaBean, tipoSobre);
    }

    private boolean esSobrePlomo(String tipoSobre) {
        return ConstantesOficio.TIPO_SOBRE_PLOMO.equalsIgnoreCase(tipoSobre);
    }

    private DigitizationGetFilesResponse procesarPlomoStae(ActaBean actaBean, String tipoSobre) throws IOException {

        Archivo escrutinio = obtenerArchivoPorId(actaBean.getIdArchivoEscrutinioFirmado());
        Archivo instalacion = obtenerArchivoPorId(actaBean.getIdArchivoInstalacionFirmado());
        Archivo sufragio = obtenerArchivoPorId(actaBean.getIdArchivoSufragioFirmado());

        if (escrutinio == null || instalacion == null || sufragio == null) {
            throw new IllegalArgumentException(
                    ConstantesOficio.MSG_SOBRE_NO_ENCONTRADO_STAE + tipoSobre.toLowerCase() + ".");
        }

        DigitizationGetFilesResponse data = new DigitizationGetFilesResponse();
        data.setActa1File(toBase64(escrutinio));
        data.setActa2File(toBase64(instalacion));
        data.setActa3File(toBase64(sufragio));

        return data;
    }

    private DigitizationGetFilesResponse procesarPlomoNormal(ActaBean actaBean, String tipoSobre) throws IOException {
        Archivo escrutinio = obtenerArchivoPorId(actaBean.getIdArchivoEscrutinio());
        Archivo instalacion = obtenerArchivoPorId(actaBean.getIdArchivoInstalacionSufragio());

        if (escrutinio == null || instalacion == null) {
            throw new IllegalArgumentException(
                    ConstantesOficio.MSG_SOBRE_NO_ENCONTRADO + tipoSobre.toLowerCase() + ".");
        }

        DigitizationGetFilesResponse data = new DigitizationGetFilesResponse();
        data.setActa1File(toBase64(convertirArchivo(escrutinio)));
        data.setActa2File(toBase64(convertirArchivo(instalacion)));
        data.setActa3File(null);

        return data;
    }

    private DigitizationGetFilesResponse procesarSobreCeleste(ActaBean actaBean, String tipoSobre) throws IOException {

        ActaCeleste celeste = actaCelesteRepository
                .findByActa_IdAndEstadoDigitalizacion(actaBean.getActaId(),
                        ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_ACEPTADA)
                .orElseThrow(() -> new IllegalArgumentException(
                        ConstantesOficio.MSG_SOBRE_NO_ENCONTRADO + tipoSobre.toLowerCase() + "."));

        Archivo escrutinio = celeste.getArchivoEscrutinio();
        Archivo instalacion = celeste.getArchivoInstalacionSufragio();

        if (escrutinio == null || instalacion == null) {
            throw new IllegalArgumentException(
                    ConstantesOficio.MSG_SOBRE_NO_ENCONTRADO + tipoSobre.toLowerCase() + ".");
        }

        DigitizationGetFilesResponse data = new DigitizationGetFilesResponse();
        data.setActa1File(toBase64(convertirArchivo(escrutinio)));
        data.setActa2File(toBase64(convertirArchivo(instalacion)));
        data.setActa3File(null);

        return data;
    }

    private GenericResponse<Object> registrarAsociacionConActas(TabResolucion resolucionJEE, Oficio oficio,
            TokenInfo tokenInfo) {
        ResolucionAsociadosRequest asociacionReq = new ResolucionAsociadosRequest();
        asociacionReq.setId(resolucionJEE.getId());
        asociacionReq.setNumeroResolucion(resolucionJEE.getNumeroResolucion());
        asociacionReq.setNumeroExpediente(resolucionJEE.getNumeroExpediente());
        asociacionReq.setFechaResolucion(resolucionJEE.getFechaResolucion());
        asociacionReq.setTipoResolucion(resolucionJEE.getTipoResolucion());
        asociacionReq.setProcedencia(resolucionJEE.getProcedencia());

        List<ActaBean> actas = obtenerActasDesdeOficio(oficio);
        asociacionReq.setActasAsociadas(actas);
        updateEstadoActaAsociada(resolucionJEE, tokenInfo.getNombreUsuario());
        GenericResponse<Object> response = resolucionService.registrarAsociacionConActas(tokenInfo, asociacionReq, null, null, null);

        if (!response.isSuccess()) {
            return response;
        }

        List<Long> actaIds = response.getActasId();
        if (actaIds != null && !actaIds.isEmpty()) {
            this.actaTransmisionNacionStrategyService.sincronizar(actaIds, tokenInfo.getAbrevProceso(),
                    TransmisionNacionEnum.ASOCIACION_RESOL_TRANSMISION, tokenInfo.getNombreUsuario());
        }

        return response;
    }

    private void updateEstadoActaAsociada(TabResolucion tabResolucion, String usuario) {
        List<DetActaResolucion> anteriores = detActaResolucionRepository.findByResolucion(tabResolucion);
        for (DetActaResolucion detalle : anteriores) {
            Acta acta = detalle.getActa();
            acta.setEstadoActa(ConstantesEstadoActa.ESTADO_ACTA_ACTA_DEVUELTA);
            acta.setUsuarioModificacion(usuario);
            acta.setFechaModificacion(new Date());

            this.actaRepository.save(acta);
        }

    }

    private List<ActaBean> obtenerActasDesdeOficio(Oficio oficio) {
        return detActaOficioRepository.findByOficio_Id(oficio.getId()).stream().map(det -> {
            ActaBean acta = new ActaBean();
            acta.setActaId(det.getActa().getId());
            acta.setEstadoActa(det.getActa().getEstadoActa());
            acta.setEstadoResolucion(det.getActa().getEstadoActaResolucion());
            acta.setMesa(det.getActa().getMesa().getCodigo());
            acta.setCopia(det.getActa().getNumeroCopia());
            return acta;
        }).toList();
    }

    private File convertirArchivo(Archivo archivo) throws IOException {
        Resource recurso = this.storageService.loadFile(archivo.getGuid(), false);
        File tiff = recurso.getFile();
        String nombrePdf = archivo.getNombre().replaceAll("\\.\\w+$", "") + ".pdf";
        return storageService.convertTIFFToPDF(tiff, nombrePdf);
    }

    private String toBase64(File file) throws IOException {
        return Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
    }

    private String toBase64(Archivo archivo) throws IOException {
        Resource recurso = this.storageService.loadFile(archivo.getGuid(), false);
        File file = recurso.getFile();
        return Base64.getEncoder().encodeToString(Files.readAllBytes(file.toPath()));
    }
}
