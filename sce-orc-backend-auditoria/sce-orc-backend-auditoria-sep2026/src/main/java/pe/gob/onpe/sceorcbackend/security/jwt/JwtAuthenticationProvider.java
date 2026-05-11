package pe.gob.onpe.sceorcbackend.security.jwt;

import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.security.TokenDecoder;
import pe.gob.onpe.sceorcbackend.security.dto.UserContext;

@AllArgsConstructor
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtAuthenticate jwtAuthenticate;
    private final JwtSettings jwtSettings;
    private final TokenDecoder tokenDecoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserContext context = jwtAuthenticate.authenticate(authentication);
        return new JwtAuthenticationToken(context, context.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }

    public Claims validateAuthToken(final String token) {
        // just parsing correctly means its valid
        return tokenDecoder.decodeToken(token);
    }

    public TokenInfo getInfoToken(final Claims claims){
        return this.tokenDecoder.getInfoToken(claims);
    }
}