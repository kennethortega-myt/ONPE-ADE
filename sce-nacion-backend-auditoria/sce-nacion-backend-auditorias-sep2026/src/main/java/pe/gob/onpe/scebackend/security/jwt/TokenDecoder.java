package pe.gob.onpe.scebackend.security.jwt;

import java.util.ArrayList;
import java.util.List;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import pe.gob.onpe.scebackend.security.dto.LoginRequest;
import pe.gob.onpe.scebackend.security.dto.LoginUserHeader;
import pe.gob.onpe.scebackend.security.dto.TokenInfo;
import pe.gob.onpe.scebackend.security.dto.UserContext;
import pe.gob.onpe.scebackend.utils.SceConstantes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Component
public class TokenDecoder {

  @Value("${security.jwt.tokenSigningKey}")
  private String tokenSigningKey;

  public Claims decodeToken(String token) {
    return Jwts.parser().verifyWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(tokenSigningKey))).build()
            .parseSignedClaims(token.replace("\"", "")).getPayload();
  }

  public LoginRequest obtenerInfoUsuario() {
    LoginRequest usuarioDTO = (LoginRequest) SecurityContextHolder.getContext().getAuthentication().getCredentials();
    return usuarioDTO;
  }

  public TokenInfo getUsuarioSession() {
    return (TokenInfo) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
  }

  public UserContext getUsuarioSession2() {
    return (UserContext) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
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
    tokenInfo.setNombreUsuario(usr);
    tokenInfo.setIdSession(ids);
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
