package pe.gob.onpe.sceorcbackend.model.service.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.sceorcbackend.exception.BadRequestException;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.verification.BarCodeInfo;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DetTipoEleccionDocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.services.DocumentoElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.digitalizacion.DigtalDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.ActaCelesteInfo;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.ActaInfo;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.MesaInfo;
import pe.gob.onpe.sceorcbackend.model.service.DigitalizacionService;
import pe.gob.onpe.sceorcbackend.utils.*;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class DigitalizacionServiceImpl implements DigitalizacionService {

    private static final Logger logger = LoggerFactory.getLogger(DigitalizacionServiceImpl.class);

    private final UtilSceService utilSceService;
    private final DetOtroDocumentoRepository detOtroDocumentoRepository;
    private final DocumentoElectoralService adminDocumentoElectoralService;
    private final MesaDocumentoRepository mesaDocumentoRepository;
    private final MesaRepository mesaRepository;
    private final ITabLogService logService;
    private final ArchivoService archivoService;
    private final DetActaAccionService detActaAccionService;
    private final ActaRepository actaRepository;
    private final DetActaResolucionRepository detActaResolucionRepository;

    public DigitalizacionServiceImpl(UtilSceService  utilSceService,
                                     DetOtroDocumentoRepository detOtroDocumentoRepository,
                                     MesaRepository mesaRepository,
                                     DocumentoElectoralService adminDocumentoElectoralService,
                                     MesaDocumentoRepository mesaDocumentoRepository,
                                     ITabLogService logService, ArchivoService archivoService,
                                     DetActaAccionService detActaAccionService,
                                     ActaRepository actaRepository,
                                     DetActaResolucionRepository detActaResolucionRepository) {

        this.utilSceService = utilSceService;
        this.detOtroDocumentoRepository = detOtroDocumentoRepository;
        this.mesaDocumentoRepository = mesaDocumentoRepository;
        this.adminDocumentoElectoralService = adminDocumentoElectoralService;
        this.mesaRepository = mesaRepository;
        this.logService = logService;
        this.archivoService = archivoService;
        this.detActaAccionService = detActaAccionService;
        this.actaRepository = actaRepository;
        this.detActaResolucionRepository = detActaResolucionRepository;
    }

    @Override
    @Transactional
    public void digitalizarListaElectores(MultipartFile file, String nroMesa, TokenInfo tokenInfo) {

        this.utilSceService.validarNumeroMesa(nroMesa);
        String mimeType = this.utilSceService.validarArchivoEscaneado(file, ConstantesComunes.CONTROL_CALIDAD_TIPO_DOCUMENTO_LISTA_ELECTORES, nroMesa);
        MesaInfo mesaInfo = this.utilSceService.validarMesa(nroMesa);
        Mesa mesa = mesaInfo.getMesa();
        this.utilSceService.validarMesaNoInstalada(mesa);
        MesaUtils.validateEstadoDigitalizacionPorTipoDocumento(mesa, ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES);
        boolean isPerdidaParcial = DenunciaUtils.validarDenunciaPerdidaParcial(nroMesa,  this.detOtroDocumentoRepository.findByNroMesaAndTipoDocumento(nroMesa, ConstantesOtrosDocumentos.TIPO_DOC_LISTA_ELECTORES));
        crearDocumentosLeMesa(file, mesa, tokenInfo, mimeType, isPerdidaParcial);
    }

    @Override
    @Transactional
    public void digitalizarMiembrosDeMesa(MultipartFile file, String nroMesa, TokenInfo tokenInfo) {
        this.utilSceService.validarNumeroMesa(nroMesa);
        String mimeType =this.utilSceService.validarArchivoEscaneado(file, ConstantesComunes.CONTROL_CALIDAD_TIPO_DOCUMENTO_HOJA_ASISTENCIA, nroMesa);
        MesaInfo mesaInfo = this.utilSceService.validarMesa(nroMesa);
        Mesa mesa = mesaInfo.getMesa();
        this.utilSceService.validarMesaNoInstalada(mesa);
        MesaUtils.validateEstadoDigitalizacionPorTipoDocumento(mesa, ConstantesComunes.ABREV_DOCUMENT_HOJA_DE_CONTROL_DE_ASISTENCIA_MIEMBROS_MESA);
        crearDocumentosMmMesa(file, mesa, tokenInfo, mimeType);
    }

    @Override
    @Transactional
    public DigtalDTO digitalizarActa(MultipartFile file, String codeBar, TokenInfo tokenInfo) {

        logger.info("[DIGTAL] Inicio digitalizacion acta codeBar={}, usuario={}, cc={}", codeBar, tokenInfo.getNombreUsuario(), tokenInfo.getCodigoCentroComputo());

        this.utilSceService.validarCodigoBarrasActas(codeBar);

        this.utilSceService.validarArchivoEscaneado(file, ConstantesComunes.CONTROL_CALIDAD_TIPO_DOCUMENTO_ACTA, ConstantesComunes.VACIO);

        ActaInfo actaInfo = this.utilSceService.validarActa(codeBar, tokenInfo.getCodigoCentroComputo(),Boolean.TRUE);
        Mesa mesa = actaInfo.getMesa();
        BarCodeInfo barcodeInfo = actaInfo.getBarCodeInfo();
        // Bloquear acta para evitar procesamiento paralelo (espera si otra transaccion la tiene bloqueada)
        Acta acta = this.actaRepository.findByIdForUpdate(actaInfo.getActa().getId())
                .orElseThrow(() -> new IllegalArgumentException("El acta no está registrada en BD."));
        this.utilSceService.validarMesaNoInstalada(mesa);

        logger.info("[DIGTAL] Acta validada actaId={}, mesa={}, estadoActa={}, estadoDigtal={}", acta.getId(), mesa.getCodigo(), acta.getEstadoActa(), acta.getEstadoDigitalizacion());

        Long tipoDocumento = determinarTipoDocumento(actaInfo.getConfigAis(), barcodeInfo);

        logger.info("[DIGTAL] Tipo documento determinado: {} (AE={}, AIS={}), actaId={}", tipoDocumento, ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AE, ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AIS, acta.getId());

        if (!SceUtils.isValidCheckDigit(barcodeInfo, tipoDocumento, actaInfo.getConfigAe(), actaInfo.getConfigAis())) {
            String descTipoActa = Objects.equals(tipoDocumento, ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AE)
                    ? "acta de escrutinio"
                    : "acta de instalación y sufragio";

            throw new BadRequestException(String.format("Dígito de chequeo inválido para el %s %s.", descTipoActa, barcodeInfo.getCodigoBarra()));
        }

        ActaUtils.validarSolucionTecnologica(actaInfo);

        ActaUtils.validarCopiaEnAeAisParaConvencionalesContingencia(acta, actaInfo);

        //REDIGITALIZACIÓN: si al menos uno de los campos de redigitalización es > 0, se usa flujo separado
        boolean esRedigitalizacion = (acta.getRedigtalEscrutinio() != null && acta.getRedigtalEscrutinio() > 0)
                || (acta.getRedigtalInstalacionSufragio() != null && acta.getRedigtalInstalacionSufragio() > 0);

        if (esRedigitalizacion) {
            return redigitalizarActa(file, acta, tipoDocumento, barcodeInfo, tokenInfo);
        }
        //VALIDACIÓN EXTRAVIADA/SINIESTRADA
        validarActaExtraviadaSiniestrada(actaInfo);

        ActaUtils.validarEstadoDigitalizacionActa(actaInfo, tipoDocumento);

        Integer tipoDocumentoPr = ConstantesCatalogo.CATALOGO_CODIGO_DOC_ELECTORAL_PR_ACTA_ESCRUTINIO;
        if(tipoDocumento.equals(ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AIS)){
            tipoDocumentoPr = ConstantesCatalogo.CATALOGO_CODIGO_DOC_ELECTORAL_PR_ACTA_INSTALACION_SUFRAGIO;
        }

        Archivo archivo = this.archivoService.guardarArchivo(file, tokenInfo.getNombreUsuario(), tokenInfo.getCodigoCentroComputo(), Optional.of(tipoDocumentoPr));
        logger.info("[DIGTAL] Archivo guardado archivoId={}, actaId={}", archivo.getId(), acta.getId());

        guardarActa(acta, tipoDocumento, barcodeInfo.getNroCopia(), archivo, barcodeInfo.getDigitoChequeo(), tokenInfo.getNombreUsuario(),
                tokenInfo.getCodigoCentroComputo());

        logger.info("[DIGTAL] Acta actualizada actaId={}, archivoEscrutinio={}, archivoAIS={}", acta.getId(), acta.getArchivoEscrutinio() != null ? acta.getArchivoEscrutinio().getId() : null, acta.getArchivoInstalacionSufragio() != null ? acta.getArchivoInstalacionSufragio().getId() : null);

        actualizarMesaInstalada(acta, tokenInfo.getNombreUsuario());

        registrarLogDigitalizacionUploadActa(tipoDocumento, barcodeInfo.getCodigoBarra(), tokenInfo);

        if(acta.getArchivoEscrutinio()!=null && acta.getArchivoInstalacionSufragio()!=null) {
            logger.info("[DIGTAL] Acta completa (AE+AIS), enviando a cola. actaId={}, archivoAE={}, archivoAIS={}", acta.getId(), acta.getArchivoEscrutinio().getId(), acta.getArchivoInstalacionSufragio().getId());
            DigtalDTO digtalDTO = new DigtalDTO();
            digtalDTO.setIdArchivo(acta.getArchivoEscrutinio().getId());
            digtalDTO.setIdActa(acta.getId());
            digtalDTO.setTipoArchivo(ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AE);
            digtalDTO.setIdArchivoAis(acta.getArchivoInstalacionSufragio().getId());
            digtalDTO.setTipoArchivoAis(ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AIS);
            digtalDTO.setCompleto(Boolean.TRUE);
            return digtalDTO;
        }else{
            logger.info("[DIGTAL] Acta parcial (falta AE o AIS), no se envia a cola. actaId={}", acta.getId());
            return null;
        }
    }



    /**
     * Flujo de redigitalización de acta.
     * Inactiva el archivo anterior (renombrando su GUID con _old), guarda el nuevo archivo
     * con el GUID original, y actualiza el estado de redigitalización en el acta.
     * Solo marca redigitalizado=true cuando ambos documentos (AE y AIS) están en estado 2.
     */
    private DigtalDTO redigitalizarActa(MultipartFile file, Acta acta, Long tipoDocumento,
                                         BarCodeInfo barcodeInfo, TokenInfo tokenInfo) {

        boolean esAE = Objects.equals(tipoDocumento, ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AE);

        logger.info("[DIGTAL][REDIGTAL] Flujo de redigitalización actaId={}, esAE={}, nRedigtalEscrutinio={}, nRedigtalAIS={}",
                acta.getId(), esAE, acta.getRedigtalEscrutinio(), acta.getRedigtalInstalacionSufragio());

        Integer tipoDocumentoPr = ConstantesCatalogo.CATALOGO_CODIGO_DOC_ELECTORAL_PR_ACTA_ESCRUTINIO;
        if (tipoDocumento.equals(ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AIS)) {
            tipoDocumentoPr = ConstantesCatalogo.CATALOGO_CODIGO_DOC_ELECTORAL_PR_ACTA_INSTALACION_SUFRAGIO;
        }

        // Obtener archivo anterior
        Archivo archivoAnterior = esAE ? acta.getArchivoEscrutinio() : acta.getArchivoInstalacionSufragio();
        String guidOriginal = null;

        if (archivoAnterior != null) {
            guidOriginal = archivoAnterior.getGuid();
            // Calcular siguiente número incremental: _old_1, _old_2, _old_3...
            long countOld = this.archivoService.contarArchivosOld(guidOriginal);
            String guidOld = guidOriginal + "_old_" + (countOld + 1);
            logger.info("[DIGTAL][REDIGTAL] Inactivando archivo anterior archivoId={}, guidOriginal={}, guidOld={}",
                    archivoAnterior.getId(), guidOriginal, guidOld);
            archivoAnterior.setGuid(guidOld);
            archivoAnterior.setActivo(ConstantesComunes.INACTIVO);
            archivoAnterior.setUsuarioModificacion(tokenInfo.getNombreUsuario());
            archivoAnterior.setFechaModificacion(new Date());
            this.archivoService.save(archivoAnterior);
            this.archivoService.renameFile(guidOriginal, guidOld);
        }

        // Guardar nuevo archivo
        Archivo archivoNuevo = this.archivoService.guardarArchivo(file, tokenInfo.getNombreUsuario(),
                tokenInfo.getCodigoCentroComputo(), Optional.of(tipoDocumentoPr));
        logger.info("[DIGTAL][REDIGTAL] Nuevo archivo guardado archivoId={}, actaId={}", archivoNuevo.getId(), acta.getId());

        // Chancar GUID: el nuevo archivo toma el GUID original
        if (guidOriginal != null) {
            this.archivoService.renameFile(archivoNuevo.getGuid(), guidOriginal);
            archivoNuevo.setGuid(guidOriginal);
            // Actualizar también el guid dentro de la metadata
            if (archivoNuevo.getMetadata() != null) {
                archivoNuevo.getMetadata().put("guid", guidOriginal);
            }
            this.archivoService.save(archivoNuevo);
            logger.info("[DIGTAL][REDIGTAL] GUID del nuevo archivo reemplazado a guidOriginal={}", guidOriginal);
        }

        // Setear nuevo archivo en el acta y marcar como redigitalizado (2)
        if (esAE) {
            acta.setArchivoEscrutinio(archivoNuevo);
            acta.setRedigtalEscrutinio((short) 2);
        } else {
            acta.setArchivoInstalacionSufragio(archivoNuevo);
            acta.setRedigtalInstalacionSufragio((short) 2);
        }
        acta.setUsuarioModificacion(tokenInfo.getNombreUsuario());
        acta.setFechaModificacion(new Date());
        this.utilSceService.guardarActa(acta);

        logger.info("[DIGTAL][REDIGTAL] Acta actualizada actaId={}, nRedigtalEscrutinio={}, nRedigtalAIS={}",
                acta.getId(), acta.getRedigtalEscrutinio(), acta.getRedigtalInstalacionSufragio());

        actualizarMesaInstalada(acta, tokenInfo.getNombreUsuario());
        registrarLogDigitalizacionUploadActa(tipoDocumento, barcodeInfo.getCodigoBarra(), tokenInfo);

        // Armar respuesta: transmitir solo cuando ambos campos están en 2
        boolean ambosRedigitalizados = acta.getArchivoEscrutinio() != null && acta.getArchivoInstalacionSufragio() != null
                && acta.getRedigtalEscrutinio() != null && acta.getRedigtalEscrutinio() == 2
                && acta.getRedigtalInstalacionSufragio() != null && acta.getRedigtalInstalacionSufragio() == 2;

        if (ambosRedigitalizados) {
            logger.info("[DIGTAL][REDIGTAL] Ambos documentos redigitalizados, enviando a cola. actaId={}", acta.getId());
            DigtalDTO digtalDTO = new DigtalDTO();
            digtalDTO.setIdArchivo(acta.getArchivoEscrutinio().getId());
            digtalDTO.setIdActa(acta.getId());
            digtalDTO.setTipoArchivo(ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AE);
            digtalDTO.setIdArchivoAis(acta.getArchivoInstalacionSufragio().getId());
            digtalDTO.setTipoArchivoAis(ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AIS);
            digtalDTO.setCompleto(Boolean.TRUE);
            digtalDTO.setRedigitalizado(Boolean.TRUE);
            return digtalDTO;
        } else {
            logger.info("[DIGTAL][REDIGTAL] Redigitalización parcial, no se envía a cola. actaId={}, nRedigtalEscrutinio={}, nRedigtalAIS={}",
                    acta.getId(), acta.getRedigtalEscrutinio(), acta.getRedigtalInstalacionSufragio());
            return null;
        }
    }

    @Override
    @Transactional
    public DigtalDTO digitalizarActaCeleste(MultipartFile file, String codeBar, TokenInfo tokenInfo) {

    	this.utilSceService.validarCodigoBarrasActas(codeBar);
    	this.utilSceService.validarArchivoEscaneado(file, ConstantesComunes.CONTROL_CALIDAD_TIPO_DOCUMENTO_ACTA, ConstantesComunes.VACIO);
    	
        ActaCelesteInfo actaInfo = this.utilSceService.validarActaCeleste(codeBar, tokenInfo.getCodigoCentroComputo(), tokenInfo.getNombreUsuario());
        Mesa mesa = actaInfo.getMesa();
        BarCodeInfo barcodeInfo = actaInfo.getBarCodeInfo();
        this.utilSceService.validarMesaNoInstalada(mesa);

        Long tipoDocumento = determinarTipoDocumento(actaInfo.getConfigAis(), barcodeInfo);

        if (!SceUtils.isValidCheckDigit(barcodeInfo, tipoDocumento, actaInfo.getConfigAe(), actaInfo.getConfigAis())) {
            String descTipoActa = Objects.equals(tipoDocumento, ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AE)
                    ? "acta de escrutinio"
                    : "acta de instalación y sufragio";

            throw new BadRequestException(String.format("Dígito de chequeo inválido para el %s %s.", descTipoActa, barcodeInfo.getCodigoBarra()));
        }

        validarEstadoActaCeleste(actaInfo, tipoDocumento);
        
        Integer tipoDocumentoPr = ConstantesCatalogo.CATALOGO_CODIGO_DOC_ELECTORAL_PR_ACTA_ESCRUTINIO;
        if(tipoDocumento.equals(ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AIS)){
            tipoDocumentoPr = ConstantesCatalogo.CATALOGO_CODIGO_DOC_ELECTORAL_PR_ACTA_INSTALACION_SUFRAGIO;
        }

        Archivo archivo = this.archivoService.guardarArchivo(file, tokenInfo.getNombreUsuario(), tokenInfo.getCodigoCentroComputo(), Optional.of(tipoDocumentoPr));
        ActaCeleste actaCeleste = guardarActaCeleste(actaInfo.getActaCeleste(), tipoDocumento, barcodeInfo.getNroCopia(), archivo, barcodeInfo.getDigitoChequeo(), tokenInfo.getNombreUsuario());

        regLogDigitalizacionActaCeleste(tipoDocumento, barcodeInfo.getCodigoBarra(), tokenInfo);

        if (actaCeleste.getArchivoEscrutinio() != null && actaCeleste.getArchivoInstalacionSufragio() != null) {
            DigtalDTO dto = new DigtalDTO();
            dto.setIdActa(actaCeleste.getId());
            dto.setIdArchivo(actaCeleste.getArchivoEscrutinio().getId());
            dto.setTipoArchivo(ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AE);
            dto.setIdArchivoAis(actaCeleste.getArchivoInstalacionSufragio().getId());
            dto.setTipoArchivoAis(ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AIS);
            dto.setCompleto(Boolean.TRUE);
            return dto;
        }

        return null;
    }
    
    @Override
    @Transactional
    public DigtalDTO digitalizarActaExtranjero(MultipartFile tif, MultipartFile pdf, String codeBar, TokenInfo tokenInfo) {

        this.utilSceService.validarCodigoBarrasActas(codeBar);

        this.utilSceService.validarArchivoEscaneado(tif, ConstantesComunes.CONTROL_CALIDAD_TIPO_DOCUMENTO_ACTA, ConstantesComunes.VACIO);

        ActaInfo actaInfo = this.utilSceService.validarActa(codeBar, tokenInfo.getCodigoCentroComputo(),Boolean.TRUE);
        Mesa mesa = actaInfo.getMesa();
        BarCodeInfo barcodeInfo = actaInfo.getBarCodeInfo();
        Acta acta = actaInfo.getActa();
        this.utilSceService.validarMesaNoInstalada(mesa);

        Long tipoDocumento = determinarTipoDocumento(actaInfo.getConfigAis(), barcodeInfo);

        if (!SceUtils.isValidCheckDigit(barcodeInfo, tipoDocumento, actaInfo.getConfigAe(), actaInfo.getConfigAis())) {
            String descTipoActa = Objects.equals(tipoDocumento, ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AE)
                    ? "acta de escrutinio"
                    : "acta de instalación y sufragio";

            throw new BadRequestException(String.format("Dígito de chequeo inválido para el %s %s.", descTipoActa, barcodeInfo.getCodigoBarra()));
        }
        
        ActaUtils.validarSolucionTecnologica(actaInfo);
        ActaUtils.validarEstadoDigitalizacionActa(actaInfo, tipoDocumento);

        Integer tipoDocumentoPr = ConstantesCatalogo.CATALOGO_CODIGO_DOC_ELECTORAL_PR_ACTA_ESCRUTINIO;
        if(tipoDocumento.equals(ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AIS)){
            tipoDocumentoPr = ConstantesCatalogo.CATALOGO_CODIGO_DOC_ELECTORAL_PR_ACTA_INSTALACION_SUFRAGIO;
        }

        Archivo archivoTif = this.archivoService.guardarArchivo(tif, tokenInfo.getNombreUsuario(), tokenInfo.getCodigoCentroComputo(), Optional.of(tipoDocumentoPr));
        Archivo archivoPdf = this.archivoService.guardarArchivo(pdf, tokenInfo.getNombreUsuario(), tokenInfo.getCodigoCentroComputo(), Optional.of(tipoDocumentoPr));
        guardarActaExtranjero(acta, tipoDocumento, barcodeInfo.getNroCopia(), archivoTif, archivoPdf, barcodeInfo.getDigitoChequeo(), tokenInfo.getNombreUsuario(),
                tokenInfo.getCodigoCentroComputo());
        actualizarMesaInstalada(acta, tokenInfo.getNombreUsuario());

        registrarLogDigitalizacionUploadActa(tipoDocumento, barcodeInfo.getCodigoBarra(), tokenInfo);

        if(acta.getArchivoEscrutinio()!=null && acta.getArchivoInstalacionSufragio()!=null) {
            DigtalDTO digtalDTO = new DigtalDTO();
            digtalDTO.setIdArchivo(acta.getArchivoEscrutinio().getId());
            digtalDTO.setIdActa(acta.getId());
            digtalDTO.setTipoArchivo(ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AE);
            digtalDTO.setIdArchivoAis(acta.getArchivoInstalacionSufragio().getId());
            digtalDTO.setTipoArchivoAis(ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AIS);
            digtalDTO.setCompleto(Boolean.TRUE);
            return digtalDTO;
        }else{
            return null;
        }
    }

    private void validarActaExtraviadaSiniestrada(ActaInfo actaInfo) {

        String estadoActa = actaInfo.getActa().getEstadoActa();

        boolean esExtraviada = ConstantesEstadoActa.ESTADO_ACTA_EXTRAVIADA.equals(estadoActa);
        boolean esSiniestrada = ConstantesEstadoActa.ESTADO_ACTA_SINIESTRADA.equals(estadoActa);
        boolean esAsociada = ConstantesEstadoActa.ESTADO_ACTA_ASOCIADA_A_RESOLUCION.equals(estadoActa);

        if (!esExtraviada && !esSiniestrada && !esAsociada) {
            return;
        }

        // Buscar la última resolución activa asociada al acta, solo de tipo extraviada o siniestrada
        Optional<DetActaResolucion> ultimaResolucion = this.detActaResolucionRepository
                .findFirstByActa_IdAndActivoAndResolucion_TipoResolucionInOrderByIdDesc(
                        actaInfo.getActa().getId(),
                        ConstantesComunes.ACTIVO,
                        List.of(ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_EXTRAVIADAS,
                                ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_SINIESTRADAS,
                                ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_ENVIADAS_A_JEE));

        if (ultimaResolucion.isEmpty()) {
            return;
        }

        TabResolucion resolucion = ultimaResolucion.get().getResolucion();
        Integer tipoResolucion = resolucion.getTipoResolucion();
        String estadoResolucion = resolucion.getEstadoResolucion();

        // Escenario 1: Acta asociada (K) a resolución de tipo extraviada/siniestrada en estado D (en proceso)
        // → debe desasociar antes de digitalizar
        if (esAsociada && ConstantesEstadoResolucion.EN_PROCESO.equals(estadoResolucion)) {
            if (Objects.equals(tipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_EXTRAVIADAS)) {
                throw new BadRequestException(String.format(
                        "La mesa %s de elección %s está asociada a un memo de tipo acta extraviada.",
                        actaInfo.getBarCodeInfo().getNroMesa(),
                        actaInfo.getNombreEleccion()
                ));
            }
            if (Objects.equals(tipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_SINIESTRADAS)) {
                throw new BadRequestException(String.format(
                        "La mesa %s de elección %s está asociada a un memo de tipo acta siniestrada.",
                        actaInfo.getBarCodeInfo().getNroMesa(),
                        actaInfo.getNombreEleccion()
                ));
            }
        }

        // Escenario 2: Acta declarada extraviada/siniestrada con resolución procesada (P)
        // → debe asociarse a una resolución de tipo envío al jurado
        if (esExtraviada && Objects.equals(tipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_EXTRAVIADAS)
                && ConstantesEstadoResolucion.PROCESADO.equals(estadoResolucion)) {
            throw new BadRequestException(String.format(
                    "La mesa %s de elección %s se encuentra declarada extraviada, para digitalizarla primero asociela a la resolución.",
                    actaInfo.getBarCodeInfo().getNroMesa(),
                    actaInfo.getNombreEleccion()
            ));
        }

        if (esSiniestrada && Objects.equals(tipoResolucion, ConstantesCatalogo.CATALOGO_TIPO_RESOL_ACTAS_SINIESTRADAS)
                && ConstantesEstadoResolucion.PROCESADO.equals(estadoResolucion)) {
            throw new BadRequestException(String.format(
                    "La mesa %s de elección %s se encuentra declarada siniestrada, para digitalizarla primero asociela a la resolución.",
                    actaInfo.getBarCodeInfo().getNroMesa(),
                    actaInfo.getNombreEleccion()
            ));
        }
    }
    
	private void validarEstadoActaCeleste(ActaCelesteInfo actaInfo, Long tipoDocumento) {
		String estadoActa = actaInfo.getActa().getEstadoActa();
		String estadoCc = actaInfo.getActa().getEstadoCc();
		String estadoDigitalizacion = actaInfo.getActaCeleste().getEstadoDigitalizacion();
		String nroMesa = actaInfo.getBarCodeInfo().getNroMesa();
		String nombreEleccion = actaInfo.getNombreEleccion();
		String codigoBarra = actaInfo.getBarCodeInfo().getCodigoBarra();

		if (!ConstantesEstadoActa.ESTADO_ACTA_PARA_ENVIO_AL_JURADO.equals(estadoActa)
				|| !ConstantesEstadoActa.ESTADO_COMPUTO_ACTA_PROCESADA_OBSERVADA.equals(estadoCc)) {
			throw new BadRequestException(String.format(
					"La mesa %s de elección %s no puede ser digitalizada. porque el acta del sobre plomo no se encuentra en estado OBSERVADA y enviada al Jurado.",
					nroMesa, nombreEleccion));
		}
		
		if (!Arrays.asList(
	            ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION,
	            ConstantesEstadoActa.ESTADO_DIGTAL_1ER_CONTROL_RECHAZADA
	        ).contains(estadoDigitalizacion)) {
	        throw new BadRequestException(String.format(
	            "El acta celeste %s no se encuentra en estado PENDIENTE o RECHAZADA.",
	            codigoBarra
	        ));
	    }
		
		if (Objects.equals(tipoDocumento, ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AE) &&
				actaInfo.getActaCeleste().getArchivoEscrutinio() != null) {
		        throw new BadRequestException(String.format(
		            "El AE de la mesa %s ya fue digitalizado.", codigoBarra));
		    }

		    if (Objects.equals(tipoDocumento, ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AIS) &&
		    	actaInfo.getActaCeleste().getArchivoInstalacionSufragio() != null) {
		        throw new BadRequestException(String.format(
		            "El AIS de la mesa %s ya fue digitalizado.", codigoBarra));
		    }
	}


    private void guardarActa(Acta acta, Long type, String nroCopia,
                             Archivo archivo, String digitoChequeo,
                             String usuario, String codigoCentroComputo) {

        boolean esAE = Objects.equals(type, ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AE);

        // Campos comunes
        acta.setNumeroCopia(nroCopia);
        acta.setUsuarioModificacion(usuario);
        acta.setFechaModificacion(new Date());

        // Campos específicos
        if (esAE) {
            acta.setArchivoEscrutinio(archivo);
            acta.setDigitoChequeoEscrutinio(digitoChequeo);
        } else {
            acta.setArchivoInstalacionSufragio(archivo);
            acta.setDigitoChequeoInstalacion(digitoChequeo);
        }

        this.utilSceService.guardarActa(acta);

        if (esAE) {
            guardarDetAccionRecibida(
                acta.getId(),
                codigoCentroComputo,
                usuario,
                acta.getFechaModificacion()
            );
        }
    }
    
    private ActaCeleste guardarActaCeleste(ActaCeleste celeste, Long type, String nroCopia, Archivo archivo, String digitoChequeo, String usuario) {

    	boolean esAE = Objects.equals(type, ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AE);
    	
        if (esAE) {
            celeste.setNumeroCopia(nroCopia);
            celeste.setArchivoEscrutinio(archivo);
            celeste.setDigitoChequeoEscrutinio(digitoChequeo);
        } else {
            celeste.setArchivoInstalacionSufragio(archivo);
            celeste.setDigitoChequeoInstalacion(digitoChequeo);
        }
        
        celeste.setDigitalizacionEscrutinio(0L);
        celeste.setDigitalizacionInstalacion(0L);
        celeste.setDigitalizacionSufragio(0L);
		celeste.setDigitalizacionInstalacionSufragio(0L);	    
        celeste.setUsuarioModificacion(usuario);
        celeste.setFechaModificacion(new Date());
        
        this.utilSceService.guardarActaCeleste(celeste);
        
        return celeste;
    }
    
    private void guardarActaExtranjero(Acta acta, Long type, String nroCopia, Archivo tif, Archivo pdf, String digitoChequeo, String usuario, String codigoCentroComputo) {

        boolean esAE = Objects.equals(type, ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AE);
        if (esAE) {
            acta.setNumeroCopia(nroCopia);
            acta.setArchivoEscrutinio(tif);
            acta.setArchivoEscrutinioFirmado(pdf);
            acta.setDigitoChequeoEscrutinio(digitoChequeo);
            acta.setUsuarioModificacion(usuario);
            acta.setFechaModificacion(new Date());
        } else {
            acta.setArchivoInstalacionSufragio(tif);
            acta.setArchivoInstalacionSufragioFirmado(pdf);
            acta.setDigitoChequeoInstalacion(digitoChequeo);
        }

        this.utilSceService.guardarActa(acta);

        if (esAE) {
            guardarDetAccionRecibida(acta.getId(),codigoCentroComputo, usuario, acta.getFechaModificacion());
        }
    }

    private void guardarDetAccionRecibida(Long idActa, String codigoCentroComputo, String usuario, Date fecha) {
        DetActaAccion detActaAccion = DetActaAccion.builder()
                .fechaAccion(fecha)
                .codigoCentroComputo(codigoCentroComputo)
                .usuarioAccion(usuario)
                .tiempo(ConstantesComunes.DET_ACTA_ACCION_TIEMPO_INI)
                .activo(1)
                .accion(ConstantesComunes.DET_ACTA_ACCION_PROCESO_RECIBIDA)
                .acta(new Acta(idActa))
                .usuarioCreacion(usuario)
                .orden(2)
                .build();
        this.detActaAccionService.save(detActaAccion);
    }

    private void actualizarMesaInstalada(Acta acta, String usuario) {
        Mesa mesa = acta.getMesa();
        if(mesa.getEstadoMesa().equals(ConstantesEstadoMesa.POR_INFORMAR)){
            mesa.setEstadoMesa(ConstantesEstadoMesa.INSTALADA);
            mesa.setUsuarioModificacion(usuario);
            mesa.setFechaModificacion(new Date());
            this.mesaRepository.save(mesa);
        }
    }

    private void registrarLogDigitalizacionUploadActa(Long type, String barcode, TokenInfo tokenInfo) {
        String tipoDoc = Objects.equals(type, ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AE) ? "AE" : "AIS";
        String mensaje = String.format("El documento electoral %s del acta %s, fue digitalizado correctamente.", tipoDoc, barcode);

        this.logService.registrarLog(
                tokenInfo.getNombreUsuario(),
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                mensaje,
                tokenInfo.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);
    }

    private void regLogDigitalizacionActaCeleste(Long type, String barcode, TokenInfo tokenInfo) {
        String tipoDoc = (type == 2) ? "AE" : "AIS";
        String mensaje = String.format("El documento electoral %s del acta celeste %s, fue digitalizado correctamente.", tipoDoc, barcode);

        this.logService.registrarLog(
                tokenInfo.getNombreUsuario(),
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                mensaje,
                tokenInfo.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);
    }

    private Long determinarTipoDocumento(DetTipoEleccionDocumentoElectoral admDetTipoEleccionDocumentoElectoraAIS, BarCodeInfo barCodeInfo) {
        Long type = ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AE;
        if (admDetTipoEleccionDocumentoElectoraAIS.getDigitoChequeo().contains(barCodeInfo.getDigitoChequeo())) {
            type = ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AIS;
        }
        return type;
    }


    private void crearDocumentosMmMesa(MultipartFile file, Mesa mesa, TokenInfo tokenInfo, String tipoArchivo) {
        DocumentoElectoral admDocumentoElectoral = this.adminDocumentoElectoralService.findByAbreviatura(ConstantesComunes.ABREV_DOCUMENT_HOJA_DE_ASISTENCIA);
        if (admDocumentoElectoral == null){
            throw new BadRequestException("El documento electoral " + ConstantesComunes.ABREV_DOCUMENT_HOJA_DE_ASISTENCIA + " no se encuentra registrado.");
        }
        // Guardar zip temporalmente en storage solo para extracción (no se registra en tab_archivo ni MesaDocumento)
        String guidZip = this.archivoService.obtegerUui(file, tokenInfo);
        this.archivoService.storeFile(file, guidZip);
        this.archivoService.validarExtraerContenidoZipMm(mesa, guidZip);
        guardarMesaDocumentosExtraidos(mesa.getCodigo(), tokenInfo.getCodigoCentroComputo(), tokenInfo.getNombreUsuario(), mesa, admDocumentoElectoral, ConstantesComunes.ABREV_DOCUMENT_HOJA_DE_ASISTENCIA);
        // Eliminar el zip del storage
        this.archivoService.deleteFile(guidZip);
        mesa.setEstadoDigitalizacionMm(ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA);
        mesa.setFechaModificacion(new Date());
        mesa.setUsuarioModificacion(tokenInfo.getNombreUsuario());
        this.mesaRepository.save(mesa);
        this.logService.registrarLog(
                tokenInfo.getNombreUsuario(),
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                "La Hoja de asistencia de la mesa  "+mesa.getCodigo()+", fue digitalizada correctamente.",
                tokenInfo.getCodigoCentroComputo(),
                ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO,1);
    }

    private void crearDocumentosLeMesa(MultipartFile file, Mesa mesa, TokenInfo tokenInfo, String tipoArchivo, boolean isPerdidaParcial) {

        DocumentoElectoral admDocumentoElectoral = this.adminDocumentoElectoralService.findByAbreviatura(ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES);
        if (admDocumentoElectoral == null){
            throw new BadRequestException("El documento electoral " + ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES + " no se encuentra registrado.");
        }

        // Guardar zip temporalmente en storage solo para extracción (no se registra en tab_archivo ni MesaDocumento)
        String guidZip = this.archivoService.obtegerUui(file, tokenInfo);
        this.archivoService.storeFile(file, guidZip);
        boolean isImagenesCompletas = this.archivoService.validarExtraerContenidoZipLe(mesa, guidZip);

        //eliminar los mesa documentos anteriores
        if (mesa.getEstadoDigitalizacionLe().equals(ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA_PARCIALMENTE)) {
            List<MesaDocumento> mesaDocumentoList = this.mesaDocumentoRepository.findByMesaAndAdmDocumentoElectoral(mesa, admDocumentoElectoral);
            this.mesaDocumentoRepository.deleteAll(mesaDocumentoList);
        }

        guardarMesaDocumentosExtraidos(mesa.getCodigo(), tokenInfo.getCodigoCentroComputo(), tokenInfo.getNombreUsuario(), mesa, admDocumentoElectoral, ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES);

        // Eliminar el zip del storage
        this.archivoService.deleteFile(guidZip);

        if (isImagenesCompletas) {
            if(isPerdidaParcial) {
                throw new BadRequestException(String.format("No se puede digitalizar la lista de electores de la mesa %s está, está completa y cuenta con una denuncia procesada por pérdida parcial.", mesa.getCodigo()));
            }
            mesa.setEstadoDigitalizacionLe(ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA);
        } else {
            mesa.setEstadoDigitalizacionLe(ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA_PARCIALMENTE);
        }

        if(isPerdidaParcial)
            mesa.setEstadoDigitalizacionLe(ConstantesEstadoMesa.C_ESTADO_DIGTAL_DIGITALIZADA_CON_PERDIDA_PARCIAL);

        mesa.setFechaModificacion(new Date());
        mesa.setUsuarioModificacion(tokenInfo.getNombreUsuario());
        this.mesaRepository.save(mesa);
        this.logService.registrarLog(
                tokenInfo.getNombreUsuario(),
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                "La Lista de Electores de la mesa  "+mesa.getCodigo()+", fue digitalizada correctamente.",
                tokenInfo.getCodigoCentroComputo(),
                ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO,1);
    }

    private void guardarMesaDocumento(Archivo archivo, String usuario, Mesa mesa, DocumentoElectoral admDocumentoElectoral, String tipoArchivo, Integer pagina) {
        MesaDocumento mesaDocumento = new MesaDocumento();
        mesaDocumento.setMesa(mesa);
        mesaDocumento.setArchivo(archivo);
        mesaDocumento.setEstadoDigitalizacion(ConstantesDigtalMesa.DIGITALIZADA);
        mesaDocumento.setAdmDocumentoElectoral(admDocumentoElectoral);
        mesaDocumento.setUsuarioCreacion(usuario);
        mesaDocumento.setFechaCreacion(new Date());
        mesaDocumento.setActivo(ConstantesComunes.ACTIVO);
        mesaDocumento.setTipoArchivo(tipoArchivo);
        mesaDocumento.setPagina(pagina);
        mesaDocumento.setDigitalizacion(ConstantesComunes.ACTIVO);
        this.mesaDocumentoRepository.save(mesaDocumento);
    }

    private void guardarMesaDocumentosExtraidos(String nummesa, String codigoCentroComputo, String usuario, Mesa mesa,
                                                DocumentoElectoral admDocumentoElectoral, String abrevDocElectoral) {

        try {
            List<String> mimesValidos =  List.of(ConstantesFormatos.IMAGE_TIF_VALUE, ConstantesFormatos.PDF_VALUE);
            String uploadDir = this.archivoService.getPathUpload();
            Path filedestination = Paths.get(uploadDir).resolve(abrevDocElectoral).resolve(nummesa);
            String destinationExtract = filedestination.toString();

            File carpeta = new File(destinationExtract);
            for (String nombreArchivoCompleto : Objects.requireNonNull(carpeta.list())) {
                Path filepath = Paths.get(destinationExtract).resolve(nombreArchivoCompleto);
                File filePaginaMm = new File(filepath.toString());
                FileInputStream fileInputStream = new FileInputStream(filePaginaMm);

                String mimeType = new Tika().detect(fileInputStream, nombreArchivoCompleto);
                if (!mimesValidos.contains(mimeType.toLowerCase())) {
                    throw new BadRequestException(
                            String.format("El archivo %s no es válido. Tipo detectado: %s", nombreArchivoCompleto, mimeType)
                    );
                }
                Long size = filePaginaMm.length();
                String guid = codigoCentroComputo.concat(ConstantesComunes.GUION_MEDIO).concat(DigestUtils.sha256Hex(fileInputStream));
                Integer pagina = null;

                if (mimeType.equals(ConstantesFormatos.IMAGE_TIF_VALUE)) {
                    if(abrevDocElectoral.equalsIgnoreCase(ConstantesComunes.ABREV_DOCUMENT_LISTA_ELECTORES)){
                        String paginaS = nombreArchivoCompleto.substring(8, 10);//009896010130E.TIF
                        pagina = Integer.parseInt(paginaS);
                    }
                    else if(abrevDocElectoral.equalsIgnoreCase(ConstantesComunes.ABREV_DOCUMENT_HOJA_DE_ASISTENCIA)){
                        String codigo = nombreArchivoCompleto.substring(6, 8);//00996501G.TIF
                        pagina = switch (codigo) {
                            case ConstantesComunes.COD_DOCUMENT_MIEMBROS_DE_MESA -> 1;
                            case ConstantesComunes.COD_DOCUMENT_MIEMBROS_DE_MESA_NO_SORTEADOS -> 2;
                            default -> throw new IllegalArgumentException("Código no soportado: " + codigo);
                        };
                    }
                }

                Archivo archivo = this.archivoService.saveArchivo(nombreArchivoCompleto, size, guid, usuario, mimeType);
                guardarMesaDocumento(archivo, usuario, mesa, admDocumentoElectoral, archivo.getFormato(), pagina);
                this.archivoService.storeFile(filePaginaMm, archivo.getGuid());
                fileInputStream.close();
            }

            FileUtils.deleteDirectory(carpeta);
        } catch (Exception e) {
            throw new BadRequestException("Error al guardar los documentos extraido del zip.");
        }
    }
}
