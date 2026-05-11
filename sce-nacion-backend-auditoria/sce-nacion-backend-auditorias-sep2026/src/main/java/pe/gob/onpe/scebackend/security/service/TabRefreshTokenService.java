package pe.gob.onpe.scebackend.security.service;



import java.util.Optional;


import pe.gob.onpe.scebackend.model.orc.entities.TabRefreshToken;
import pe.gob.onpe.scebackend.model.service.CrudService;
import pe.gob.onpe.scebackend.security.dto.GenericResponse;
import pe.gob.onpe.scebackend.security.dto.JwtResponseDTO;
import pe.gob.onpe.scebackend.security.dto.TokenInfo;

public interface TabRefreshTokenService extends CrudService<TabRefreshToken> {

    public TabRefreshToken createRefreshToken(String username, Integer plusRefreshMinutes);

    Optional<TabRefreshToken> findByUsuario(String usuario);

    Optional<TabRefreshToken> findByToken(String token);


    public TabRefreshToken verifyExpiration(TabRefreshToken token);

    GenericResponse<JwtResponseDTO> getNewToken(TokenInfo tokenInfo);
}
