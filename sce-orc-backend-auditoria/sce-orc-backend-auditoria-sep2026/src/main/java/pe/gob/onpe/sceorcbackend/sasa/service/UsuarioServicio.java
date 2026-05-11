package pe.gob.onpe.sceorcbackend.sasa.service;

import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.request.usuario.UsuarioUpdateRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;
import pe.gob.onpe.sceorcbackend.sasa.dto.BuscarPorIdOutputDto;
import pe.gob.onpe.sceorcbackend.sasa.dto.CargarAccesoDatosOutputDto;
import pe.gob.onpe.sceorcbackend.sasa.dto.CargarAccesosInputDto;

public interface UsuarioServicio {

    CargarAccesoDatosOutputDto cargarAccesos(CargarAccesosInputDto input, String token);

    GenericResponse<BuscarPorIdOutputDto> buscarPorId(TokenInfo tokenInfo, Integer usuarioId);

    GenericResponse<Boolean> actualizarUsuario(TokenInfo tokenInfo, Usuario usuario,
            UsuarioUpdateRequestDto usuarioData);

    GenericResponse<Boolean> restablecerContrasenia(TokenInfo tokenInfo, Integer usuarioId);

}
