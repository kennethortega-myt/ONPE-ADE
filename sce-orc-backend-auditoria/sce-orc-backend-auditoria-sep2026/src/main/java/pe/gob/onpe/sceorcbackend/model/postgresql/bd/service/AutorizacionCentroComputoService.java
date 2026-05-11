package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service;

import pe.gob.onpe.sceorcbackend.model.dto.request.AutorizacionCCRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.AutorizacionCCResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.ConsultaEstadoSolicitudResponseDto;

public interface AutorizacionCentroComputoService {
    AutorizacionCCResponseDto recibirAutorizacion(AutorizacionCCRequestDto autorizacionCCRequestDto);
    boolean crearSolicitudAutorizacion(AutorizacionCCRequestDto autorizacionCCRequestDto, String cc);
    ConsultaEstadoSolicitudResponseDto consultarEstadoSolicitud(AutorizacionCCRequestDto autorizacionCCRequestDto);
}
