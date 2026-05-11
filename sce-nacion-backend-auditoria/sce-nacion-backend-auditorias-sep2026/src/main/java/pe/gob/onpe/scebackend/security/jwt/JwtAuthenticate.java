package pe.gob.onpe.scebackend.security.jwt;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import pe.gob.onpe.scebackend.security.dto.UserContext;


@Component
public class JwtAuthenticate {

	@Autowired
    private JwtSettings jwtSettings;

    public UserContext Authenticate(Authentication authentication) {
        RawAccessJwtToken rawAccessToken = (RawAccessJwtToken) authentication.getCredentials();
        Jws<Claims> jwsClaims = rawAccessToken.parseClaims(jwtSettings.getTokenSigningKey());
        Claims claims = jwsClaims.getPayload();
        String subject = claims.getSubject();
        List<String> scopes = claims.get("scopes", List.class);
        String name = claims.get("usr", String.class);
        String validator = claims.get("cll", String.class);
        List<GrantedAuthority> authorities = scopes.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return UserContext.create(subject, name, validator, authorities);
    }
	
}
