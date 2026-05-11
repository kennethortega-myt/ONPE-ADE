package pe.gob.onpe.scebackend.security.service;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import pe.gob.onpe.scebackend.security.dto.TokenInfo;
import pe.gob.onpe.scebackend.security.enums.Scopes;
import pe.gob.onpe.scebackend.security.jwt.JwtSettings;
import pe.gob.onpe.scebackend.security.utils.DateUtils;

import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class JwtService {

    private final JwtSettings settings;


    public JwtService(JwtSettings settings){
        this.settings = settings;
    }

    public String createAccessJwtToken(TokenInfo tokenInfo) {

        Map<String, Object> claims = getClaims(tokenInfo, false);
        Date now = DateUtils.asDate(LocalDateTime.now());
        Date expiration = DateUtils.asDate(LocalDateTime.now()
                .plusMinutes(settings.getTokenExpirationTime()));

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

    private Map<String, Object> getClaims (TokenInfo tokenInfo, boolean isRefresh) {
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy z");
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
        claims.put("per", tokenInfo.getScopes() != null ? tokenInfo.getScopes().getFirst() : null);
        claims.put("date", formatoFecha.format(new Date()));
        return claims;
    }
}
