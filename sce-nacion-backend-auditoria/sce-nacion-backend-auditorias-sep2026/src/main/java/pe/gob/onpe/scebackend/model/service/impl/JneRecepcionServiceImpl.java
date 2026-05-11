package pe.gob.onpe.scebackend.model.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import pe.gob.onpe.scebackend.exeption.JneRecepcionException;
import pe.gob.onpe.scebackend.model.dto.request.RecepcionJne;
import pe.gob.onpe.scebackend.model.dto.request.RecepcionJneRequestDto;
import pe.gob.onpe.scebackend.model.enums.EstadoOficioEnum;
import pe.gob.onpe.scebackend.model.enums.EstadoRecepcionJneEnum;
import pe.gob.onpe.scebackend.model.enums.TipoDocumentoJneEnum;
import pe.gob.onpe.scebackend.model.orc.entities.Archivo;
import pe.gob.onpe.scebackend.model.orc.entities.DetActaOficio;
import pe.gob.onpe.scebackend.model.orc.entities.JneTransmisionRecepcion;
import pe.gob.onpe.scebackend.model.orc.entities.Oficio;
import pe.gob.onpe.scebackend.model.orc.repository.DetActaOficioRepository;
import pe.gob.onpe.scebackend.model.orc.repository.JneTransmisionRecepcionRepository;
import pe.gob.onpe.scebackend.model.orc.repository.OficioRepository;
import pe.gob.onpe.scebackend.model.service.IArchivoOrcService;
import pe.gob.onpe.scebackend.model.service.JneRecepcionService;
import pe.gob.onpe.scebackend.model.service.impl.comun.JneEnvioService;
import pe.gob.onpe.scebackend.utils.SceConstantes;

@Service
@Slf4j
@RequiredArgsConstructor
public class JneRecepcionServiceImpl implements JneRecepcionService {

    private final OficioRepository oficioRepository;
    private final DetActaOficioRepository detActaOficioRepository;
    private final IArchivoOrcService archivoOrcService;
    private final JneTransmisionRecepcionRepository jneTransmisionRecepcionRepository;
    private final JneEnvioService envioService;
    private final ObjectMapper objectMapper;

    @Value("${sce.cc.url-jne-recepcion}")
    private String urlEndpointJneRecepcion;

    @Override
    public void procesarRecepcion(MultipartFile pdf, String json, String codigoEnvio) {
        try {
            log.info("[procesarRecepcion] Recepción Nación recibida");
            log.info("[procesarRecepcion] Codigo de recepcion: {}", codigoEnvio);
            log.info("[procesarRecepcion] JSON recibido: {}", json);

            RecepcionJneRequestDto request = objectMapper.readValue(json,
                    RecepcionJneRequestDto.class);
            String usuario = request.getDniUsuarioCarga();
            String numeroOficio = request.getCarga().getNumeroOficio();

            List<RecepcionJne> recepciones = request.getCarga().getRecepciones();

            if (recepciones == null || recepciones.isEmpty()) {
                throw new JneRecepcionException("Recepciones vacías en la trama");
            }

            RecepcionJne recepcion = recepciones.get(0);

            TipoDocumentoJneEnum tipoDocumento = TipoDocumentoJneEnum
                    .fromCodigo(recepcion.getDocumento().getTipoDocumento());

            Oficio oficio = oficioRepository.findByNombreOficio(numeroOficio)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Oficio no encontrado: " + numeroOficio));

            log.info("[procesarRecepcion] Oficio {} tipoDocumento {}", numeroOficio, tipoDocumento);

            Archivo archivo = null;

            switch (tipoDocumento) {
            case ERROR:
                procesarError(oficio, recepcion, usuario);
                break;
            case EXPEDIENTE:
                procesarExpediente(oficio, recepcion, usuario);
                break;
            case RESOLUCION:
                archivo = procesarResolucion(oficio, pdf, request);
                break;
            default:
                throw new IllegalArgumentException("Tipo documento no soportado: " + tipoDocumento);
            }

            registrarTramaRecepcion(json, usuario, archivo, tipoDocumento, codigoEnvio);
            log.info("[procesarRecepcion] Oficio actualizado correctamente: {}", numeroOficio);
        } catch (Exception e) {
            log.error("[procesarRecepcion] Error procesando recepción Nación", e);
            throw new JneRecepcionException("Error procesando recepción Nación", e);
        }
    }

    private Archivo procesarResolucion(Oficio oficio, MultipartFile pdf,
            RecepcionJneRequestDto request) {
        if (pdf == null || pdf.isEmpty()) {
            throw new JneRecepcionException("Archivo PDF requerido para resolución");
        }

        RecepcionJne recepcion = request.getCarga().getRecepciones().get(0);
        String numeroResolucion = recepcion.getDocumento().getNroDocumento();
        String usuario = request.getDniUsuarioCarga();

        oficio.setEstado(EstadoOficioEnum.ATENDIDO.getCodigo());
        oficio.setEstadoJne(SceConstantes.ACTIVO);
        oficio.setFechaRespuesta(convertirFecha(recepcion.getDocumento().getFechaDocumento()));
        oficio.setUsuarioModificacion(usuario);
        oficio.setFechaModificacion(new Date());

        oficioRepository.save(oficio);

        List<DetActaOficio> detalles = detActaOficioRepository.findByOficioId(oficio.getId());

        Archivo archivo = archivoOrcService.guardarArchivo(pdf, request.getDniUsuarioCarga(),
                request.getCarga().getIdOdpe(), Optional.empty());

        Date now = new Date();

        detalles.forEach(det -> {
            det.setArchivoJne(archivo);
            det.setNumeroResolucionJne(numeroResolucion);
            det.setFechaModificacion(now);
            det.setUsuarioModificacion(usuario);
        });

        detActaOficioRepository.saveAll(detalles);

        log.info("Resolución {} registrada para oficio {}", numeroResolucion,
                oficio.getNombreOficio());

        return archivo;
    }

    private void procesarExpediente(Oficio oficio, RecepcionJne recepcion, String usuario) {
        String expediente = recepcion.getDocumento().getNroDocumento();

        if (!EstadoOficioEnum.ATENDIDO.getCodigo().equals(oficio.getEstado())) {
            oficio.setEstado(EstadoOficioEnum.EXPEDIENTE_GENERADO.getCodigo());
        }
        oficio.setEstadoJne(SceConstantes.ACTIVO);
        oficio.setFechaRespuesta(convertirFecha(recepcion.getDocumento().getFechaDocumento()));
        oficio.setUsuarioModificacion(usuario);
        oficio.setFechaModificacion(new Date());

        oficioRepository.save(oficio);

        List<DetActaOficio> detalles = detActaOficioRepository.findByOficioId(oficio.getId());

        Date now = new Date();

        detalles.forEach(det -> {
            det.setNumeroExpediente(expediente);
            det.setFechaModificacion(now);
            det.setUsuarioModificacion(usuario);
        });

        detActaOficioRepository.saveAll(detalles);

        log.info("Expediente {} registrado para oficio {}", expediente, oficio.getNombreOficio());
    }

    private void procesarError(Oficio oficio, RecepcionJne recepcion, String usuario) {
        Date now = new Date();

        oficio.setEstado(EstadoOficioEnum.RECHAZADO_JEE.getCodigo());
        oficio.setFechaRespuesta(convertirFecha(recepcion.getDocumento().getFechaDocumento()));
        oficio.setEstadoJne(SceConstantes.ACTIVO);
        oficio.setUsuarioModificacion(usuario);
        oficio.setFechaModificacion(now);

        oficioRepository.save(oficio);

        List<DetActaOficio> detalles = detActaOficioRepository.findByOficioId(oficio.getId());
        detalles.forEach(det -> {
            det.setObservacion(recepcion.getDocumento().getObservaciones());
            det.setFechaModificacion(now);
            det.setUsuarioModificacion(usuario);
        });

        detActaOficioRepository.saveAll(detalles);

        log.info("Oficio {} marcado como ERROR", oficio.getNombreOficio());
    }

    private void registrarTramaRecepcion(String tramaJson, String usuario, Archivo archivo,
            TipoDocumentoJneEnum tipoDocumento, String codigoEnvio) {

        if (jneTransmisionRecepcionRepository.existsByCodigoJneEnvio(codigoEnvio)) {
            log.warn("Transmisión ya registrada codigoEnvio={}", codigoEnvio);
            throw new JneRecepcionException(
                    "Transmisión ya registrada codigoEnvio :" + codigoEnvio);
        }

        Archivo archivoGuardar = TipoDocumentoJneEnum.RESOLUCION == tipoDocumento ? archivo : null;

        JneTransmisionRecepcion registro = JneTransmisionRecepcion.builder()
                .codigoJneEnvio(codigoEnvio)
                .trama(tramaJson).archivo(archivoGuardar).audUsuarioCreacion(usuario)
                .audFechaCreacion(new Date())
                .build();

        JneTransmisionRecepcion reg = jneTransmisionRecepcionRepository.save(registro);
        envioService.enviarRecepcionOrc(reg);
    }

    @Override
    public void reenviarPendientesJNE() {
        log.info("[reenviarPendientesJNE] Buscando tramas pendientes de envío...");
        List<JneTransmisionRecepcion> pendientes = jneTransmisionRecepcionRepository
                .findTop50Pendientes(
                        List.of(EstadoRecepcionJneEnum.PENDIENTE.getCodigo(),
                                EstadoRecepcionJneEnum.ERROR.getCodigo()),
                        Short.valueOf(SceConstantes.ACTIVO.toString()),
                        SceConstantes.MAX_INTENTOS_JNE, PageRequest.of(0, 10));

        if (pendientes.isEmpty()) {
            log.info("[reenviarPendientesJNE] No hay tramas pendientes");
            return;
        }

        log.info("[reenviarPendientesJNE] {} tramas encontradas para reenvío", pendientes.size());

        for (JneTransmisionRecepcion registro : pendientes) {
            try {
                log.info("[reenviarPendientesJNE] Evaluando id={}", registro.getId());
                boolean yaEnviado = registro.getEnviado() == EstadoRecepcionJneEnum.ENVIADO
                        .getCodigo();
                boolean maxIntentos = registro.getIntentos() != null
                        && registro.getIntentos() >= SceConstantes.MAX_INTENTOS_JNE;

                if (yaEnviado || maxIntentos) {
                    if (yaEnviado) {
                        log.debug("[reenviarPendientesJNE] Registro {} ya enviado",
                                registro.getId());
                    }

                    if (maxIntentos) {
                        log.warn("[reenviarPendientesJNE] Máximo intentos alcanzado id={}",
                                registro.getId());
                    }
                    continue;
                }
                log.info("[reenviarPendientesJNE] Reintentando envío id={}", registro.getId());
                envioService.enviarRecepcionOrc(registro);
            } catch (Exception e) {
                log.error("[reenviarPendientesJNE] Error reenviando id={}", registro.getId(), e);
            }
        }
    }

    private Date convertirFecha(String fecha) {
        return Date.from(LocalDate.parse(fecha).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
