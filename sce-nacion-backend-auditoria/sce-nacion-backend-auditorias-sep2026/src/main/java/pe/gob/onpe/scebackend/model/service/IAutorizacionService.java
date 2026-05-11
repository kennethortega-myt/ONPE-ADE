package pe.gob.onpe.scebackend.model.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import pe.gob.onpe.scebackend.model.dto.AutorizacionDto;
import pe.gob.onpe.scebackend.model.dto.EstadoAprobacionDto;
import pe.gob.onpe.scebackend.model.dto.TipoAutorizacionDto;
import pe.gob.onpe.scebackend.model.dto.request.AutorizacionFilterRequestDto;
import pe.gob.onpe.scebackend.model.dto.request.AutorizacionNacionRequestDto;
import pe.gob.onpe.scebackend.model.dto.request.AutorizacionRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.AutorizacionNacionResponseDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;

public interface IAutorizacionService {
    List<AutorizacionDto> listAutorizaciones();
    List<AutorizacionDto> listAutorizaciones(AutorizacionFilterRequestDto filter);

    List<TipoAutorizacionDto> listTiposAutorizacionActivos();
    List<EstadoAprobacionDto> listEstadosAprobacionActivos();

    ResponseEntity<GenericResponse> aprobarAutorizacion(AutorizacionRequestDto filtro, String usuario);

    ResponseEntity<GenericResponse> rechazarAutorizacion(AutorizacionRequestDto filtro, String usuario);

    ResponseEntity<GenericResponse> crearSolicitudAutorizacion(AutorizacionNacionRequestDto request);

    ResponseEntity<AutorizacionNacionResponseDto> recibirAutorizacion(AutorizacionNacionRequestDto request);

    String generarDescripcionGenerica(AutorizacionNacionRequestDto request);
}