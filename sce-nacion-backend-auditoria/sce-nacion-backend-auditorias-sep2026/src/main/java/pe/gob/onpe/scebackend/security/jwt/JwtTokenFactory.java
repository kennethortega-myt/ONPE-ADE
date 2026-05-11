package pe.gob.onpe.scebackend.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import pe.gob.onpe.scebackend.security.dto.UserContext;
import pe.gob.onpe.scebackend.security.enums.Scopes;
import pe.gob.onpe.scebackend.security.utils.DateUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenFactory {

    SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");

    @Autowired
    private JwtSettings settings;

    public String createAccessJwtToken(UserContext userContext, String validator) {
        if (StringUtils.isBlank(userContext.getUsername()))
            throw new IllegalArgumentException("Cannot create JWT Token without username");
        if (userContext.getAuthorities() == null || userContext.getAuthorities().isEmpty())
            throw new IllegalArgumentException("User doesn't have any privileges");

        Map<String, Object> claimsMap = new HashMap<>();
        // en este caso no encontrw que usara el SUB entiendo que asi deberia funcionar
        claimsMap.put("sub", userContext.getUsername());
        claimsMap.put("scopes", userContext.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toList()));
        // Anadir los claims adicionales desde el metodo getClaims )que tambien cambie(
        getClaims(userContext, validator, claimsMap);
        // Obtener la fecha actual y la de expiración
        Date now = DateUtils.asDate(LocalDateTime.now());
        Date expiration = DateUtils.asDate(LocalDateTime.now()
                .plusMinutes(settings.getTokenExpirationTime()));
        return Jwts.builder()
                .claims(claimsMap)
                .issuer(settings.getTokenIssuer())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(Keys.hmacShaKeyFor(
                                Decoders.BASE64.decode(settings.getTokenSigningKey())),
                        Jwts.SIG.HS512)
                .compact();
    }

    public String createRefreshToken(UserContext userContext, String validator) {
    	if (StringUtils.isBlank(userContext.getUsername()))
            throw new IllegalArgumentException("Cannot create JWT Token without username");

        if (userContext.getAuthorities() == null || userContext.getAuthorities().isEmpty())
            throw new IllegalArgumentException("User doesn't have any privileges");

        Map<String, Object> claimsMap = new HashMap<>();
        List<String> scopes = userContext.getAuthorities().stream().map(Object::toString).collect(Collectors.toList());
        claimsMap.put("sub", userContext.getUsername());
        scopes.add(Scopes.REFRESH_TOKEN.authority());
        claimsMap.put("scopes", scopes);
        getClaims(userContext, validator, claimsMap);
        return Jwts.builder()
                    .claims(claimsMap)
                .issuer(settings.getTokenIssuer())
                .id(UUID.randomUUID().toString())
                .issuedAt(DateUtils.asDate(LocalDateTime.now()))
                .expiration(DateUtils.asDate(LocalDateTime.now().plusMinutes(settings.getRefreshTokenExpTime())))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(settings.getTokenSigningKey())),Jwts.SIG.HS512)
                .compact();
    }

    public String verifyPosition(RawAccessJwtToken rawAccessToken) {
        Jws<Claims> jwsClaims = rawAccessToken.parseClaims(settings.getTokenSigningKey());
        return jwsClaims.getBody().get("cll", String.class);
    }

    private void getClaims(UserContext userContext, String validator, Map<String, Object> claims) {
        claims.put("usr", userContext.getUsernameSinEncriptar());
        claims.put("cll", validator);
        claims.put("dil", userContext.getUserId());
        claims.put("epd", userContext.getOdpe());
        claims.put("lac", userContext.getLocal());
        claims.put("idp", userContext.getPerfilId());
        claims.put("ids", userContext.getIdSession());
        claims.put("apr", userContext.getAcronimoProceso());
        claims.put("ncc", userContext.getNombreCentroComputo());
        claims.put("ccc", userContext.getCodigoCentroComputo());
        claims.put("per", userContext.getAuthorities() != null ? userContext.getAuthorities().getFirst().getAuthority() : null);
        claims.put("date", formatoFecha.format(new Date()));
        claims.put("cn", userContext.getClaveNueva());
        
        List<String> roles = userContext.getAuthorities().stream()
                .map(grantedAuthority -> {
                    String authority = grantedAuthority.getAuthority();
                    return authority.startsWith("ROLE_") ? authority : "ROLE_" + authority;
                })
                .toList();
        claims.put("authorities", roles);
        
    }

    public long getAccessTokenExpirationInSeconds() {
        return this.settings.getTokenExpirationTime() * 60L;
    }

    public long getRefreshTokenExpirationInSeconds() {
        return this.settings.getRefreshTokenExpTime() * 60L;
    }
}
