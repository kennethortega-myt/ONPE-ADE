package pe.gob.onpe.sceorcbackend.sasa.service;

import pe.gob.onpe.sceorcbackend.model.ActualizarNuevaClaveInputDto;
import pe.gob.onpe.sceorcbackend.model.dto.NuevaClaveInputDto;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.sasa.dto.BuscarPorIdOutputDto;
import pe.gob.onpe.sceorcbackend.sasa.dto.BuscarPorIdUsuarioOutputDto;
import pe.gob.onpe.sceorcbackend.sasa.dto.LoginDatosOutputDto;
import pe.gob.onpe.sceorcbackend.sasa.dto.LoginInputDto;

public interface SasaAuthService {

    LoginDatosOutputDto accederSistema(LoginInputDto input);

    GenericResponse<String> refreshToken(TokenInfo tokenInfo);

    GenericResponse<BuscarPorIdOutputDto> desbloquearUsuario(TokenInfo tokenInfo,
            BuscarPorIdUsuarioOutputDto usuarioData);

    GenericResponse<Boolean> actualizarContrasenia(ActualizarNuevaClaveInputDto actualizarNuevaClaveInputDto,
            String usuario);

    GenericResponse<Boolean> nuevaContraseniaSasa(TokenInfo tokenInfo,
            NuevaClaveInputDto actualizarNuevaClaveInputDto);

}
