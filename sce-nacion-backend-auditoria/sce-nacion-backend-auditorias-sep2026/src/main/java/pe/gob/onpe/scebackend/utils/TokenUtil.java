package pe.gob.onpe.scebackend.utils;

import java.util.Date;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class TokenUtil {
    private TokenUtil() {
        throw new UnsupportedOperationException("TokenUtil es una clase utilitaria y no debe ser instanciada");
    }
    public static String generate(String documento, String tokenSigningKey) {
        return Jwts.builder().id("contigo").subject(documento)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSigningKey)), Jwts.SIG.HS512)
                .compact();
    }

    public static String generateSustento(String identificador, String hash, String tokenSigningKey) {
        return Jwts.builder().id(identificador).subject(hash)
                .issuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000000))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSigningKey)), Jwts.SIG.HS512)
                .compact();
    }

    public static Claims validateToken(String token, String tokenSigningKey) {
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSigningKey))).build()
                .parseSignedClaims(token).getPayload();
    }

}
