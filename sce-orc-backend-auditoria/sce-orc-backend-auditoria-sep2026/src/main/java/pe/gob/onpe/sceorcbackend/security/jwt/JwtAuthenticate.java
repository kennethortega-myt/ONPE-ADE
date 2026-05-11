package pe.gob.onpe.sceorcbackend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pe.gob.onpe.sceorcbackend.security.dto.UserContext;

import java.util.List;
import java.util.stream.Collectors;


@Component
public class JwtAuthenticate {

    private final JwtSettings jwtSettings;

    public JwtAuthenticate(JwtSettings jwtSettings) {
        this.jwtSettings = jwtSettings;
    }
    public UserContext authenticate(Authentication authentication) {
        RawAccessJwtToken rawAccessToken = (RawAccessJwtToken) authentication.getCredentials();
        Jws<Claims> jwsClaims = rawAccessToken.parseClaims(jwtSettings.getTokenSigningKey());
        Claims claims = jwsClaims.getPayload();
        String subject = claims.getSubject();
        @SuppressWarnings("unchecked")
		List<String> scopes = claims.get("scopes", List.class);
        String name = claims.get("usr", String.class);
        String validator = claims.get("cll", String.class);
        List<GrantedAuthority> authorities = scopes.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return UserContext.create(subject, name, validator, authorities);
    }

}
