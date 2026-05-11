package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import pe.gob.onpe.sceorcbackend.model.dto.request.AutorizacionCCRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.AutorizacionCCResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.ConsultaEstadoSolicitudResponseDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Acta;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabAutorizacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.TabAutorizacionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AutorizacionCentroComputoService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;

@Service
public class AutorizacionCentroComputoServiceImpl implements AutorizacionCentroComputoService {

    private final TabAutorizacionRepository tabAutorizacionRepository;
    private final ActaRepository actaRepository;
    private final ITabLogService logService;

    public AutorizacionCentroComputoServiceImpl(TabAutorizacionRepository tabAutorizacionRepository,
                                                ActaRepository actaRepository,
                                                ITabLogService logService
                                                ) {
        this.tabAutorizacionRepository = tabAutorizacionRepository;
        this.actaRepository = actaRepository;
        this.logService = logService;
    }

    @Override
    public AutorizacionCCResponseDto recibirAutorizacion(AutorizacionCCRequestDto autorizacionCCRequestDto) {
        AutorizacionCCResponseDto responseDto = new AutorizacionCCResponseDto();
        Optional<TabAutorizacion> autorizacionOpt = this.validarAutorizacion(autorizacionCCRequestDto);

        if(autorizacionOpt.isPresent()){
            TabAutorizacion autorizacion = autorizacionOpt.get();
            responseDto.setSolicitudGenerada(Boolean.TRUE);

            if(autorizacion.getEstadoAprobacion().equals(ConstantesComunes.ESTADO_APROBADO)){
                responseDto.setAutorizado(Boolean.TRUE);
                responseDto.setMensaje("Autorizado");
                responseDto.setIdAutorizacion(autorizacion.getId().toString());
                autorizacion.setEstadoAprobacion(ConstantesComunes.ESTADO_EJECUTADA);
                this.tabAutorizacionRepository.save(autorizacion);
            }else{
                responseDto.setAutorizado(Boolean.FALSE);
                responseDto.setMensaje(generarMensajeAutorizacion(autorizacionCCRequestDto));
                responseDto.setIdAutorizacion("0");
            }

        }else{
            responseDto.setSolicitudGenerada(Boolean.FALSE);
            responseDto.setMensaje("No cuenta con ninguna solicitud pendiente de aprobación");
            responseDto.setIdAutorizacion("0");
        }

        return responseDto;
    }

    @Override
    public boolean crearSolicitudAutorizacion(AutorizacionCCRequestDto autorizacionCCRequestDto, String cc) {
        Optional<TabAutorizacion> autorizacionOpt = this.validarAutorizacion(autorizacionCCRequestDto);
        if(autorizacionOpt.isPresent()){
            return false;
        }

        TabAutorizacion nuevaAutorizacion = this.crearNuevaAutorizacion(autorizacionCCRequestDto);
        tabAutorizacionRepository.save(nuevaAutorizacion);

        logService.registrarLog(
                autorizacionCCRequestDto.getUsuario(),
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                this.generarDescripcionGenerica(autorizacionCCRequestDto),
                cc,
                ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_SI,
                ConstantesComunes.LOG_TRANSACCIONES_ACCION
        );
        return true;
    }

    @Override
    public ConsultaEstadoSolicitudResponseDto consultarEstadoSolicitud(AutorizacionCCRequestDto autorizacionCCRequestDto) {
        Optional<TabAutorizacion> autorizacionOpt = this.validarAutorizacion(autorizacionCCRequestDto);

        if (autorizacionOpt.isPresent()) {
            TabAutorizacion autorizacion = autorizacionOpt.get();
            String mensaje = this.descripcionPorTipoAutorizacion(
                    autorizacion.getTipoAutorizacion(),
                    autorizacion.getEstadoAprobacion()
            );

            return new ConsultaEstadoSolicitudResponseDto(true, mensaje);
        }

        return new ConsultaEstadoSolicitudResponseDto(false, "No cuenta con ninguna solicitud generada.");
    }

    private String descripcionPorTipoAutorizacion(String tipoAutorizacion, String estado){
        if (ConstantesComunes.ESTADO_APROBADO.equals(estado)) {
            return switch (tipoAutorizacion) {
                case ConstantesComunes.TIPO_AUTORIZACION_VER_AGRUPACION_POLITICA ->
                        "Su solicitud para visualizar los partidos políticos fue aprobada. Vuelva a buscar el acta para continuar.";
                default -> "Su solicitud fue aprobada.";
            };
        } else {
            return switch (tipoAutorizacion) {
                case ConstantesComunes.TIPO_AUTORIZACION_VER_AGRUPACION_POLITICA ->
                        "Su solicitud para visualizar los partidos políticos está pendiente de aprobación. Por favor, espere la respuesta del administrador y vuelva a buscar el acta.";
                default -> "Su solicitud está pendiente de aprobación.";
            };
        }
    }

    private String generarMensajeAutorizacion(AutorizacionCCRequestDto request) {
        String descripcion = switch (request.getTipoAutorizacion()) {
            case ConstantesComunes.TIPO_AUTORIZACION_VER_AGRUPACION_POLITICA ->
                    "Existe una solicitud pendiente de aprobación para visualizar los partidos políticos del acta. Por favor, espere.";

            default -> "Existe una solicitud pendiente. Por favor, espere.";
        };

        return ConstantesComunes.MENSAJE_SOLICITUD_USUARIO + request.getUsuario() + ": " + descripcion;
    }

    private Optional<TabAutorizacion> validarAutorizacion(AutorizacionCCRequestDto autorizacionCCRequestDto){
        List<String> estadosBuscados = new ArrayList<>();
        estadosBuscados.add(ConstantesComunes.ESTADO_APROBADO);
        estadosBuscados.add(ConstantesComunes.ESTADO_PENDIENTE);
        return this.tabAutorizacionRepository.findFirstByEstadoAprobacionInAndUsuarioCreacionAndTipoAutorizacionAndTipoDocumentoAndIdDocumentoAndActivo(
                estadosBuscados, autorizacionCCRequestDto.getUsuario(), autorizacionCCRequestDto.getTipoAutorizacion(),
                autorizacionCCRequestDto.getTipoDocumento(), autorizacionCCRequestDto.getIdDocumento(),
                ConstantesComunes.ACTIVO);
    }

    private TabAutorizacion crearNuevaAutorizacion(AutorizacionCCRequestDto requestDto){
        TabAutorizacion tabAutorizacion = new TabAutorizacion();
        tabAutorizacion.setEstadoAprobacion(ConstantesComunes.ESTADO_PENDIENTE);
        tabAutorizacion.setTipoAutorizacion(requestDto.getTipoAutorizacion());
        tabAutorizacion.setAutorizacion(1);
        tabAutorizacion.setActivo(1);
        tabAutorizacion.setTipoDocumento(requestDto.getTipoDocumento());
        tabAutorizacion.setIdDocumento(requestDto.getIdDocumento());

        String detalle = generarDescripcionGenerica(requestDto);
        tabAutorizacion.setDetalle(detalle);

        Date now = new Date();
        tabAutorizacion.setFechaCreacion(now);
        tabAutorizacion.setFechaModificacion(now);
        tabAutorizacion.setUsuarioCreacion(requestDto.getUsuario());

        return tabAutorizacion;
    }

    private String generarDescripcionGenerica(AutorizacionCCRequestDto requestDto){

        String descripcion = switch (requestDto.getTipoAutorizacion()){
            case ConstantesComunes.TIPO_AUTORIZACION_VER_AGRUPACION_POLITICA ->
                    String.format("para visualizar los partidos políticos del acta %s.", this.obtenerDatosActa(requestDto.getIdDocumento()));
            default -> "";
        };

        return ConstantesComunes.MENSAJE_SOLICITUD_USUARIO + requestDto.getUsuario() + " " + descripcion;
    }

    private String obtenerDatosActa(Long idActa){
        String datosActa= "";
        Optional<Acta> actaOpt = actaRepository.buscarActaActivaPorId(idActa);
        if (actaOpt.isPresent()){
            Acta acta = actaOpt.get();
            if(acta.getNumeroCopia() == null || acta.getDigitoChequeoEscrutinio() == null){
                datosActa = acta.getMesa().getCodigo();
            }else{
                datosActa = String.format("%s-%s%s", acta.getMesa().getCodigo(), acta.getNumeroCopia(), acta.getDigitoChequeoEscrutinio());
            }
        }
        return datosActa;
    }
}
