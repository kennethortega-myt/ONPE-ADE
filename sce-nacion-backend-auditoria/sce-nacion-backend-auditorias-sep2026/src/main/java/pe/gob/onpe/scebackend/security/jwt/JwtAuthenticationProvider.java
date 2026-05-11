package pe.gob.onpe.scebackend.security.jwt;

import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import pe.gob.onpe.scebackend.security.dto.TokenInfo;
import pe.gob.onpe.scebackend.security.dto.UserContext;

@AllArgsConstructor
@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtAuthenticate jwtAuthenticate;
    private final TokenDecoder tokenDecoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UserContext context = jwtAuthenticate.Authenticate(authentication);
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
