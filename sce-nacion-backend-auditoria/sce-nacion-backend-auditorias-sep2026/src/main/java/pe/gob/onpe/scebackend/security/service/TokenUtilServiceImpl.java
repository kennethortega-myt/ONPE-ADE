package pe.gob.onpe.scebackend.security.service;

import io.jsonwebtoken.Claims;
import pe.gob.onpe.scebackend.security.dto.TokenInfo;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.SceConstantes;

import org.springframework.stereotype.Service;


import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.function.Function;

@Service
public class TokenUtilServiceImpl implements TokenUtilService {

    private final TokenDecoder tokenDecoder;

    public TokenUtilServiceImpl(TokenDecoder tokenDecoder) {
        this.tokenDecoder = tokenDecoder;
    }


    @Override
    public TokenInfo getInfo(String authorization) {
        String token = authorization.substring(SceConstantes.LENGTH_BEARER);
        Claims claims = this.tokenDecoder.decodeToken(token);
        TokenInfo tokenInfo = this.tokenDecoder.getInfoToken(claims);
        tokenInfo.setTokenPlano(token.replace("\"", ""));
        return tokenInfo;
    }
    
    @Override
    public String getTimeExpired(String authorization) {
        String token = authorization.substring(SceConstantes.LENGTH_BEARER);
        Date fechaExpirationFin = extractExpiration(token);
        Date dateInicio = new Date();

        LocalDateTime localDateTimeFin = fechaExpirationFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        LocalDateTime localDateTimeInicio = dateInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        Duration duracion = Duration.between(localDateTimeInicio, localDateTimeFin);

        long horas = duracion.toHours();
        long minutos = duracion.toMinutes() % 60;
        long segundos = duracion.getSeconds() % 60;

        return "Restante: " + horas + " horas, " + minutos + " minutos, " + segundos + " segundos.";
    }
    
    @Override
    public long getTimeExpiredSeconds(String authorization) {
        String token = authorization.substring(SceConstantes.LENGTH_BEARER);
        Date fechaExpirationFin = extractExpiration(token);
        Date dateInicio = new Date();
        LocalDateTime localDateTimeFin = fechaExpirationFin.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime localDateTimeInicio = dateInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        Duration duracion = Duration.between(localDateTimeInicio, localDateTimeFin);
        return duracion.getSeconds(); // tiempo en segundos
    }
    
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = this.tokenDecoder.decodeToken(token);
        return claimsResolver.apply(claims);
    }
}
