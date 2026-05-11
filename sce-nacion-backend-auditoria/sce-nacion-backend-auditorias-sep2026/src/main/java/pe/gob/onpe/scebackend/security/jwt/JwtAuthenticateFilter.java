package pe.gob.onpe.scebackend.security.jwt;

import java.io.IOException;
import java.util.Arrays;

import pe.gob.onpe.scebackend.exeption.entity.ExceptionResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

//@Component
//@Log4j2
//@RequiredArgsConstructor
public class JwtAuthenticateFilter {
//extends OncePerRequestFilter
//  public static final String HEADER_STRING = "Authorization";
//
//  public static final String BEARER_TOKEN_PREFIX = "Bearer ";
//
//  private final JwtAuthenticationProvider tokenProvider;
//
//  @Override
//  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//      throws ServletException, IOException {
//    response.setHeader("X-FRAME-OPTIONS", "DENY");
//    response.setHeader("Access-Control-Allow-OriginPatterns", "*");
//    response.setHeader("Access-Control-Allow-Methods", "GET,POST");
//    response.setHeader("Access-Control-Allow-Headers", "*");
//    response.setHeader("Access-Control-Allow-Credentials", "true");
//    response.setHeader("Access-Control-Max-Age", "36000");
//
//    if ("OPTIONS".equalsIgnoreCase((request).getMethod())) {
//      response.setHeader("Access-Control-Allow-Origin", "http://localhost:4200");
//      response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
//      response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
//      response.setHeader("Access-Control-Allow-Credentials", "true");
//      response.setStatus(HttpServletResponse.SC_OK);
//      return;
//    }
//
//    try {
//      String uri = request.getRequestURI();
//
//      // Manejo especial para rutas WebSocket
//      if (uri.startsWith(request.getContextPath() + "/ws")) {
//        filterChain.doFilter(request, response);
//        return;
//      }
//
//      if (esUrlLibre(uri, request)) {
//        filterChain.doFilter(request, response);
//      } else {
//        validarToken(request, response, filterChain);
//      }
//    } catch (ExpiredJwtException e) {
//      String isRefreshToken = request.getHeader("isRefreshToken");
//      String requestURL = request.getRequestURL().toString();
//      if (isRefreshToken != null && isRefreshToken.equals("true") && requestURL.contains("refreshtoken")) {
//        allowForRefreshToken(e, request);
//      } else {
//        request.setAttribute("exception", e);
//        log.error("El TOKEN ha expirado.", e);
//        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//        response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
//      }
//    } catch (UnsupportedJwtException e) {
//      log.error("No se puede detectar la clave del TOKEN.", e);
//      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//      response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
//    } catch (MalformedJwtException e) {
//      log.error("El TOKEN esta mal formado.", e);
//      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//      response.sendError(HttpServletResponse.SC_FORBIDDEN, e.getMessage());
//    } catch (Exception e) {
//      log.error("Exception : ", e);
//      validarError(response, "Ocurrió un error interno.");
//    }
//  }
//
//  private void validarToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//      throws IOException, ServletException {
//    if (checkJWTToken(request)) {
//
//      String jwt = getJWTFromRequest(request);
//      Claims claims = tokenProvider.validateAuthToken(jwt);
//      if (claims.get("dil") != null) {
//        Authentication auth = new UsernamePasswordAuthenticationToken(this.tokenProvider.getInfoToken(claims), null, null);
//        SecurityContextHolder.getContext().setAuthentication(auth);
//        filterChain.doFilter(request, response);
//      } else {
//        log.error("El TOKEN no cuenta con la clave principal.");
//        SecurityContextHolder.clearContext();
//        validarError(response, "El TOKEN no cuenta con la clave principal.");
//      }
//
//    } else {
//      log.error(request.getRequestURI() + ": Se requiere TOKEN para acceder al servicio.");
//      SecurityContextHolder.clearContext();
//      validarError(response, "Se requiere TOKEN para acceder al servicio.");
//      response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//    }
//  }
//
//  private void validarError(HttpServletResponse response, String mensaje) throws IOException {
//    ExceptionResponse error = new ExceptionResponse();
//    error.setMensaje(mensaje);
//    error.setMensajeInteno(mensaje);
//    error.setResultado(-1);
//
//    Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
//    String errorJson = gson.toJson(error);
//    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
//    response.setContentType("application/json");
//    response.setCharacterEncoding("UTF-8");
//    response.getWriter().write(errorJson);
//  }
//
//  private void allowForRefreshToken(ExpiredJwtException ex, HttpServletRequest request) {
//    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
//        null, null, null);
//    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
//    request.setAttribute("claims", ex.getClaims());
//  }
//
//  private boolean esUrlLibre(String url, HttpServletRequest request) {
//    String urlTmp = "";
//    for (String urlLibre : Arrays.asList(JwtConstant.getWhitelistPaths())) {
//      urlTmp = request.getContextPath() + urlLibre;
//      if (url.contains(urlTmp)) {
//        return true;
//      }
//    }
//    return false;
//  }
//
//  private String getJWTFromRequest(HttpServletRequest request) {
//    final String bearerToken = request.getHeader(HEADER_STRING);
//    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TOKEN_PREFIX)) {
//      return bearerToken.substring(BEARER_TOKEN_PREFIX.length());
//    }
//    throw new MalformedJwtException("Authentication Header doesn't have Bearer Token");
//  }
//
//  private boolean checkJWTToken(HttpServletRequest request) {
//    final String authenticationHeader = request.getHeader(HEADER_STRING);
//    return authenticationHeader != null && authenticationHeader.startsWith(BEARER_TOKEN_PREFIX);
//  }
}


