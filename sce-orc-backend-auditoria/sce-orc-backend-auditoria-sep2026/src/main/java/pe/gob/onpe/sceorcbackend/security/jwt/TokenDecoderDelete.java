package pe.gob.onpe.sceorcbackend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class TokenDecoderDelete {
	
	@Value("${security.jwt.tokenSigningKey}")
    private String tokenSigningKey;

	public Claims decodeToken(String token) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSigningKey)))
                .build()
                .parseSignedClaims(token.replace("\"", ""))
                .getPayload();
    }
	
}
