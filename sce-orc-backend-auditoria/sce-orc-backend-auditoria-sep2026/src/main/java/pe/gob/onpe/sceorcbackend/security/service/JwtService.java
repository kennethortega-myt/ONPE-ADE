package pe.gob.onpe.sceorcbackend.security.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.PuestaCeroService;
import pe.gob.onpe.sceorcbackend.security.enums.Scopes;
import pe.gob.onpe.sceorcbackend.security.jwt.JwtSettings;
import pe.gob.onpe.sceorcbackend.security.utils.DateUtils;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class JwtService {

    private final JwtSettings settings;

    private final PuestaCeroService puestaCeroService;


    public JwtService(JwtSettings settings, PuestaCeroService puestaCeroService){
        this.settings = settings;
        this.puestaCeroService = puestaCeroService;
    }

    public String createAccessJwtToken(TokenInfo tokenInfo) {

        Map<String, Object> claims = getClaims(tokenInfo, false);
        Date expiration = DateUtils.asDate(LocalDateTime.now()
                .plusMinutes(settings.getTokenExpirationTime()));
        Date now = DateUtils.asDate(LocalDateTime.now());
        return Jwts.builder()
                .claims(claims)
                .issuer(settings.getTokenIssuer())
                .id(UUID.randomUUID().toString())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(settings.getTokenSigningKey())), Jwts.SIG.HS512)
                .compact();
    }

    public String createRefreshJwtToken(TokenInfo tokenInfo) {

        Map<String, Object> claims = getClaims(tokenInfo, true);
        Date expirationRfrsh = DateUtils.asDate(LocalDateTime.now()
                .plusMinutes(settings.getRefreshTokenExpTime()));
        Date nowRfrsh = DateUtils.asDate(LocalDateTime.now());
        return Jwts.builder()
                .claims(claims)
                .issuer(settings.getTokenIssuer())
                .id(UUID.randomUUID().toString())
                .issuedAt(nowRfrsh)
                .expiration(expirationRfrsh)
                .signWith(Keys.hmacShaKeyFor(Decoders.BASE64.decode(settings.getTokenSigningKey())), Jwts.SIG.HS512)
                .compact();
    }

    private Map<String, Object> getClaims(TokenInfo tokenInfo, boolean isRefresh) {
        SimpleDateFormat formatoFecha = new SimpleDateFormat(SceConstantes.PATTERN_DD_MM_YYYY_Z_SLASHED);
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", tokenInfo.getSubject());
        List<String> scopes = new ArrayList<>(tokenInfo.getScopes());
        if (!isRefresh)
            scopes.remove(Scopes.REFRESH_TOKEN.authority());
        claims.put("scopes", scopes);
        claims.put("usr", tokenInfo.getNombreUsuario());
        claims.put("cll", tokenInfo.getValidator());
        claims.put("dil", tokenInfo.getUserId());
        claims.put("idp", tokenInfo.getIdPerfil());
        claims.put("ids", tokenInfo.getIdSession());
        claims.put("apr", tokenInfo.getAbrevProceso());
        claims.put("ncc", tokenInfo.getNombreCentroComputo());
        claims.put("ccc", tokenInfo.getCodigoCentroComputo());
        claims.put("per", tokenInfo.getScopes() != null ? tokenInfo.getScopes().get(0) : null);
        claims.put("date", formatoFecha.format(new Date()));
        claims.put("exepc", this.puestaCeroService.realizarPuestaCeroCentroComputo());

        return claims;
    }
}
