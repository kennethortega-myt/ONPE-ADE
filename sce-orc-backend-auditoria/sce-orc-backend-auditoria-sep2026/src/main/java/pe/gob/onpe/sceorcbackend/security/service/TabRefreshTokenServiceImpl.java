package pe.gob.onpe.sceorcbackend.security.service;


import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.sceorcbackend.exception.TokenExpiredException;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.usuarios.JwtResponseDTO;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabRefreshToken;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.TabRefreshTokenRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.UsuarioRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.TokenBlacklistService;
import pe.gob.onpe.sceorcbackend.security.enums.Scopes;
import pe.gob.onpe.sceorcbackend.security.jwt.JwtTokenFactory;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TabRefreshTokenServiceImpl implements TabRefreshTokenService {

    private final TabRefreshTokenRepository tabRefreshTokenRepository;
    private final UsuarioRepository tabUsuarioRepository;
    private final JwtService jwtService;
    private final TokenBlacklistService tokenBlacklistService;
    private final JwtTokenFactory tokenFactory;

    public TabRefreshToken createRefreshToken(String usuario, Integer plusMinutes) {
        TabRefreshToken refreshToken = TabRefreshToken.builder()
                .userInfo(tabUsuarioRepository.findByUsuario(usuario))
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusSeconds((long)plusMinutes * 60))
                .build();

        return this.tabRefreshTokenRepository.save(refreshToken);
    }

    @Override
    public void save(TabRefreshToken tabRefreshToken) {
        this.tabRefreshTokenRepository.save(tabRefreshToken);
    }

    @Override
    public void saveAll(List<TabRefreshToken> k) {
        this.tabRefreshTokenRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.tabRefreshTokenRepository.deleteAll();
    }

    @Override
    public List<TabRefreshToken> findAll() {
        return this.tabRefreshTokenRepository.findAll();
    }

    @Override
    public Optional<TabRefreshToken> findByUsuario(String usuario) {
        return this.tabRefreshTokenRepository.findByUsuario(usuario);
    }

    @Override
    public Optional<TabRefreshToken> findByToken(String token) {
        return this.tabRefreshTokenRepository.findByToken(token);
    }


    public TabRefreshToken verifyExpiration(TabRefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            this.tabRefreshTokenRepository.delete(token);
            throw new TokenExpiredException(token.getToken() + " El token de actualización ha expirado. Inicie sesión nuevamente.!");
        }
        return token;
    }

    @Override
    public GenericResponse<JwtResponseDTO> getNewToken(TokenInfo tokenInfo) {

        if (tokenInfo.getScopes().isEmpty())
            return new GenericResponse<>(false, "El refresh token no cuenta con un scope.");

        List<String> scopes = tokenInfo.getScopes();
        List<String> scopeRefesrh = scopes.stream().filter(e -> e.equals(Scopes.REFRESH_TOKEN.authority())).toList();

        if (scopeRefesrh.isEmpty())
            return new GenericResponse<>(false, "El token enviado no corresponde a un REFRESH_TOKEN.");

        Usuario tabUsuario = tabUsuarioRepository.findByUsuario(tokenInfo.getNombreUsuario());
        if (tabUsuario == null)
            return new GenericResponse<>(false, "El usuario asociado al refresh token no se encuentra registrado.");

        String token = this.jwtService.createAccessJwtToken(tokenInfo);
        String refreshToken = this.jwtService.createRefreshJwtToken(tokenInfo);
        tokenBlacklistService.addTokenToRedis(tokenInfo.getNombreUsuario(), token, tokenFactory.getAccessTokenExpirationInSeconds());
        tokenBlacklistService.addRefreshTokenToRedis(tokenInfo.getNombreUsuario(), refreshToken, tokenFactory.getRefreshTokenExpirationInSeconds());
        JwtResponseDTO jwtResponseDTO = new JwtResponseDTO();
        jwtResponseDTO.setToken(token);
        jwtResponseDTO.setRefreshToken(refreshToken);
        return new GenericResponse<>(true, "El token y refresh token fueron actualizados con éxito.", jwtResponseDTO);

    }

}
