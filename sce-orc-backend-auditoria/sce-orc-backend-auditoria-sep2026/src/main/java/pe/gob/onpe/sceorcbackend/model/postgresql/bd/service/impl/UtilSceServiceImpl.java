package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pe.gob.onpe.sceorcbackend.exception.BadRequestException;
import pe.gob.onpe.sceorcbackend.exception.DuplicadoException;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.verification.BarCodeInfo;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity.DetTipoEleccionDocumentoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.admin.services.DetTipoEleccionDocumentoElectoralService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.util.UtilRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.functionalinterface.ManejadorVotoIlegible;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.ActaCelesteInfo;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.ActaInfo;
import pe.gob.onpe.sceorcbackend.model.postgresql.util.MesaInfo;
import pe.gob.onpe.sceorcbackend.utils.*;
import org.apache.commons.imaging.Imaging;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class UtilSceServiceImpl implements UtilSceService {

    Logger logger = LoggerFactory.getLogger(UtilSceServiceImpl.class);

    private final ArchivoService archivoService;

    private final StorageService storageService;

    private final VersionRepository versionRepository;

    private final ProcesoElectoralRepository procesoElectoralRepository;

    private final DetActaOpcionService detActaOpcionService;

    private final MesaRepository mesaRepository;

    private final ActaRepository actaRepository;
    
    private final ActaCelesteRepository actaCelesteRepository;

    private final DetTipoEleccionDocumentoElectoralService detTipoEleccionDocumentoElectoralService;

    private final UtilRepository utilRepository;

    private final DetParametroRepository detParametroRepository;


    public UtilSceServiceImpl(ArchivoService archivoService,StorageService storageService,
                              VersionRepository versionRepository,
                              ProcesoElectoralRepository procesoElectoralRepository,
                              DetActaOpcionService detActaOpcionService,
                              MesaRepository mesaRepository,
                              ActaRepository actaRepository,
                              ActaCelesteRepository actaCelesteRepository,
                              DetTipoEleccionDocumentoElectoralService detTipoEleccionDocumentoElectoralService,
                              UtilRepository utilRepository,
                              DetParametroRepository detParametroRepository) {
        this.archivoService = archivoService;
        this.storageService = storageService;
        this.versionRepository = versionRepository;
        this.procesoElectoralRepository = procesoElectoralRepository;
        this.detActaOpcionService = detActaOpcionService;
        this.mesaRepository = mesaRepository;
        this.actaRepository =actaRepository;
        this.actaCelesteRepository = actaCelesteRepository;
        this.detTipoEleccionDocumentoElectoralService = detTipoEleccionDocumentoElectoralService;
        this.utilRepository = utilRepository;
        this.detParametroRepository = detParametroRepository;
    }

    @Override
    public Archivo guardarArchivoPdf(byte[] archivoPdf, String nombreArchivo, TokenInfo tokenInfo) {
       try {
           Archivo archivo = new Archivo();
           archivo.setNombre(nombreArchivo);
           archivo.setFormato(ConstantesFormatos.PDF_VALUE);
           archivo.setPeso(String.valueOf(archivoPdf.length));
           archivo.setActivo(ConstantesComunes.ACTIVO);
           archivo.setGuid(tokenInfo.getCodigoCentroComputo().concat(ConstantesComunes.GUION_MEDIO).concat(DigestUtils.sha256Hex(new ByteArrayInputStream(archivoPdf))));
           archivo.setUsuarioCreacion(tokenInfo.getNombreUsuario());
           archivo.setRuta(this.storageService.getPathUpload());
           archivo.setFechaCreacion(new Date());
           this.archivoService.save(archivo);
           this.storageService.storeFile(archivoPdf, archivo.getGuid());
           return archivo;
       } catch (Exception e){
           logger.error("Error: ",e);
           return null;
       }

    }

    @Override
    public String getVersionSistema() {
        return versionRepository.findAll().stream()
            .findFirst()
            .map(Version::getCodversion)
            .orElse("S/V");
    }


    @Override
    public String getSinValorOficial() {

        ProcesoElectoral procesoElectoral = this.procesoElectoralRepository.findByActivo(ConstantesComunes.ACTIVO);

        if (procesoElectoral == null || procesoElectoral.getFechaConvocatoria() == null) {
            return ConstantesComunes.SVO;
        }

        Date fechaConvocatoria = procesoElectoral.getFechaConvocatoria();

        // Restamos 2 días a la fecha de convocatoria
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(fechaConvocatoria);
        calendario.add(Calendar.DAY_OF_MONTH, ConstantesComunes.DIAS_PREVIOS_MARCA_DE_AGUA);
        Date fechaLimite = calendario.getTime();

        return new Date().before(fechaLimite)
            ? ConstantesComunes.SVO
            : ConstantesComunes.VACIO;
    }

    @Override
    public String getSinValorOficial(Integer idProceso) {
        if(idProceso == null ) return ConstantesComunes.SVO;
        Optional<ProcesoElectoral> procesoElectoral = this.procesoElectoralRepository.findById(idProceso.longValue());
        return calculoSinValor(procesoElectoral);
    }

    private String calculoSinValor(Optional<ProcesoElectoral> optionalProcesoElectoral) {

        if (optionalProcesoElectoral.isEmpty() || optionalProcesoElectoral.get().getFechaConvocatoria() == null)
            return ConstantesComunes.SVO;

        Date fechaConvocatoria = optionalProcesoElectoral.get().getFechaConvocatoria();

        //Restamos 2 días a la fecha de convocatoria
        Calendar calendario = Calendar.getInstance();
        calendario.setTime(fechaConvocatoria);
        calendario.add(Calendar.DAY_OF_MONTH, ConstantesComunes.DIAS_PREVIOS_MARCA_DE_AGUA);
        Date fechaLimite = calendario.getTime();

        return new Date().before(fechaLimite)
                ? ConstantesComunes.SVO
                : ConstantesComunes.VACIO;
    }

    @Override
    @Transactional
    public  <T> void procesarYGuardarDetActaOpcion(Acta acta, T votoOpcion, Optional<DetActaOpcion> optionalDetActaOpcion,
                                                   List<DetActaOpcion> listaErrores, String usuario, ManejadorVotoIlegible<T> manejador) {
        if (optionalDetActaOpcion.isEmpty()) return;

        DetActaOpcion detActaOpcion = optionalDetActaOpcion.get();
        manejador.manejar(acta, votoOpcion, detActaOpcion);
        detActaOpcion.setEstadoErrorMaterial(ConsultaErroresMateriales.getDetErrorMaterialOpcion(acta, detActaOpcion));

        if (detActaOpcion.getEstadoErrorMaterial() != null &&
            !detActaOpcion.getEstadoErrorMaterial().isEmpty()) {
            SceUtils.agregarEstadoResolucion(acta, ConstantesEstadoActa.ESTADO_ACTA_RESOLUCION_ERROR_MAT_AGRUPOL);
        }

        detActaOpcion.setFechaModificacion(new Date());
        detActaOpcion.setUsuarioModificacion(usuario);
        this.detActaOpcionService.save(detActaOpcion);
        listaErrores.add(detActaOpcion);
    }

    @Override
    public MesaInfo validarMesa(String nroMesa) {
        validarNumeroMesa(nroMesa);

        Mesa mesa = mesaRepository.findByCodigo(nroMesa);
        if (mesa == null) {
            throw new IllegalArgumentException(
                String.format(ConstantesMensajes.MSJ_FORMAT_NUMERO_MESA_NO_EXISTE, nroMesa)
            );
        }

        MesaInfo mesaInfo = new MesaInfo();
        mesaInfo.setMesa(mesa);
        validarExistenciaActasPorMesa(mesaInfo);
        return mesaInfo;
    }

    public void validarMesaNoInstalada(Mesa mesa) {
        if (ConstantesEstadoMesa.NO_INSTALADA.equals(mesa.getEstadoMesa())) {
            throw new BadRequestException(
                    String.format(ConstantesComunes.MENSAJE_FORMATO_MESA_ESTADO_NO_INSTALADA, mesa.getCodigo())
            );
        }
    }

    @Override
    public void guardarActa(Acta acta) {
        this.actaRepository.save(acta);
    }
    
    @Override
    public void guardarActaCeleste(ActaCeleste acta) {
        this.actaCelesteRepository.save(acta);
    }

    private record Config(
            List<String> mimes,
            List<String> extensiones,
            long maxSize,
            String errorSize
    ) {}

    @Override
    public String validarArchivoEscaneado(MultipartFile file, String tipoDocumento, String nroMesa) {
        if (file == null || file.isEmpty() || file.getSize() == 0) {
            throw new BadRequestException("El archivo está vacío o no contiene datos.");
        }

        Config config = getConfigGeneral(tipoDocumento);
        byte[] fileBytes;
        String mimeType;

        try {
            fileBytes = file.getBytes();
            mimeType = new Tika().detect(fileBytes, file.getOriginalFilename());
            validarCaracteresArchivoMultipart(file.getOriginalFilename());
            validarMimeGeneral(mimeType, config, file.getOriginalFilename());
            validarTamanoGeneral(file.getSize(), config);
            validarExtensionGeneral(file.getOriginalFilename(), config);

            if (isListaElectoresGeneral(tipoDocumento)) {
                validarNombreZipLEGeneral(file.getOriginalFilename(), nroMesa);
            }

            validarContenidoPorTipoDocumento(fileBytes, tipoDocumento, mimeType, nroMesa, file.getOriginalFilename());

            return mimeType;
        } catch (Exception e) {
            throw new BadRequestException("Error al procesar el archivo: " + e.getMessage());
        }
    }

    private void validarCaracteresArchivoMultipart(String fileName) {
        if(fileName == null || fileName.isEmpty()) {
            throw new BadRequestException("Nombre de archivo nulo o vacío");
        }

        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            throw new BadRequestException("Nombre de archivo no válido (ruta sospechosa).");
        }

        if (!ConstantesComunes.SAFE_FILENAME.matcher(fileName).matches()) {
            throw new BadRequestException("Nombre de archivo contiene caracteres no permitidos");
        }

    }

// --- Submétodos con sufijo _General ---

    private Config getConfigGeneral(String tipoDocumento) {
        Map<String, Config> configMap = Map.of(
                ConstantesComunes.CONTROL_CALIDAD_TIPO_DOCUMENTO_ACTA,
                new Config(List.of(ConstantesFormatos.IMAGE_TIF_VALUE),
                        List.of(ConstantesFormatos.EXTENSION_FILE_TIF),
                        ConstantesComunes.MAX_SIZE_ACTA,
                        ConstantesMensajes.MJS_ARCHIVO_NO_SUPERA_5_MB),
                ConstantesComunes.CONTROL_CALIDAD_TIPO_DOCUMENTO_RESOLUCION,
                new Config(List.of(ConstantesFormatos.IMAGE_TIF_VALUE, ConstantesFormatos.PDF_VALUE),
                        List.of(ConstantesFormatos.EXTENSION_FILE_TIF, ConstantesFormatos.EXTENSION_FILE_PDF),
                        ConstantesComunes.MAX_SIZE_RESOLUCION,
                        ConstantesMensajes.MJS_ARCHIVO_NO_SUPERA_10_MB),
                ConstantesComunes.CONTROL_CALIDAD_TIPO_DOCUMENTO_DENUNCIAS,
                new Config(List.of(ConstantesFormatos.IMAGE_TIF_VALUE, ConstantesFormatos.PDF_VALUE),
                        List.of(ConstantesFormatos.EXTENSION_FILE_TIF, ConstantesFormatos.EXTENSION_FILE_PDF),
                        ConstantesComunes.MAX_SIZE_DENUNCIAS,
                        ConstantesMensajes.MJS_ARCHIVO_NO_SUPERA_10_MB),
                ConstantesComunes.CONTROL_CALIDAD_TIPO_DOCUMENTO_LISTA_ELECTORES,
                new Config(List.of(ConstantesFormatos.ZIP_VALUE),
                        List.of(ConstantesFormatos.EXTENSION_FILE_ZIP),
                        ConstantesComunes.MAX_SIZE_LISTA_ELECTORES,
                        ConstantesMensajes.MJS_ARCHIVO_NO_SUPERA_50_MB),
                ConstantesComunes.CONTROL_CALIDAD_TIPO_DOCUMENTO_HOJA_ASISTENCIA,
                new Config(List.of(ConstantesFormatos.ZIP_VALUE),
                        List.of(ConstantesFormatos.EXTENSION_FILE_ZIP),
                        ConstantesComunes.MAX_SIZE_HOJA_ASISTENCIA,
                        ConstantesMensajes.MJS_ARCHIVO_NO_SUPERA_10_MB)
        );

        Config config = configMap.get(tipoDocumento);
        if (config == null) {
            throw new BadRequestException("Tipo de documento no soportado: " + tipoDocumento);
        }
        return config;
    }

    private void validarMimeGeneral(String mimeType, Config config, String nombreArchivo) {
        if (!config.mimes().contains(mimeType.toLowerCase())) {
            throw new BadRequestException(
                    String.format("El archivo %s no es válido. Tipo detectado: %s", nombreArchivo, mimeType));
        }
    }

    private void validarTamanoGeneral(long size, Config config) {
        if (size > config.maxSize()) {
            throw new BadRequestException(config.errorSize());
        }
    }

    private void validarExtensionGeneral(String nombreArchivo, Config config) {
        if (nombreArchivo == null ||
                config.extensiones().stream().noneMatch(ext -> nombreArchivo.toLowerCase().endsWith(ext.toLowerCase()))) {
            throw new BadRequestException(
                    "El archivo no tiene una extensión válida. Se esperaba: " +
                            config.extensiones().stream().map(String::toLowerCase).toList());
        }
    }

    private boolean isListaElectoresGeneral(String tipoDocumento) {
        return ConstantesComunes.CONTROL_CALIDAD_TIPO_DOCUMENTO_LISTA_ELECTORES.equals(tipoDocumento);
    }

    private void validarNombreZipLEGeneral(String nombreArchivo, String nroMesa) {
        String esperado = nroMesa + ConstantesFormatos.EXTENSION_FILE_ZIP;
        if (!nombreArchivo.equalsIgnoreCase(esperado)) {
            throw new BadRequestException(
                    String.format("El nombre del ZIP debe ser exactamente %s, recibido: %s", esperado, nombreArchivo));
        }
    }

    private void validarContenidoPorTipoDocumento(byte[] fileBytes, String tipoDocumento, String mimeType, String nroMesa, String nombreArchivo) {
        if (mimeType.equals(ConstantesFormatos.IMAGE_TIF_VALUE)) {
            validarTiffSeguro(fileBytes);
        } else if (mimeType.equals(ConstantesFormatos.PDF_VALUE)) {
            validarPdfSeguro(fileBytes);
        } else if (isListaElectoresGeneral(tipoDocumento) && mimeType.equals(ConstantesFormatos.ZIP_VALUE)) {
            validarZipSeguroLE(fileBytes, nroMesa);
        } else if (ConstantesComunes.CONTROL_CALIDAD_TIPO_DOCUMENTO_HOJA_ASISTENCIA.equals(tipoDocumento)
                && mimeType.equals(ConstantesFormatos.ZIP_VALUE)) {
            validarZipSeguroMM(fileBytes, nroMesa, nombreArchivo);
        }
    }


    @Override
    public void validarCodigoBarrasActas(String codeBar) {
        if (codeBar == null) {
            throw new BadRequestException("El código de barras es obligatorio.");
        }

        String trimmed = codeBar.trim();

        if (trimmed.isEmpty()) {
            throw new BadRequestException("El código de barras es obligatorio.");
        }

        if (trimmed.length() < ConstantesComunes.CODEBAR_MIN_LENGTH || trimmed.length() > ConstantesComunes.CODEBAR_MAX_LENGTH) {
            throw new BadRequestException(
                    String.format("El código de barras debe tener entre %d y %d caracteres.",
                            ConstantesComunes.CODEBAR_MIN_LENGTH, ConstantesComunes.CODEBAR_MAX_LENGTH)
            );
        }

        if (!ConstantesComunes.CODEBAR_PATTERN.matcher(trimmed).matches()) {
            throw new BadRequestException(
                    "El código de barras debe contener al menos un número y una letra mayúscula, y no puede incluir caracteres especiales."
            );
        }
    }

    @Override
    public void validarNumeroResolucion(String numeroResolucion) {

        if (numeroResolucion == null || numeroResolucion.isBlank()) {
            throw new IllegalArgumentException("El código es obligatorio.");
        }

        if (SceUtils.tieneEspaciosEnExtremos(numeroResolucion)) {
            throw new BadRequestException(String.format(
                    "El número de resolución tiene espacios en blanco al inicio y/o final: '%s'.", numeroResolucion));
        }

        if (!ConstantesComunes.NUMERO_RESOLUCION_PATTERN.matcher(numeroResolucion).matches()) {
            throw new IllegalArgumentException(
                    "Formato inválido para el número de resolución. Solo se permiten letras, números, guion (-), slash (/) y punto (.), un espacio en blanco entre caracteres " +
                            "con un máximo de 50 caracteres."
            );

        }
    }

    @Override
    public void validarPdfSeguro(byte[] fileBytes) {
        try (PDDocument document = Loader.loadPDF(fileBytes)) {

            // Buscar JavaScript en el catálogo
            COSDictionary catalog = document.getDocumentCatalog().getCOSObject();
            if (catalog.containsKey(COSName.AA) || catalog.containsKey(COSName.JS)) {
                throw new SecurityException("El archivo pdf contiene acciones automáticas o JavaScript.");
            }

            // Revisar acciones a nivel de documento
            if (document.getDocumentCatalog().getOpenAction() instanceof PDActionJavaScript) {
                throw new SecurityException("El archivo pdf contiene JavaScript en la acción de apertura.");
            }

            // Revisar formularios interactivos
            if (document.getDocumentCatalog().getAcroForm() != null) {
                throw new SecurityException("El archivo pdf contiene formularios interactivos (AcroForm).");
            }

        } catch (Exception e) {
            throw new BadRequestException("El archivo pdf no es seguro: " + e.getMessage());
        }
    }

    public void validarZipSeguroLE(byte[] fileBytes, String nroMesa) {
        String carpetaEsperada = nroMesa + "/";
        String pdfPattern = nroMesa + "\\.PDF";
        String tifPattern = nroMesa + "01\\d{2}\\d{2}[A-Z]\\.TIF";

        List<TifInfo> tifList = new ArrayList<>();
        boolean pdfEncontrado = false;

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(fileBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String fileNameOriginal = entry.getName();

                validarCarpetaLE(entry, fileNameOriginal, carpetaEsperada);
                if (entry.isDirectory()) continue;

                validarZipSlipLE(fileNameOriginal);

                byte[] content = leerContenidoLE(zis);
                String mimeType = new Tika().detect(content, fileNameOriginal);

                String nombreBase = extraerNombreBaseLE(fileNameOriginal);

                if (nombreBase.toUpperCase().matches(pdfPattern)) {
                    validarPdfLE(fileNameOriginal, mimeType);
                    pdfEncontrado = true;
                } else if (nombreBase.toUpperCase().matches(tifPattern)) {
                    tifList.add(validarTifLE(nombreBase, nroMesa, fileNameOriginal, mimeType));
                } else {
                    throw new SecurityException(
                            "Archivo '" + fileNameOriginal + "' no cumple patrón esperado: PDF (" +
                                    pdfPattern + ") o TIF (" + tifPattern + ")");
                }
            }
        } catch (Exception e) {
            throw new BadRequestException("ZIP inválido: " + e.getMessage());
        }

        if (!pdfEncontrado) throw new SecurityException("El ZIP LE debe contener un PDF con nombre: '" + nroMesa + ".PDF'");
        if (tifList.isEmpty()) throw new SecurityException("El ZIP LE debe contener al menos un TIF válido.");

        int paginaMaxGlobal = tifList.stream()
                .mapToInt(TifInfo::getPaginaMaxima)
                .max()
                .orElse(-1);

        tifList.forEach(tif -> {
            if (tif.getPagina() > paginaMaxGlobal) {
                throw new SecurityException(
                        "Archivo TIF '" + tif.getNombre() + "' tiene página " + tif.getPagina() +
                                " mayor que la página máxima esperada: " + paginaMaxGlobal);
            }
        });
    }

    // Submétodos LE
    private void validarCarpetaLE(ZipEntry entry, String fileNameOriginal, String carpetaEsperada) {
        if (!fileNameOriginal.toUpperCase().startsWith(carpetaEsperada.toUpperCase())) {
            throw new SecurityException(
                    "El archivo '" + fileNameOriginal + "' no está dentro de la carpeta esperada: " + carpetaEsperada);
        }
        if (entry.isDirectory() && !fileNameOriginal.equalsIgnoreCase(carpetaEsperada)) {
            throw new SecurityException(
                    "La carpeta '" + fileNameOriginal + "' dentro del ZIP no coincide con la carpeta esperada: " + carpetaEsperada);
        }
    }

    private void validarZipSlipLE(String fileName) {
        if (fileName.contains("..") || fileName.startsWith("/") || fileName.startsWith("\\")) {
            throw new SecurityException("Archivo inseguro (path traversal) dentro del ZIP: " + fileName);
        }
    }

    private byte[] leerContenidoLE(ZipInputStream zis) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int read;
        while ((read = zis.read(buffer)) != -1) {
            baos.write(buffer, 0, read);
        }
        return baos.toByteArray();
    }

    private String extraerNombreBaseLE(String fileNameOriginal) {
        return fileNameOriginal.contains("/") ? fileNameOriginal.substring(fileNameOriginal.lastIndexOf('/') + 1) : fileNameOriginal;
    }

    private void validarPdfLE(String fileNameOriginal, String mimeType) {
        if (!mimeType.equalsIgnoreCase(ConstantesFormatos.PDF_VALUE)) {
            throw new SecurityException("PDF con MIME incorrecto: '" + fileNameOriginal + "' (" + mimeType + ")");
        }
    }

    private TifInfo validarTifLE(String nombreBase, String nroMesa, String fileNameOriginal, String mimeType) {
        int offset = nroMesa.length() + 2; // +2 para el literal "01"
        int pagina = Integer.parseInt(nombreBase.substring(offset, offset + 2));
        int paginaMaxima = Integer.parseInt(nombreBase.substring(offset + 2, offset + 4));

        if (pagina < 1 || pagina > 99) throw new SecurityException("Archivo TIF '" + fileNameOriginal + "' tiene pagina inválida: " + pagina);
        if (paginaMaxima < 1 || paginaMaxima > 99) throw new SecurityException("Archivo TIF '" + fileNameOriginal + "' tiene pagina máxima inválida: " + paginaMaxima);
        if (!mimeType.equalsIgnoreCase(ConstantesFormatos.IMAGE_TIF_VALUE)) {
            throw new SecurityException("TIF con MIME incorrecto: '" + fileNameOriginal + "' (" + mimeType + ")");
        }

        return new TifInfo(fileNameOriginal, pagina, paginaMaxima);
    }

    public void validarZipSeguroMM(byte[] fileBytes, String nroMesa, String zipFileName) {
        validarNombreZip(nroMesa, zipFileName);

        Tika tika = new Tika();
        Map<String, String> patrones = Map.of(
                nroMesa + "\\.PDF", "PDF",
                nroMesa + "01[A-Z]\\.TIF", ConstantesComunes.VALIDACION_TIF_01_MM,
                nroMesa + "02[A-Z]\\.TIF", ConstantesComunes.VALIDACION_TIF_02_MM
        );

        Map<String, Integer> contador = new HashMap<>();
        List<String> archivosZip = new ArrayList<>();

        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(fileBytes))) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                validarZipEntry(entry);

                byte[] content = zis.readAllBytes();
                String fileName = entry.getName();
                String mimeType = tika.detect(content, fileName);
                archivosZip.add(fileName);

                boolean matched = false;
                for (Map.Entry<String, String> p : patrones.entrySet()) {
                    if (fileName.toUpperCase().matches(p.getKey())) {
                        validarMime(p.getValue(), mimeType, fileName);
                        contador.put(p.getValue(), contador.getOrDefault(p.getValue(), 0) + 1);
                        matched = true;
                        break;
                    }
                }
                if (!matched) {
                    throw new SecurityException("Archivo no permitido: " + fileName);
                }
            }
        } catch (IOException e) {
            throw new BadRequestException("ZIP inválido: " + e.getMessage());
        }

        validarConteoArchivos(archivosZip, contador, nroMesa);
    }

    // Métodos auxiliares
    private void validarNombreZip(String nroMesa, String zipFileName) {
        if (zipFileName == null || !zipFileName.equalsIgnoreCase(nroMesa + ".zip")) {
            throw new SecurityException("El nombre del ZIP debe ser igual al número de mesa: '" + nroMesa + ".zip'");
        }
    }

    private void validarZipEntry(ZipEntry entry) {
        String fileName = entry.getName();
        if (fileName.contains("..") || fileName.startsWith("/") || fileName.startsWith("\\")) {
            throw new SecurityException("Archivo inseguro: " + fileName);
        }
        if (entry.isDirectory()) {
            throw new SecurityException("El ZIP no debe contener carpetas: " + fileName);
        }
    }

    private void validarMime(String tipo, String mimeType, String fileName) {
        if ("PDF".equals(tipo) && !mimeType.equalsIgnoreCase(ConstantesFormatos.PDF_VALUE)) {
            throw new SecurityException("PDF con MIME incorrecto: " + fileName);
        }
        if ((tipo.equals(ConstantesComunes.VALIDACION_TIF_01_MM) || tipo.equals(ConstantesComunes.VALIDACION_TIF_02_MM)) && !mimeType.equalsIgnoreCase(ConstantesFormatos.IMAGE_TIF_VALUE)) {
            throw new SecurityException(tipo + " con MIME incorrecto: " + fileName);
        }
    }

    private void validarConteoArchivos(List<String> archivosZip, Map<String, Integer> contador, String nroMesa) {
        if (archivosZip.size() != ConstantesComunes.EXPECTED_FILE_COUNT_MM) {
            throw new SecurityException("El ZIP debe contener exactamente 3 archivos, se encontraron: " + archivosZip.size());
        }
        if (!contador.containsKey("PDF")) throw new SecurityException("Falta el PDF obligatorio: '" + nroMesa + ".PDF'");
        if (!contador.containsKey(ConstantesComunes.VALIDACION_TIF_01_MM)) throw new SecurityException("Falta el TIF 01 obligatorio: patrón '" + nroMesa + "01[A-Z].TIF'");
        if (!contador.containsKey(ConstantesComunes.VALIDACION_TIF_02_MM)) throw new SecurityException("Falta el TIF 02 obligatorio: patrón '" + nroMesa + "02[A-Z].TIF'");
    }




    @Override
    public void validarTiffSeguro(byte[] fileBytes) {
        try (ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(fileBytes))) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (!readers.hasNext()) {
                throw new BadRequestException("No se encontró lector de imágenes TIFF válido.");
            }

            ImageReader reader = readers.next();
            reader.setInput(iis, false, true);

            int numPages = reader.getNumImages(true);
            if (numPages == 0) {
                throw new BadRequestException("El TIFF no contiene imágenes válidas.");
            }

        } catch (Exception e) {
            // Fallback con Commons Imaging
            try {
                Imaging.getImageInfo(fileBytes); // valida cabecera sin decodificar
            } catch (Exception ex) {
                throw new BadRequestException("El TIFF no es válido o está corrupto: " + ex.getMessage());
            }
        }
    }

    @Override
    public void validarNumeroMesa(String nroMesa) {
        if (nroMesa == null) {
            throw new BadRequestException("El número de mesa es obligatorio.");
        }

        String trimmed = nroMesa.trim();

        if (trimmed.isEmpty()) {
            throw new BadRequestException("El número de mesa es obligatorio.");
        }

        if (trimmed.length() != ConstantesComunes.LONGITUD_MESA) {
            throw new BadRequestException(String.format("El número de mesa debe tener %d caracteres.", ConstantesComunes.LONGITUD_MESA));
        }

        if (!ConstantesComunes.NRO_MESA_PATTERN.matcher(trimmed).matches()) {
            throw new BadRequestException(
                    "El número de mesa solo debe incluir números."
            );
        }
    }

    @Override
    public String obtenerNombreProcesoByAcronimo(final String acronimo) {
        String nombre = "";
        try {
            ProcesoElectoral procesoElectoral = this.procesoElectoralRepository.findByAcronimo(acronimo);
            if(Objects.nonNull(procesoElectoral)) {
                nombre = procesoElectoral.getNombre();
            }
            return nombre;
        }catch (Exception e){
            return nombre;
        }
    }


    @Override
    public MesaInfo validarMesaConEleccionPrincipal(String nroMesa) {
        MesaInfo mesaInfo = validarMesa(nroMesa);
        setearEleccionPrincipal(mesaInfo);
        return mesaInfo;
    }

    private void validarExistenciaActasPorMesa(MesaInfo mesaInfo) {
        List<Acta> cabActaList = this.actaRepository.findByMesaOrderById(mesaInfo.getMesa());
        if (cabActaList.isEmpty()) {
            throw new IllegalArgumentException(
                String.format("No existen registros de actas para la mesa %s.", mesaInfo.getMesa().getCodigo())
            );
        }

        mesaInfo.setActaList(cabActaList);
    }

    private void setearEleccionPrincipal(MesaInfo mesaInfo) {

        List<Acta> actaList = this.actaRepository.findActaPrincipalByMesa(mesaInfo.getMesa().getCodigo());
        if(actaList.isEmpty()){
            throw new BadRequestException(
                    String.format("El acta principal asociada a la mesa %s no existe.", mesaInfo.getMesa().getCodigo())
            );
        }

        mesaInfo.setActaPrincipal(actaList.getFirst());
    }

    @Override
    public ActaInfo validarActa(String codigoBarras, String codigoCentroComputo, boolean isUploadDigitalizacion) {

        BarCodeInfo barCodeInfo = SceUtils.parsearCodigoBarra(codigoBarras);

        MesaInfo mesaInfo = validarMesa(barCodeInfo.getNroMesa());

        DetTipoEleccionDocumentoElectoral configAe =
            this.detTipoEleccionDocumentoElectoralService.findByCopia(barCodeInfo.getNroCopia());
        DetTipoEleccionDocumentoElectoral configAis =
                this.detTipoEleccionDocumentoElectoralService.findAisByCopia(barCodeInfo.getNroCopia());

        Eleccion eleccion = configAe.getEleccion();
        List<Acta> actas = this.actaRepository
            .buscarActaPorCodigoMesaaAndCodigoEleccion(barCodeInfo.getNroMesa(), eleccion.getCodigo());

        if (actas.isEmpty()) {
            throw new IllegalArgumentException(
                String.format("El número de acta %s no existe para la elección %s", codigoBarras, eleccion.getNombre())
            );
        }

        Acta acta = actas.getFirst();

        // Validar centro de cómputo solo si se proporcionó
        if (codigoCentroComputo != null && !codigoCentroComputo.trim().isEmpty()) {
            CentroComputo centroComputo = acta.getUbigeoEleccion().getUbigeo().getCentroComputo();
            if (!codigoCentroComputo.equalsIgnoreCase(centroComputo.getCodigo())) {
                throw new IllegalArgumentException(
                    String.format("El número de acta %s no pertenece al centro de cómputo %s", codigoBarras, codigoCentroComputo)
                );
            }
        }

        if(!isUploadDigitalizacion) {
            if (acta.getNumeroCopia() == null) {
                throw new IllegalArgumentException(
                    String.format("El acta con código de barras %s no tiene número de copia registrado en la BD", codigoBarras)
                );
            }

            if (acta.getDigitoChequeoEscrutinio() == null) {
                throw new IllegalArgumentException(
                    String.format("El acta con código de barras %s no tiene dígito de chequeo registrado en el sistema.", codigoBarras)
                );
            }

            String copiaDigBD = acta.getNumeroCopia().concat(acta.getDigitoChequeoEscrutinio());
            if (!copiaDigBD.equalsIgnoreCase(barCodeInfo.getNroCopiaAndDigito())) {
                throw new IllegalArgumentException(
                    String.format("La copia y dígito %s no coinciden con lo registrado en el sistema.", barCodeInfo.getNroCopiaAndDigito())
                );
            }
        }

        return ActaInfo.builder()
                .acta(acta)
                .mesa(mesaInfo.getMesa())
                .codigoEleccion(eleccion.getCodigo())
                .nombreEleccion(eleccion.getNombre())
                .barCodeInfo(barCodeInfo)
                .configAe(configAe)
                .configAis(configAis)
                .build();
    }
    
    @Override
    public ActaCelesteInfo validarActaCeleste (String codigoBarras, String codigoCentroComputo, String usuario) {
    	BarCodeInfo barCodeInfo = SceUtils.parsearCodigoBarra(codigoBarras);
        MesaInfo mesaInfo = validarMesa(barCodeInfo.getNroMesa());

        DetTipoEleccionDocumentoElectoral configAe =
            this.detTipoEleccionDocumentoElectoralService.findByCopia(barCodeInfo.getNroCopia());

        DetTipoEleccionDocumentoElectoral configAis =
            this.detTipoEleccionDocumentoElectoralService.findAisByCopia(barCodeInfo.getNroCopia());
        
        Eleccion eleccion = configAe.getEleccion();
        List<Acta> actas = this.actaRepository
            .buscarActaPorCodigoMesaaAndCodigoEleccion(barCodeInfo.getNroMesa(), eleccion.getCodigo());
        
        if (actas.isEmpty()) {
            throw new IllegalArgumentException(String.format(
                "El número de acta %s no existe para la elección %s.",
                codigoBarras, eleccion.getNombre()
            ));
        }
        
        Acta actaPloma = actas.getFirst();
        
        validarCentroComputo(codigoCentroComputo, actaPloma, codigoBarras);

        validarDatosActaPlomaRegistrados(actaPloma, codigoBarras);

        validarCopiaYDigitoDistintos(barCodeInfo, actaPloma);
        
        ActaCeleste actaCeleste = findActaCelesteByActaId(actaPloma.getId()).orElseGet(() -> {
            ActaCeleste nueva = new ActaCeleste();
            nueva.setActa(actaPloma);
            nueva.setActivo(SceConstantes.ACTIVO);
            nueva.setDigitalizacionEscrutinio(0L);
            nueva.setDigitalizacionInstalacionSufragio(0L);
            nueva.setEstadoDigitalizacion(ConstantesEstadoActa.ESTADO_DIGTAL_PENDIENTE_DIGITALIZACION);
            nueva.setUsuarioCreacion(usuario);
            nueva.setFechaCreacion(new Date());
            return nueva;
        });
        
        return ActaCelesteInfo.builder()
                .actaCeleste(actaCeleste)
                .acta(actaPloma)
                .mesa(mesaInfo.getMesa())
                .codigoEleccion(eleccion.getCodigo())
                .nombreEleccion(eleccion.getNombre())
                .barCodeInfo(barCodeInfo)
                .configAe(configAe)
                .configAis(configAis)
                .build();
    }
    
    @Override
    public Optional<ActaCeleste> findActaCelesteByActaId(Long actaId) {
        return actaCelesteRepository.findByActa_Id(actaId);
    }

    @Override
    public Integer obtenerCantidadCandidatos(String esquema, Long idActa) {
        return this.utilRepository.obtenerCantidadCandidatos(esquema, idActa);
    }


    public void inactivarArchivo(Archivo archivoAnterior, TokenInfo tokenInfo) {
        if(archivoAnterior !=null){
            archivoAnterior.setActivo(ConstantesComunes.INACTIVO);
            archivoAnterior.setFechaModificacion(new Date());
            archivoAnterior.setUsuarioModificacion(tokenInfo.getNombreUsuario());
            this.archivoService.save(archivoAnterior);
        }
    }

    public void validarGuidUnico(TokenInfo tokenInfo, MultipartFile file) {
        String guid = SceUtils.getGuid(tokenInfo, file);
        boolean existe = !archivoService.findByGuidAndActivo(guid, ConstantesComunes.ACTIVO).isEmpty();
        if (existe) {
            throw new BadRequestException(String.format("Ya existe un archivo con el GUID: %s.", guid));
        }
    }

    public void validarNombreArchivoUnico(MultipartFile file) {
        boolean existe = !archivoService.findByNombreAndActivo(file.getOriginalFilename(), ConstantesComunes.ACTIVO).isEmpty();
        if (existe) {
            throw new DuplicadoException(String.format("El archivo con nombre '%s' ya se encuentra registrado.", file.getOriginalFilename()));
        }
    }

    @Override
    public void storeFile(MultipartFile file, String fileName) {
        this.archivoService.storeFile(file, fileName);
    }

    @Override
    public String getResolucionJne() {
        try {
            List<DetParametro> parametros = this.detParametroRepository
                    .findByNombre(ConstantesReportes.NOMBRE_PARAMETRO_DET_RESOLUCION_JNE)
                    .stream().toList();
            if(!parametros.isEmpty()){
                return parametros.getFirst().getValor();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private void validarCentroComputo(String codigoCentroComputo, Acta acta, String codigoBarras) {
        if (codigoCentroComputo == null || codigoCentroComputo.isBlank()) return;

        CentroComputo cc = acta.getUbigeoEleccion().getUbigeo().getCentroComputo();

        if (!codigoCentroComputo.equalsIgnoreCase(cc.getCodigo())) {
            throw new IllegalArgumentException(String.format(
                "El número de acta %s no pertenece al centro de cómputo %s.",
                codigoBarras, codigoCentroComputo
            ));
        }
    }
    
    private void validarDatosActaPlomaRegistrados(Acta acta, String codigoBarras) {
       if (acta.getNumeroCopia() == null || acta.getDigitoChequeoEscrutinio() == null) {
           throw new IllegalArgumentException(String.format(
             "El acta ploma con código de barras %s no tiene número de copia y/o dígito de chequeo registrado.",
              codigoBarras
          ));
      }
   }
    
   private void validarCopiaYDigitoDistintos(BarCodeInfo barCodeCeleste, Acta actaPloma) {
      String copiaPloma = actaPloma.getNumeroCopia();
      String digitoPloma = actaPloma.getDigitoChequeoEscrutinio();

      String copiaCeleste = barCodeCeleste.getNroCopia();
      String digitoCeleste = barCodeCeleste.getDigitoChequeo();

      if (copiaPloma.equalsIgnoreCase(copiaCeleste)) {
         throw new IllegalArgumentException(String.format(
             "El número de copia del acta celeste (%s) no puede ser igual al de la acta ploma (%s).",
             copiaCeleste, copiaPloma
            ));
        }

     if (digitoPloma.equalsIgnoreCase(digitoCeleste)) {
         throw new IllegalArgumentException(String.format(
           "El dígito de chequeo del acta celeste (%s) no puede ser igual al de la acta ploma (%s).",
           digitoCeleste, digitoPloma
          ));
        }
    }
    
}
