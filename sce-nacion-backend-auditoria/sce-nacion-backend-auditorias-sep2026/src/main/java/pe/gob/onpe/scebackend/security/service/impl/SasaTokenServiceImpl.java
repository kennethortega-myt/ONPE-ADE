package pe.gob.onpe.scebackend.security.service.impl;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.scebackend.security.service.SasaTokenService;

@Service
@RequiredArgsConstructor
public class SasaTokenServiceImpl implements SasaTokenService {

    private final StringRedisTemplate redisTemplate;

    private static final String REDIS_PREFIX = "token:sasa:usuario:";
    private static final Duration TOKEN_DURATION = Duration.ofDays(2);

    @Override
    public void addToken(Integer usuarioId, String token) {
        if (usuarioId == null || token == null || token.isBlank()) {
            throw new IllegalArgumentException("Id de usuario y token son obligatorios");
        }

        redisTemplate.opsForValue().set(
                buildKey(usuarioId),
                token,
                TOKEN_DURATION);
    }

    @Override
    public Optional<String> getToken(Integer usuarioId) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(buildKey(usuarioId)));
    }

    @Override
    public void deleteToken(Integer usuarioId) {
        redisTemplate.delete(buildKey(usuarioId));
    }

    private String buildKey(Integer usuarioId) {
        return REDIS_PREFIX.concat(usuarioId.toString());
    }

}
