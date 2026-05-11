package pe.gob.onpe.scebackend.model.service.impl.comun;

import java.io.File;
import java.util.Date;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import pe.gob.onpe.scebackend.model.dto.request.RecepcionJne;
import pe.gob.onpe.scebackend.model.dto.request.RecepcionJneRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.enums.EstadoRecepcionJneEnum;
import pe.gob.onpe.scebackend.model.orc.entities.CentroComputo;
import pe.gob.onpe.scebackend.model.orc.entities.JneTransmisionRecepcion;
import pe.gob.onpe.scebackend.model.orc.repository.CentroComputoRepository;
import pe.gob.onpe.scebackend.model.orc.repository.JneTransmisionRecepcionRepository;
import pe.gob.onpe.scebackend.model.service.StorageService;
import pe.gob.onpe.scebackend.utils.SceConstantes;

@Service
@Slf4j
@RequiredArgsConstructor
public class JneEnvioService {

    private final JneTransmisionRecepcionRepository jneTransmisionRecepcionRepository;
    private final CentroComputoRepository centroComputoRepository;
    private final StorageService storageService;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    private static final String HEADER_CC = "codigocc";

    @Value("${sce.cc.url-jne-recepcion}")
    private String urlEndpointJneRecepcion;

    @Transactional(transactionManager = "locationTransactionManager", rollbackFor = Exception.class)
    public boolean enviarRecepcionOrc(JneTransmisionRecepcion registro) {
        log.info("[enviarRecepcionOrc] Iniciando envío id={}", registro.getId());
        try {
            int updated = jneTransmisionRecepcionRepository.bloquearParaProceso(registro.getId(),
                    EstadoRecepcionJneEnum.EN_PROCESO.getCodigo(),
                    List.of(EstadoRecepcionJneEnum.PENDIENTE.getCodigo(),
                            EstadoRecepcionJneEnum.ERROR.getCodigo()));

            if (updated == 0) {
                log.info("[enviarRecepcionOrc] Registro {} ya fue tomado por otro proceso",
                        registro.getId());
                return false;
            }

            registro = jneTransmisionRecepcionRepository.findByIdWithArchivo(registro.getId())
                    .orElseThrow(() -> new IllegalStateException("Registro no encontrado"));

            Integer intentosActuales = registro.getIntentos() == null ? 0 : registro.getIntentos();

            if (intentosActuales >= SceConstantes.MAX_INTENTOS_JNE) {
                log.warn("[enviarRecepcionOrc] Máximo de intentos alcanzado id={}",
                        registro.getId());
                return false;
            }

            if (registro.getEnviado() == EstadoRecepcionJneEnum.ENVIADO.getCodigo()) {
                log.info("[enviarRecepcionOrc] Registro {} ya fue enviado", registro.getId());
                return true;
            }

            RecepcionJneRequestDto request = objectMapper.readValue(registro.getTrama(),
                    RecepcionJneRequestDto.class);
            RecepcionJne recepcion = request.getCarga().getRecepciones().get(0);
            String numeroActa = recepcion.getActas().getNumeroMesa();

            CentroComputo cp = centroComputoRepository.findByCodigoMesa(numeroActa).orElseThrow(
                    () -> new IllegalStateException(
                            "CentroComputo no encontrado para acta " + numeroActa));

            log.info("[enviarRecepcionOrc] Centro de computo para envio trama : {}",
                    cp.getCodigo());

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(cp.getApiTokenBackedCc());
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            headers.add(HEADER_CC, cp.getCodigo());

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            body.add("json", new HttpEntity<>(registro.getTrama()));
            body.add("codigoEnvio", new HttpEntity<>(registro.getCodigoJneEnvio()));
            if (registro.getArchivo() != null) {
                File ruta = storageService.obtenerArchivoRuta(registro.getArchivo());
                if (ruta == null) {
                    log.info("[enviarRecepcionOrc] - Error no se encuentro archivo");
                    throw new IllegalStateException(
                            "No se pudo acceder al archivo para transmisión ORC: "
                                    + registro.getArchivo().getId());
                }

                FileSystemResource pdfRes = new FileSystemResource(ruta);

                HttpHeaders pdfHeaders = new HttpHeaders();
                pdfHeaders.setContentType(MediaType.APPLICATION_PDF);

                body.add("filePdf", new HttpEntity<>(pdfRes, pdfHeaders));
            }

            String urlBase = String.format("%s://%s:%d", cp.getProtocolBackendCc(),
                    cp.getIpBackendCc(),
                    cp.getPuertoBackedCc());

            String url = urlBase + urlEndpointJneRecepcion;

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body,
                    headers);

            ResponseEntity<GenericResponse> response = restTemplate.exchange(url, HttpMethod.POST,
                    requestEntity,
                    GenericResponse.class);

            boolean ok = response.getStatusCode() == HttpStatus.OK;

            registro.setAudUsuarioModificacion(registro.getAudUsuarioCreacion());
            registro.setAudFechaModificacion(new Date());
            if (ok) {
                registro.setEnviado(EstadoRecepcionJneEnum.ENVIADO.getCodigo());
                registro.setEstado(EstadoRecepcionJneEnum.ENVIADO.getCodigo());
                registro.setMensaje("Enviado correctamente a CC");
            } else {
                registro.setEstado(EstadoRecepcionJneEnum.ERROR.getCodigo());
                registro.setMensaje("Ocurrio un error enviando a ORC");
            }

            jneTransmisionRecepcionRepository.save(registro);

            log.info("[enviarRecepcionOrc] Resultado envío id={} status={}", registro.getId(),
                    response.getStatusCode());
            return ok;
        } catch (Exception e) {
            log.error("[enviarRecepcionOrc] Error enviando a ORC id={}", registro.getId(), e);

            registro.setEstado(EstadoRecepcionJneEnum.ERROR.getCodigo());
            registro.setMensaje(e.getMessage());
            registro.setAudFechaModificacion(new Date());
            registro.setAudUsuarioModificacion(registro.getAudUsuarioCreacion());
            jneTransmisionRecepcionRepository.save(registro);
            return false;
        }
    }
}
