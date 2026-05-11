package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.gob.onpe.sceorcbackend.exception.JneRecepcionException;
import pe.gob.onpe.sceorcbackend.model.dto.request.RecepcionJne;
import pe.gob.onpe.sceorcbackend.model.dto.request.RecepcionJneRequestDto;
import pe.gob.onpe.sceorcbackend.model.enums.EstadoOficioEnum;
import pe.gob.onpe.sceorcbackend.model.enums.TipoDocumentoJneEnum;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Archivo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.DetActaOficio;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.JneTransmisionRecepcion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Oficio;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.DetActaOficioRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.JneTransmisionRecepcionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.OficioRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ArchivoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.JneRecepcionService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesCatalogo;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

@Service
@Slf4j
@RequiredArgsConstructor
public class JneRecepcionServiceImpl implements JneRecepcionService {

    private final OficioRepository oficioRepository;
    private final DetActaOficioRepository detActaOficioRepository;
    private final JneTransmisionRecepcionRepository jneTransmisionRecepcionRepository;
    private final ArchivoService archivoService;
    private final ObjectMapper objectMapper;

    @Override
    public void procesarRecepcion(MultipartFile pdf, String json, String codigoEnvio) {
        try {
            log.info("Recepción Nación recibida");
            log.info("JSON recibido: {}", json);

            RecepcionJneRequestDto request = objectMapper.readValue(json, RecepcionJneRequestDto.class);
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
                    .orElseThrow(() -> new IllegalArgumentException("Oficio no encontrado: " + numeroOficio));

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
            log.info("Oficio actualizado correctamente: {}", numeroOficio);
        } catch (Exception e) {
            log.error("Error procesando recepción Nación", e);
            throw new JneRecepcionException("Error procesando recepción Nación", e);
        }
    }

    private Archivo procesarResolucion(Oficio oficio, MultipartFile pdf, RecepcionJneRequestDto request) {
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

        List<DetActaOficio> detalles = detActaOficioRepository.findByOficio_Id(oficio.getId());

        Archivo archivo = archivoService.guardarArchivo(pdf, request.getDniUsuarioCarga(),
                request.getCarga().getIdOdpe(),
                Optional.of(ConstantesCatalogo.CATALOGO_CODIGO_DOC_ELECTORAL_PR_ACTA_RESOLUCION));

        Date now = new Date();

        detalles.forEach(det -> {
            det.setArchivoJNE(archivo);
            det.setNumeroResolucionJNE(numeroResolucion);
            det.setFechaModificacion(now);
        });

        detActaOficioRepository.saveAll(detalles);

        log.info("Resolución {} registrada para oficio {}", numeroResolucion, oficio.getNombreOficio());
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

        List<DetActaOficio> detalles = detActaOficioRepository.findByOficio_Id(oficio.getId());

        Date now = new Date();

        detalles.forEach(det -> {
            det.setNumeroExpedienteJNE(expediente);
            det.setFechaModificacion(now);
        });

        detActaOficioRepository.saveAll(detalles);

        log.info("Expediente {} registrado para oficio {}", expediente, oficio.getNombreOficio());
    }

    private void procesarError(Oficio oficio, RecepcionJne recepcion, String usuario) {
        oficio.setEstado(EstadoOficioEnum.RECHAZADO_JEE.getCodigo());
        oficio.setEstadoJne(SceConstantes.ACTIVO);
        oficio.setFechaRespuesta(convertirFecha(recepcion.getDocumento().getFechaDocumento()));
        oficio.setUsuarioModificacion(usuario);
        oficio.setFechaModificacion(new Date());

        oficioRepository.save(oficio);
        log.info("Oficio {} marcado como ERROR", oficio.getNombreOficio());
    }

    private void registrarTramaRecepcion(String tramaJson, String usuario, Archivo archivo,
            TipoDocumentoJneEnum tipoDocumento, String codigoEnvio) {

        if (jneTransmisionRecepcionRepository.existsByCodigoJneEnvio(codigoEnvio)) {
            log.warn("Transmisión ya registrada codigoEnvio={}", codigoEnvio);
            throw new JneRecepcionException("Transmisión ya registrada codigoEnvio :" + codigoEnvio);
        }

        Archivo archivoGuardar = TipoDocumentoJneEnum.RESOLUCION == tipoDocumento ? archivo : null;

        JneTransmisionRecepcion registro = JneTransmisionRecepcion.builder().codigoJneEnvio(codigoEnvio)
                .trama(tramaJson).archivo(archivoGuardar).audUsuarioCreacion(usuario).audFechaCreacion(new Date())
                .build();

        jneTransmisionRecepcionRepository.save(registro);
    }

    private Date convertirFecha(String fecha) {
        return Date.from(LocalDate.parse(fecha).atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
}
