package pe.gob.onpe.scebackend.model.service.impl;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

@Service

public class TokenBlacklistService {

    private final StringRedisTemplate redisTemplate;

    public TokenBlacklistService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void addToBlacklist(String token, long expirationInSeconds) {
        redisTemplate.opsForValue().set(token, ConstantesComunes.BLACKLISTED_VALUE, expirationInSeconds,
                TimeUnit.SECONDS);
    }

    public void removeFromRedis(String username) {
        String key = ConstantesComunes.USER_TOKEN_PREFIX + username + ConstantesComunes.TOKEN_SUFFIX;
        redisTemplate.delete(key);
    }

    public Boolean isBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }

    public void addTokenToRedis(String username, String accessToken, long expirationSeconds) {
        String key = ConstantesComunes.USER_TOKEN_PREFIX + username + ConstantesComunes.TOKEN_SUFFIX;
        redisTemplate.opsForValue().set(key, accessToken, expirationSeconds, TimeUnit.SECONDS);
    }

    public String getActiveToken(String username) {
        String key = ConstantesComunes.USER_TOKEN_PREFIX + username + ConstantesComunes.TOKEN_SUFFIX;
        return redisTemplate.opsForValue().get(key);
    }

    public Long getExpireToken(String username) {
        String key = ConstantesComunes.USER_TOKEN_PREFIX + username + ConstantesComunes.TOKEN_SUFFIX;
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    public void removeRefreshTokenFromRedis(String username) {
        String key = ConstantesComunes.USER_TOKEN_PREFIX + username + ConstantesComunes.REFRESH_TOKEN_SUFFIX;
        redisTemplate.delete(key);
    }

    public void addRefreshTokenToRedis(String username, String accessToken, long expirationSeconds) {
        String key = ConstantesComunes.USER_TOKEN_PREFIX + username + ConstantesComunes.REFRESH_TOKEN_SUFFIX;
        redisTemplate.opsForValue().set(key, accessToken, expirationSeconds, TimeUnit.SECONDS);
    }

    public String getActiveRefreshToken(String username) {
        String key = ConstantesComunes.USER_TOKEN_PREFIX + username + ConstantesComunes.REFRESH_TOKEN_SUFFIX;
        return redisTemplate.opsForValue().get(key);
    }

    public Long getExpireRefreshToken(String username) {
        String key = ConstantesComunes.USER_TOKEN_PREFIX + username + ConstantesComunes.REFRESH_TOKEN_SUFFIX;
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

}
