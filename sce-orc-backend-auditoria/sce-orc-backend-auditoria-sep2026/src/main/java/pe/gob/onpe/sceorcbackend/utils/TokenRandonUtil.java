package pe.gob.onpe.sceorcbackend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import pe.gob.onpe.sceorcbackend.model.dto.ActaRandomClaimsDto;
import pe.gob.onpe.sceorcbackend.model.dto.PositionAgrupolClaimsDto;
import pe.gob.onpe.sceorcbackend.security.utils.DateUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class TokenRandonUtil {

    @Value("${security.jwt.tokenSigningKey}")
    private String tokenSigningKey;

    private TokenRandonUtil() {
    }

    public static String generateToken(ActaRandomClaimsDto input) {
        Date expirationDate = new Date(System.currentTimeMillis() + (100L * 365 * 24 * 60 * 60 * 1000));
        Date now = DateUtils.asDate(LocalDateTime.now());

        Map<String, Object> claims = new HashMap<>();
        claims.put("actaRandom", input);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date())
                .expiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SceConstantes.SECRET_KEY)), Jwts.SIG.HS512)
                .compact();
    }

    public static String generateTokenPositionAgrupol(PositionAgrupolClaimsDto input) {
        Date expirationDate = new Date(System.currentTimeMillis() + (100L * 365 * 24 * 60 * 60 * 1000));
        Date now = DateUtils.asDate(LocalDateTime.now());

        Map<String, Object> claims = new HashMap<>();
        claims.put("positionRandom", input);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SceConstantes.SECRET_KEY)), Jwts.SIG.HS512)
                .compact();
    }

    public static Claims decodeToken(String token) {
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(SceConstantes.SECRET_KEY))).build()
                .parseSignedClaims(token.replace("\"", "")).getPayload();


    }

}
