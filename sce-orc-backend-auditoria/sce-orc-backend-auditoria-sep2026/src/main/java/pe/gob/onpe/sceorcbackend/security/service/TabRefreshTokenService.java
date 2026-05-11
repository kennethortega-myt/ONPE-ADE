package pe.gob.onpe.sceorcbackend.security.service;

import pe.gob.onpe.sceorcbackend.model.common.CrudService;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.usuarios.JwtResponseDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabRefreshToken;

import java.util.Optional;

public interface TabRefreshTokenService extends CrudService<TabRefreshToken> {

    public TabRefreshToken createRefreshToken(String username, Integer plusRefreshMinutes);

    Optional<TabRefreshToken> findByUsuario(String usuario);

    Optional<TabRefreshToken> findByToken(String token);

    public TabRefreshToken verifyExpiration(TabRefreshToken token);

    GenericResponse<JwtResponseDTO> getNewToken(TokenInfo tokenInfo);
}
