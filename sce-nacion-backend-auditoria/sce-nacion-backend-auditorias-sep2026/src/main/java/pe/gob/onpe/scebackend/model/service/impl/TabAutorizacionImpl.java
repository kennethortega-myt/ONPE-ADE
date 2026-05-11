package pe.gob.onpe.scebackend.model.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.ParametroDto;
import pe.gob.onpe.scebackend.model.dto.ResponseAutorizacionDTO;
import pe.gob.onpe.scebackend.model.dto.response.AutorizacionNacionResponseDto;
import pe.gob.onpe.scebackend.model.orc.entities.TabAutorizacion;
import pe.gob.onpe.scebackend.model.orc.repository.TabAutorizacionRepository;
import pe.gob.onpe.scebackend.model.service.IParametroService;
import pe.gob.onpe.scebackend.model.service.ITabAutorizacionService;
import pe.gob.onpe.scebackend.model.service.ITabAutorizacionTransactionalHelper;
import pe.gob.onpe.scebackend.utils.DateUtil;
import pe.gob.onpe.scebackend.utils.SceConstantes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.enums.EstadoAutorizacion;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

@Service
@Slf4j
public class TabAutorizacionImpl implements ITabAutorizacionService {

    private final TabAutorizacionRepository tabAutorizacionRepository;

    private final IParametroService parametroService;

    private final ITabAutorizacionTransactionalHelper helper;

    public TabAutorizacionImpl(TabAutorizacionRepository tabAutorizacionRepository, IParametroService parametroService, ITabAutorizacionTransactionalHelper helper) {
        this.tabAutorizacionRepository = tabAutorizacionRepository;
        this.parametroService = parametroService;
        this.helper = helper;
    }

    @Override
    @Transactional(transactionManager = "locationTransactionManager", rollbackFor = Exception.class)
    public List<TabAutorizacion>listAutorizaciones() {
        List<TabAutorizacion> autorizaciones = this.tabAutorizacionRepository.findAllByActivoOrderByFechaModificacionDesc(1);
        if (!autorizaciones.isEmpty()) {
            autorizaciones.sort((a1, a2) -> {
                String estadoAprobacion1 = a1.getEstadoAprobacion();
                String estadoAprobacion2 = a2.getEstadoAprobacion();
                Date fechaUno = Objects.isNull(a1.getFechaModificacion()) ? a1.getFechaCreacion() : a1.getFechaModificacion();
                Date fechaDos = Objects.isNull(a2.getFechaModificacion()) ? a2.getFechaCreacion() : a2.getFechaModificacion();
                
                // 1. Pendientes primero
                if (ConstantesComunes.ESTADO_PENDIENTE.equals(estadoAprobacion1) && !ConstantesComunes.ESTADO_PENDIENTE.equals(estadoAprobacion2)) {
                    return -1;
                } else if (!ConstantesComunes.ESTADO_PENDIENTE.equals(estadoAprobacion1) && ConstantesComunes.ESTADO_PENDIENTE.equals(estadoAprobacion2)) {
                    return 1;
                }
                
                // 2. Aprobados segundo (después de pendientes)
                else if (ConstantesComunes.ESTADO_APROBADO.equals(estadoAprobacion1) && !ConstantesComunes.ESTADO_APROBADO.equals(estadoAprobacion2) 
                        && !ConstantesComunes.ESTADO_PENDIENTE.equals(estadoAprobacion2)) {
                    return -1;
                } else if (!ConstantesComunes.ESTADO_APROBADO.equals(estadoAprobacion1) && ConstantesComunes.ESTADO_APROBADO.equals(estadoAprobacion2)
                        && !ConstantesComunes.ESTADO_PENDIENTE.equals(estadoAprobacion1)) {
                    return 1;
                }
                
                // 3. Para el resto (Rechazados y Ejecutados), ordenar por fecha descendente
                else if ((ConstantesComunes.ESTADO_RECHAZADO.equals(estadoAprobacion1) || ConstantesComunes.ESTADO_EJECUTADA.equals(estadoAprobacion1))
                        && (ConstantesComunes.ESTADO_RECHAZADO.equals(estadoAprobacion2) || ConstantesComunes.ESTADO_EJECUTADA.equals(estadoAprobacion2))) {
                    return fechaDos.compareTo(fechaUno);
                }
                
                // 4. Mantener orden por fecha para casos del mismo estado
                else {
                    return fechaDos.compareTo(fechaUno);
                }
            });
            
            for (int i = 0; i < autorizaciones.size(); i++) {
                TabAutorizacion autorizacion = autorizaciones.get(i);
                autorizacion.setNumeroAutorizacion((long) (i + 1));
            }
        }
        return autorizaciones;
    }

    @Override
    public Optional<TabAutorizacion> validaAutorizacion(String usr, String tipoAutorizacion) {
        List<String> estadosBuscados = new ArrayList<>();
        estadosBuscados.add(ConstantesComunes.ESTADO_APROBADO);
        estadosBuscados.add(ConstantesComunes.ESTADO_PENDIENTE);
        return this.tabAutorizacionRepository.findFirstByEstadoAprobacionInAndUsuarioCreacionAndTipoAutorizacionAndActivo(
                estadosBuscados, usr, tipoAutorizacion, ConstantesComunes.ACTIVO);
    }

    @Override
    public Optional<TabAutorizacion> validaAutorizacion(String usr, String tipoAutorizacion, String tipoDocumento, Long idDocumento, String codigoCentroComputo ) {
        List<String> estadosBuscados = new ArrayList<>();
        estadosBuscados.add(ConstantesComunes.ESTADO_APROBADO);
        estadosBuscados.add(ConstantesComunes.ESTADO_PENDIENTE);
        return this.tabAutorizacionRepository.findFirstByEstadoAprobacionInAndUsuarioCreacionAndTipoAutorizacionAndTipoDocumentoAndIdDocumentoAndCodigoCentroComputoAndActivo(
                estadosBuscados, usr, tipoAutorizacion, tipoDocumento, idDocumento, codigoCentroComputo, ConstantesComunes.ACTIVO);
    }

    @Override
    public TabAutorizacion save(TabAutorizacion tab) {
        return this.tabAutorizacionRepository.save(tab);
    }

    @Override
    public Boolean aprobacionAutorizacion(Long id, String usuario) {
        try {
            TabAutorizacion tab = this.tabAutorizacionRepository.findById(id).orElse(null);
            if (tab != null) {
                tab.setEstadoAprobacion(ConstantesComunes.ESTADO_APROBADO);
                tab.setActivo(ConstantesComunes.ACTIVO);
                tab.setFechaModificacion(new Date());
                tab.setUsuarioModificacion(usuario);
                this.tabAutorizacionRepository.save(tab);
                return true;
            }else {
                return false;
            }
        }catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public Boolean rechazarAutorizacion(Long id, String usuario) {
        try {
            TabAutorizacion tab = this.tabAutorizacionRepository.findById(id).orElse(null);
            if (tab != null) {
                if (ConstantesComunes.ESTADO_PENDIENTE.equals(tab.getEstadoAprobacion())) {
                    tab.setEstadoAprobacion(ConstantesComunes.ESTADO_RECHAZADO);
                    tab.setActivo(ConstantesComunes.ACTIVO);
                    tab.setFechaModificacion(new Date());
                    tab.setUsuarioModificacion(usuario);
                    this.tabAutorizacionRepository.save(tab);
                    return true;
                } else {
                    log.warn("No se puede rechazar la autorización con ID: {} porque no está en estado pendiente. Estado actual: {}", id, tab.getEstadoAprobacion());
                    return false;
                }
            } else {
                log.warn("No se encontró la autorización con ID: {}", id);
                return false;
            }
        } catch (Exception e) {
            log.error("Error al rechazar autorización con ID: {}", id, e);
            return false;
        }
    }

    @Override
    @Transactional("locationTransactionManager")
    public ResponseAutorizacionDTO verificarAutorizacion(Date fechaConvocatoria, String usuario, String tipoAutorizacion, String titulo, String detalleAutorizacion) {
        ResponseAutorizacionDTO responseAutorizacionDTO = new
                ResponseAutorizacionDTO();
        responseAutorizacionDTO.setContinue(Boolean.TRUE);
        if(this.validarRequiereAutorizacion(fechaConvocatoria)){
            AutorizacionNacionResponseDto autorizacion = helper.recibirAutorizacion(usuario, tipoAutorizacion);
            responseAutorizacionDTO.setAutorizado(autorizacion.isAutorizado());
            if(!autorizacion.isSolicitudGenerada()){
                TabAutorizacion aut = helper.registrarAutorizacion(usuario, tipoAutorizacion, detalleAutorizacion);
                responseAutorizacionDTO.setContinue(Boolean.FALSE);
                responseAutorizacionDTO.setMessage(String.format("Se solicitó autorización para %s", titulo));
                responseAutorizacionDTO.setIdAutorizacion(aut.getId());
            }else{
                if(!autorizacion.isAutorizado()){
                    responseAutorizacionDTO.setMessage(String.format("Existe una solicitud pendiente que fue enviada a nación para aprobar %s, espere por favor.", titulo));
                    responseAutorizacionDTO.setContinue(Boolean.FALSE);
                }else{
                    responseAutorizacionDTO.setIdAutorizacion(Objects.nonNull(autorizacion.getIdAutorizacion()) ? Long.valueOf(autorizacion.getIdAutorizacion()) : null);
                }
            }
        }
        return responseAutorizacionDTO;
    }

    @Override
    public ResponseAutorizacionDTO verificarAutorizacionv2(Date fechaConvocatoria, String usuario, String tipoAutorizacion, String titulo, String detalleAutorizacion) throws GenericException {
        ResponseAutorizacionDTO responseAutorizacionDTO = new ResponseAutorizacionDTO();
        responseAutorizacionDTO.setContinue(Boolean.TRUE);
        if(EstadoAutorizacion.PERMITE_CON_AUTORIZACION.equals(this.validarRequiereAutorizacionV2(fechaConvocatoria))){
            AutorizacionNacionResponseDto autorizacion = helper.recibirAutorizacion(usuario, tipoAutorizacion);
            responseAutorizacionDTO.setAutorizado(autorizacion.isAutorizado());
            if(!autorizacion.isSolicitudGenerada()){
                TabAutorizacion aut = helper.registrarAutorizacion(usuario, tipoAutorizacion, detalleAutorizacion);
                responseAutorizacionDTO.setContinue(Boolean.FALSE);
                responseAutorizacionDTO.setMessage(String.format("Se solicitó autorización para %s", titulo));
                responseAutorizacionDTO.setIdAutorizacion(aut.getId());
            }else{
                if(!autorizacion.isAutorizado()){
                    responseAutorizacionDTO.setMessage(String.format("Existe una solicitud pendiente que fue enviada a nación para aprobar %s, espere por favor.", titulo));
                    responseAutorizacionDTO.setContinue(Boolean.FALSE);
                }else{
                    responseAutorizacionDTO.setIdAutorizacion(Objects.nonNull(autorizacion.getIdAutorizacion()) ? Long.valueOf(autorizacion.getIdAutorizacion()) : null);
                }
            }
        }else if(EstadoAutorizacion.NO_PERMITIDO.equals(this.validarRequiereAutorizacionV2(fechaConvocatoria))){
            responseAutorizacionDTO.setContinue(Boolean.FALSE);
            responseAutorizacionDTO.setMessage("La operación ya no esta permitida.");
        }
        return responseAutorizacionDTO;
    }

    private boolean validarRequiereAutorizacion(Date fechaConvocatoria){
        String hora = SceConstantes.HORA_DEFAULT_DIA_D;
        ParametroDto parametroDto = this.parametroService.obtenerParametro(SceConstantes.PARAMETRO_HORA_PROCESAMIENTO);
        if(Objects.nonNull(parametroDto)){
            hora = (String) parametroDto.getValor();
        }
        return DateUtil.beforeDiaD(fechaConvocatoria,hora);
    }

    private EstadoAutorizacion validarRequiereAutorizacionV2(Date fechaConvocatoria) {
        if (fechaConvocatoria == null) {
            return EstadoAutorizacion.NO_PERMITIDO;
        }

        String horaInicio = SceConstantes.HORA_DEFAULT_DIA_D; // por defecto 17:00
        ParametroDto p1 = this.parametroService.obtenerParametro(SceConstantes.PARAMETRO_HORA_PROCESAMIENTO);
        if (p1 != null && p1.getValor() instanceof String) {
            horaInicio = ((String) p1.getValor()).trim();
        }

        String horaFin = SceConstantes.HORA_DEFAULT_FIN_DIA_D;
        ParametroDto p2 = this.parametroService.obtenerParametro(SceConstantes.PARAMETRO_HORA_FIN_PROCESAMIENTO);
        if (p2 != null && p2.getValor() instanceof String) {
            horaFin = ((String) p2.getValor()).trim();
        }

        LocalDate fecha = fechaConvocatoria.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

        LocalTime h1 = LocalTime.parse(horaInicio);
        LocalTime h2 = LocalTime.parse(horaFin);

        LocalDateTime corteH1 = LocalDateTime.of(fecha, h1);
        LocalDateTime corteH2 = LocalDateTime.of(fecha, h2);
        LocalDateTime ahora   = LocalDateTime.now();

        if (ahora.isBefore(corteH1)) {
            return EstadoAutorizacion.REQUIERE_AUTORIZACION;
        } else if (ahora.isBefore(corteH2)) {
            return EstadoAutorizacion.PERMITE_CON_AUTORIZACION;
        } else {
            return EstadoAutorizacion.NO_PERMITIDO;
        }
    }
}
