package pe.gob.onpe.scebackend.model.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.exportar.orc.service.IOrcDetalleTipoEleccionDocumentoElectoralExportService;
import pe.gob.onpe.scebackend.model.orc.entities.Acta;
import pe.gob.onpe.scebackend.model.orc.entities.Archivo;
import pe.gob.onpe.scebackend.model.orc.entities.CentroComputo;
import pe.gob.onpe.scebackend.model.orc.entities.Eleccion;
import pe.gob.onpe.scebackend.model.orc.entities.OrcDetalleTipoEleccionDocumentoElectoral;
import pe.gob.onpe.scebackend.model.orc.repository.ActaRepository;
import pe.gob.onpe.scebackend.model.orc.repository.CentroComputoRepository;
import pe.gob.onpe.scebackend.model.service.ExtranjeroService;
import pe.gob.onpe.scebackend.model.service.IArchivoOrcService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesEstadoActa;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesTipoDocumentoElectoral;

@Service
public class ExtranjeroServiceImpl implements ExtranjeroService {

    Logger logger = LoggerFactory.getLogger(ExtranjeroServiceImpl.class);

    private final CentroComputoRepository centroComputoRepository;
    private final ActaRepository actaRepository;
    private final IOrcDetalleTipoEleccionDocumentoElectoralExportService detalleTipoEleccionDocumentoElectoralExportService;
    private final IArchivoOrcService archivoOrcService;
    private final RestTemplate restTemplate;

    private static final String HEADER_CC = "codigocc";

    @Value("${sce.cc.url-extranjero-acta}")
    private String urlEndpointActaExtranjero;

    public ExtranjeroServiceImpl(ActaRepository actaRepository, CentroComputoRepository centroComputoRepository,
            IOrcDetalleTipoEleccionDocumentoElectoralExportService detalleTipoEleccionDocumentoElectoralExportService,
            IArchivoOrcService archivoOrcService, RestTemplate restTemplate) {
        this.actaRepository = actaRepository;
        this.centroComputoRepository = centroComputoRepository;
        this.detalleTipoEleccionDocumentoElectoralExportService = detalleTipoEleccionDocumentoElectoralExportService;
        this.archivoOrcService = archivoOrcService;
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean enviarActaOrc(MultipartFile filePdf, MultipartFile fileTif, String numeroActa, String copia,
            String digitoChequeo, String usuario, String centroComputo) {
        logger.info("[enviarActaOrc] Iniciando proceso para acta {} | copia={} | digitoChequeo={}", numeroActa, copia,
                digitoChequeo);
        Optional<CentroComputo> cpo = centroComputoRepository.findByCodigo(centroComputo);

        if (cpo.isEmpty()) {
            logger.warn("[enviarActaOrc] No se encontró CentroComputo para acta {}", numeroActa);
            return false;
        }

        CentroComputo cp = cpo.get();
        logger.info("[enviarActaOrc] CentroComputo encontrado: código={}, id={}", cp.getCodigo(), cp.getId());

        try {
            OrcDetalleTipoEleccionDocumentoElectoral configAe = this.detalleTipoEleccionDocumentoElectoralExportService
                    .findByCopiaExt(copia);
            OrcDetalleTipoEleccionDocumentoElectoral configAis = this.detalleTipoEleccionDocumentoElectoralExportService
                    .findAisByCopiaExt(copia);

            if (configAe == null || configAis == null) {
                logger.error("[enviarActaOrc] Configuración AE o AIS no encontrada para copia {}", copia);
                return false;
            }

            Eleccion eleccion = configAe.getEleccion();
            List<Acta> actas = actaRepository.buscarActaPorCodigoMesaAndCodigoEleccion(numeroActa,
                    eleccion.getCodigo());

            if (actas.isEmpty()) {
                logger.warn("[enviarActaOrc] No se encontró acta para código={} y elección={}", numeroActa,
                        eleccion.getNombre());
                return false;
            }

            Acta acta = actas.getFirst();
            logger.info("[enviarActaOrc] Acta encontrada: id={} | codigo={} | eleccion={}", acta.getId(), numeroActa,
                    eleccion.getNombre());

            Long tipoDocumento = determinarTipoDocumento(configAis, digitoChequeo);
            Integer tipoDocumentoPr = tipoDocumento.equals(ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AIS)
                    ? ConstantesTipoDocumentoElectoral.ACTA_INSTALACION_Y_SUFRAGIO
                    : ConstantesTipoDocumentoElectoral.ACTA_DE_ESCRUTINIO;

            logger.info("[enviarActaOrc] Guardando archivos PDF y TIF...");
            Archivo archivoTif = archivoOrcService.guardarArchivo(fileTif, usuario, cp.getCodigo(),
                    Optional.of(tipoDocumentoPr));
            Archivo archivoPdf = archivoOrcService.guardarArchivo(filePdf, usuario, cp.getCodigo(),
                    Optional.of(tipoDocumentoPr));

            guardarActaExtranjero(acta, tipoDocumento, copia, archivoTif, archivoPdf, digitoChequeo, usuario);
            logger.info(
                    "[enviarActaOrc] Archivos guardados y acta registrada. ActaId={} | TipoDocumento={} | Copia={} | DigitoChequeo={}",
                    acta.getId(), tipoDocumento, copia, digitoChequeo);

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(cp.getApiTokenBackedCc());
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.add("user", usuario);
            headers.add(HEADER_CC, cp.getCodigo());

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            try {
                logger.info("[enviarActaOrc] Preparando payload de archivos para envío...");
                ByteArrayResource pdfRes = new ByteArrayResource(filePdf.getBytes()) {
                    @Override
                    public String getFilename() {
                        return filePdf.getOriginalFilename() != null ? filePdf.getOriginalFilename() : "acta.pdf";
                    }
                };
                ByteArrayResource tifRes = new ByteArrayResource(fileTif.getBytes()) {
                    @Override
                    public String getFilename() {
                        return fileTif.getOriginalFilename() != null ? fileTif.getOriginalFilename() : "acta.tif";
                    }
                };

                HttpHeaders pdfHeaders = new HttpHeaders();
                pdfHeaders.setContentType(MediaType.APPLICATION_PDF);
                HttpHeaders tifHeaders = new HttpHeaders();
                tifHeaders.setContentType(MediaType.valueOf("image/tiff"));

                body.add("filePdf", new HttpEntity<>(pdfRes, pdfHeaders));
                body.add("fileTif", new HttpEntity<>(tifRes, tifHeaders));
                body.add("numeroActa", new HttpEntity<>(numeroActa));
                body.add("copia", new HttpEntity<>(copia));
                body.add("digitoChequeo", new HttpEntity<>(digitoChequeo));
            } catch (Exception e) {
                logger.error("[enviarActaOrc] Error al leer archivos", e);
                return false;
            }

            String urlBase = String.format("%s://%s:%d", cp.getProtocolBackendCc(), cp.getIpBackendCc(),
                    cp.getPuertoBackedCc());
            String url = urlBase + urlEndpointActaExtranjero;
            logger.info("[enviarActaOrc] Enviando acta a URL: {}", url);

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            try {
                ResponseEntity<GenericResponse> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
                        GenericResponse.class);
                boolean ok = response.getStatusCode() == HttpStatus.OK;
                logger.info("[enviarActaOrc] Respuesta del backend CC: {}", response.getStatusCode());
                return ok;
            } catch (Exception e) {
                logger.error("[enviarActaOrc] Error al enviar acta a backend CC", e);
                return false;
            }
        } catch (Exception e) {
            logger.error("[enviarActaOrc] Error general del proceso", e);
            return false;
        }
    }

    private void guardarActaExtranjero(Acta acta, Long type, String nroCopia, Archivo tif, Archivo pdf,
            String digitoChequeo, String usuario) {

        boolean esAE = Objects.equals(type, ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AE);
        if (esAE) {
            acta.setNumeroCopia(nroCopia);
            acta.setArchivoEscrutinio(tif);
            acta.setArchivoEscrutinioFirmado(pdf);
            acta.setDigitoChequeoEscrutinio(digitoChequeo);
        } else {
            acta.setArchivoInstalacionSufragio(tif);
            acta.setArchivoInstalacionSufragioFirmado(pdf);
            acta.setDigitoChequeoInstalacion(digitoChequeo);
        }

        Date now = new Date();
        acta.setUsuarioProcesamiento(usuario);
        acta.setFechaProcesamiento(now);
        acta.setUsuarioModificacion(usuario);
        acta.setFechaModificacion(now);

        boolean ambosDigitalizados = acta.getArchivoEscrutinio() != null
                && acta.getArchivoInstalacionSufragio() != null;

        if (ambosDigitalizados) {
            acta.setEstadoDigitalizacion(ConstantesEstadoActa.ESTADO_DIGTAL_DIGITALIZADA);
        }

        this.actaRepository.save(acta);
    }

    private Long determinarTipoDocumento(
            OrcDetalleTipoEleccionDocumentoElectoral admDetTipoEleccionDocumentoElectoraAIS, String chequeo) {
        Long type = ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AE;
        if (admDetTipoEleccionDocumentoElectoraAIS.getDigitoChequeo().contains(chequeo)) {
            type = ConstantesComunes.ID_DOCUMENTO_ELECTORAL_AIS;
        }
        return type;
    }

}
