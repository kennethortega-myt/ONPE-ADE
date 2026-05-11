package pe.gob.onpe.sceorcbackend.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.PuestaCeroService;
import pe.gob.onpe.sceorcbackend.security.dto.UserContext;
import pe.gob.onpe.sceorcbackend.security.enums.Scopes;
import pe.gob.onpe.sceorcbackend.security.utils.DateUtils;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenFactory {

    SimpleDateFormat formatoFecha = new SimpleDateFormat(SceConstantes.PATTERN_DD_MM_YYYY_SLASHED);


    @Autowired
    private PuestaCeroService puestaCeroService;

    @Autowired
    private JwtSettings settings;

    public String createAccessJwtToken(UserContext userContext, String validator) {
        if (StringUtils.isBlank(userContext.getUsername()))
            throw new IllegalArgumentException("Cannot create JWT Token without username");

        if (userContext.getAuthorities() == null || userContext.getAuthorities().isEmpty())
            throw new IllegalArgumentException("User doesn't have any privileges");

        Map<String, Object> claimsMap = new HashMap<>();
        claimsMap.put("sub", userContext.getUsername());
        claimsMap.put("scopes", userContext.getAuthorities().stream()
                .map(Object::toString)
                .collect(Collectors.toList()));
        getClaims(userContext, validator, claimsMap);

        return Jwts.builder()
                .claims(claimsMap)
                .issuer(settings.getTokenIssuer())
                .id(UUID.randomUUID().toString())
                .issuedAt(DateUtils.asDate(LocalDateTime.now()))
                .expiration(DateUtils.asDate(LocalDateTime.now().plusMinutes(settings.getTokenExpirationTime())))
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(settings.getTokenSigningKey())),Jwts.SIG.HS512)
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
        claims.put("exepc", this.puestaCeroService.realizarPuestaCeroCentroComputo());
        claims.put("date", formatoFecha.format(new Date()));
        claims.put("cn", userContext.getClaveNueva());
        claims.put("ecc", userContext.getEstadoCentroComputo());
        claims.put("usrcc", userContext.getUsernameCierreCC());
        claims.put("datecc", userContext.getFechaCierreCC());


        List<String> roles = userContext.getAuthorities().stream()
                .map(grantedAuthority -> {
                    String authority = grantedAuthority.getAuthority();
                    return authority.startsWith("ROLE_") ? authority : "ROLE_" + authority;
                })
                .toList();
        claims.put("authorities", roles);
    }


    public long getAccessTokenExpirationInSeconds() {
        return this.settings.getTokenExpirationTime() * 60L ;
    }

    public long getRefreshTokenExpirationInSeconds() {
        return this.settings.getRefreshTokenExpTime() * 60L;
    }
}
