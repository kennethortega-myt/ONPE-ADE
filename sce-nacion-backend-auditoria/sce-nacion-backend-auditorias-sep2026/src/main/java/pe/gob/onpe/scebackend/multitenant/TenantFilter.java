package pe.gob.onpe.scebackend.multitenant;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.Filter;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.SceConstantes;



@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@WebFilter("/*")
public class TenantFilter implements Filter {

	private static final String TENANT_HEADER = "X-Tenant-Id";
	private static final String AUTHORIZATION_HEADER = "Authorization";
	private static final String USERAGENT_HEADER = "User-Agent";
	private static final String USERAGENT_HEADER_VALUE = "ServerBackend";

	private static final String SCHEMA = "schema";
	
	@Autowired
	private TokenDecoder tokenDecoder;
	
	Logger logger = LogManager.getLogger(TenantFilter.class);

	@Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
		
		// Verificar la URL y determinar si debe ser ignorada
        String requestURI = ((HttpServletRequest) servletRequest).getRequestURI();
        logger.info("URL={}",requestURI);
        if ("/api/auth/login".equals(requestURI)) {
            // Ignorar la URL y continuar con la cadena de filtros
            chain.doFilter(servletRequest, servletResponse);
            return;
        }
		
		
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
		String userAgentHeader = request.getHeader(USERAGENT_HEADER);
		String schema= request.getHeader(SCHEMA);
		
		String tenantId = null;
		String proceso = null;
		String perfil = null;

		if(userAgentHeader!=null && userAgentHeader.equals(USERAGENT_HEADER_VALUE)) {
			
			logger.info("invocacion desde un servidor backend");
			
			if(request.getHeader(TENANT_HEADER)!=null && !request.getHeader(TENANT_HEADER).isEmpty()) {
				tenantId = request.getHeader(TENANT_HEADER);
			}
			
		} else {
			if(StringUtils.isNotBlank(request.getHeader(TENANT_HEADER))) {
				tenantId = request.getHeader(TENANT_HEADER);
			}
			
			logger.info("invocacion desde al lado del cliente");
			
			if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
	            String token = authorizationHeader.substring(7); // Elimina el prefijo "Bearer "
	            Claims claims = this.tokenDecoder.decodeToken(token);
	            proceso = claims.get("apr", String.class);
	            perfil  = claims.get("per", String.class);
	            logger.info("proceso={}",proceso);
	            logger.info("perfil={}",perfil);
	        }
			
			if(perfil!=null && perfil.equals(SceConstantes.PERFIL_ADM_NAC)) {
				logger.info("Es un usuario nacion");
				if(request.getHeader(TENANT_HEADER)!=null && !request.getHeader(TENANT_HEADER).isEmpty()) {
					tenantId = request.getHeader(TENANT_HEADER);
				} else {
					tenantId = proceso;
				}
			} else if(perfil!=null && perfil.equals(SceConstantes.PERFI_ADM_CC)) {
				logger.info("Es un usuario orc");
				tenantId = proceso;
			}
			if(StringUtils.isNotBlank(schema)){
				tenantId = schema;
			}
		}
		
		
		
		logger.info("tenantId={}",tenantId);
        try {
        	CurrentTenantId.set(tenantId);
			chain.doFilter(request, servletResponse);
        } finally {
        	CurrentTenantId.clear();
        }

    }
	
}
