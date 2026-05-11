package pe.gob.onpe.sceorcbackend.security;

import java.util.ArrayList;
import java.util.List;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.security.dto.LoginUserHeader;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class TokenDecoder {

    Logger logger = LoggerFactory.getLogger(TokenDecoder.class);

    @Value("${security.jwt.tokenSigningKey}")
    private String tokenSigningKey;

    public Claims decodeToken(String token) {
        return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSigningKey))).build()
                .parseSignedClaims(token.replace("\"", "")).getPayload();
    }

    public TokenInfo getUsuarioSession() {

        Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info("SecurityContextHolder.getContext().getAuthentication().getPrincipal(): {}",object);
        return (TokenInfo) object;
    }

    public LoginUserHeader getUserLogin(final String authorization){
        final LoginUserHeader user = new LoginUserHeader();
        if (authorization != null) {
            String token = authorization.substring(SceConstantes.LENGTH_BEARER);
            Claims claims = this.decodeToken(token);
            user.setPerfil(claims.get("per", String.class));
            user.setUsuario(claims.get("usr", String.class));
        }
        return user;
    }

    public TokenInfo getInfoToken(final Claims claims){
        String ccc = claims.get("ccc", String.class);
        String ncc = claims.get("ncc", String.class);
        String cambito = claims.get("cambito", String.class);
        String nambito = claims.get("nambito", String.class);
        String usr = claims.get("usr", String.class);
        String apr = claims.get("apr", String.class);
        String cll = claims.get("cll", String.class);
        String sub = claims.get("sub", String.class);
        String ids = claims.get("ids", String.class);
        List<?> rawScopes = claims.get("scopes", List.class);
        List<String> scopes = new ArrayList<>();

        if (rawScopes != null) {
            for (Object scope : rawScopes) {
                if (scope instanceof String value) {
                    scopes.add(value);
                }
            }
        }

        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setIdSession(ids);
        tokenInfo.setNombreUsuario(usr);
        tokenInfo.setCodigoCentroComputo(ccc);
        tokenInfo.setNombreCentroComputo(ncc);
        tokenInfo.setCodigoAmbito(cambito);
        tokenInfo.setNombreAmbito(nambito);
        tokenInfo.setAbrevProceso(apr);
        tokenInfo.setValidator(cll);
        tokenInfo.setIdPerfil(claims.get("idp", Integer.class));
        tokenInfo.setUserId(claims.get("dil", Integer.class));
        tokenInfo.setAutority(claims.get("per", String.class));
        tokenInfo.setSubject(sub);
        tokenInfo.setScopes(scopes);
        return tokenInfo;
    }

}
