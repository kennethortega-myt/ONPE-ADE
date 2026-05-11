package pe.gob.onpe.scebackend.sasa.service;

import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.ActualizarNuevaClaveInputDto;
import pe.gob.onpe.scebackend.model.dto.NuevaClaveInputDto;
import pe.gob.onpe.scebackend.sasa.dto.*;
import pe.gob.onpe.scebackend.security.dto.GenericResponse;
import pe.gob.onpe.scebackend.security.dto.TokenInfo;

public interface UsuarioServicio {
	
  LoginDatosOutputDto accederSistema(LoginInputDto input) throws GenericException;

  CargarAccesoDatosOutputDto cargarAccesos(CargarAccesosInputDto input, String token ) throws GenericException;

  BuscarPorIdOutputDto buscarPorId(String tokenSasa, Integer usuarioId);

  void cerrarSesionActivaSasa(String usuario, String token) throws GenericException;

  AplicacionUsuariosResponseDto listAplicacionUsuarios(String codAplicacion, String codProceso, String token) throws GenericException;

  GenericResponse<Boolean> actualizarContrasenia(ActualizarNuevaClaveInputDto actualizarNuevaClaveInputDto, String usuario);

  GenericResponse<Boolean> nuevaContraseniaSasa(TokenInfo tokenInfo,
      NuevaClaveInputDto actualizarNuevaClaveInputDto);
}
