package pe.gob.onpe.scebackend.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import pe.gob.onpe.scebackend.model.entities.ConfiguracionProcesoElectoral;
import pe.gob.onpe.scebackend.model.orc.entities.TabLogTransaccional;
import pe.gob.onpe.scebackend.model.service.IMessageService;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.impl.AgrupacionPoliticaService;
import pe.gob.onpe.scebackend.model.service.impl.ConfiguracionProcesoElectoralService;
import pe.gob.onpe.scebackend.sasa.dto.LoginDatosOutputDto;
import pe.gob.onpe.scebackend.sasa.dto.LoginInputDto;
import pe.gob.onpe.scebackend.sasa.dto.LoginPerfilesOutputDto;
import pe.gob.onpe.scebackend.sasa.service.UsuarioServicio;
import pe.gob.onpe.scebackend.security.dto.LoginRequest;
import pe.gob.onpe.scebackend.security.dto.UserContext;
import pe.gob.onpe.scebackend.security.exceptions.AuthMethodNotSupportedException;
import pe.gob.onpe.scebackend.security.jwt.JwtTokenFactory;
import pe.gob.onpe.scebackend.security.utils.CryptoUtils;
import pe.gob.onpe.scebackend.utils.LoggingUtil;
import pe.gob.onpe.scebackend.utils.SceConstantes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class AjaxAuthenticationProvider implements AuthenticationProvider {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Autowired
  public UsuarioServicio usuarioService;

  @Autowired
  public ITabLogTransaccionalService iTabLogService;

  @Autowired
  public IMessageService messageService;
  
  @Autowired
  public AgrupacionPoliticaService agrupacionPoliticaService;
  
  @Autowired
  public ConfiguracionProcesoElectoralService procesoElectoralService;

  @Autowired
  public JwtTokenFactory tokenFactory;
  
  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
	  	logger.info("Se realiza la autenticacion");
	    Assert.notNull(authentication, "No authentication data provided");
	
	    BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
	    String username = (String) authentication.getPrincipal();
	    LoginRequest l = (LoginRequest) authentication.getCredentials();
	    LoginDatosOutputDto u;
	    String idSessionHash;
	
	    try {
	      //CryptoUtils c = CryptoUtils.getInstance(JwtConstant.key);
	    	
	      String idSession = getHeader(SceConstantes.HEADER_IDSESSION);
	      if(idSession==null){
	    	  throw new InsufficientAuthenticationException("El IdSession es requerido");  
	      }
		  
	      idSessionHash = enc.encode(idSession); 
	      LoginInputDto login = new LoginInputDto();
	      login.setUsuario(username);
	      login.setClave(l.getPassword()); // c.encrypt(l.getPassword())
	      u = usuarioService.accederSistema(login);
	      if (u == null) {
			  throw new InsufficientAuthenticationException("Usuario/contraseña incorrecto.");
		  } else if ((u.getResultado()==null || u.getResultado()<1) && u.getMensaje()!=null) {
			  throw new InsufficientAuthenticationException(u.getMensaje());
		  }else if(u.getMensaje()==null){
			  throw new InsufficientAuthenticationException("Usuario/contraseña incorrecto.");
		  } else if(u.getMensaje().equals("Usuario bloqueado")) {
	      	throw new InsufficientAuthenticationException("El usuario se encuentra bloqueado.");
	      } else if (u.getDatos().getPerfiles().get(0) == null) {
	        throw new InsufficientAuthenticationException("El usuario selecionado no cuenta con roles activos.");
		  } else if (!(u.getDatos().getPerfiles().get(0).getAbreviatura().equals(SceConstantes.PERFIL_ADM_NAC) 
				  || u.getDatos().getPerfiles().get(0).getAbreviatura().equals(SceConstantes.PERFIL_REPO_NAC)
				  || u.getDatos().getPerfiles().get(0).getAbreviatura().equals(SceConstantes.PERFIL_STAE))){
			  throw new InsufficientAuthenticationException("Verifique si el usuario tiene perfil autorizado.");
		  } else if (u.getDatos() == null ||
				  u.getDatos().getUsuario() == null ||
				  u.getDatos().getUsuario().getPersonaAsignada() == null ||
				  u.getDatos().getUsuario().getPersonaAsignada() <= 0) {
			  throw new InsufficientAuthenticationException("El Usuario no dispone de permisos suficientes.");
		  }
		} catch (Exception e) {
	      throw new AuthMethodNotSupportedException(e.getMessage());
	    }

		var res = usuarioService.buscarPorId(u.getDatos().getUsuario().getToken(), u.getDatos().getUsuario().getIdUsuario());
		Integer sesionUnica = res.getData().getSesionUnica();
	
	    List<GrantedAuthority> authorities = new ArrayList<>();
	
	    String abreviaturaPerfil = null;
	    LoginPerfilesOutputDto perfil = null;
	    String acronimoProceso = null;
	    String nombreCc = null;
	    String codigoCc = null;
	    if (u.getDatos() != null && u.getDatos().getUsuario() != null) {
	      acronimoProceso = u.getDatos().getUsuario().getAcronimoProceso();
	      nombreCc = u.getDatos().getUsuario().getNombreCentroComputo();
	      codigoCc = u.getDatos().getUsuario().getCodigoCentroComputo();
	    }
	    if (u.getDatos() != null && u.getDatos().getPerfiles() != null) {
	      perfil = u.getDatos().getPerfiles().get(0);
	      abreviaturaPerfil = perfil.getAbreviatura();
	    }
	
	    SimpleGrantedAuthority s = new SimpleGrantedAuthority(abreviaturaPerfil);
	    authorities.add(s);
	
	    UserContext userContext =
	        UserContext.create(CryptoUtils.getHash(username, "SHA1"), u.getDatos().getUsuario().getNombres(), authorities);
	    userContext.setUsernameSinEncriptar(username);
	    userContext.setUserId(u.getDatos().getUsuario().getIdUsuario());
	    userContext.setAcronimoProceso(acronimoProceso);
	    userContext.setCodigoCentroComputo(codigoCc);
	    userContext.setNombreCentroComputo(nombreCc);
	    userContext.setPerfilId(u.getDatos().getPerfiles().get(0).getIdPerfil());
		userContext.setClaveNueva(u.getDatos().getUsuario().getClaveNueva());
		userContext.setIdSession(idSessionHash);
		userContext.setSesionUnica(sesionUnica);
	    try {
	      TabLogTransaccional log = new TabLogTransaccional();
	      log.setUsuario(username);
		  log.setObservacion("El usuario "+username +" "+ messageService.getMessage("log_login"));
	      log.setAccion(1);
	      log.setAutorizacion(ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO);
	      log.setAmbitoElectoral(u.getDatos().getUsuario().getNombreAmbito());
	      log.setCentroComputo(nombreCc);
	      log.setFechaRegistro(new Date());
	      log.setData(
	          LoggingUtil.logTransactions(messageService.getMessage("log_login_service"), messageService.getMessage("log_login_function"),
	              ((LoginRequest) authentication.getCredentials()).getAdicional(),
	              messageService.getMessage("log_login_message"), messageService.getMessage("log_login_event_type")));
	      this.iTabLogService.save(log);
	      
	      this.cargarCandidatos();
	      
	    } catch (Exception e) {
	    	logger.error("Error", e);
	    }
	
	    return new UsernamePasswordAuthenticationToken(userContext, null, userContext.getAuthorities());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
  }
  
  private String getHeader(String name) {
	    ServletRequestAttributes attrs =
	        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
	    if (attrs == null) return null;
	    HttpServletRequest request = attrs.getRequest();
	    return request.getHeader(name);
  }
  
  
  private void cargarCandidatos(){
	  
	  try {
		  logger.info("Se inicio la carga de candidatos");
		  List<ConfiguracionProcesoElectoral> procesos = this.procesoElectoralService.listarVigentesYActivos();
		  Integer rpta = 0;
		  String mensaje = "";
		  Map<String, Object> resultado = null;
		  if(procesos!=null){
			for(ConfiguracionProcesoElectoral proceso:procesos){
				
				if(proceso.getEtapa()!=null && proceso.getEtapa().equals(ConstantesComunes.ETAPA_SIN_CARGA)){
					logger.info("En el esquema {} aun no se ha hecho la carga, se ignora la carga de candidatos", proceso.getNombreEsquemaPrincipal());
					continue;
				}
				
				if(proceso.getNombreEsquemaPrincipal()!=null){
					logger.info("Carga de candidatos en el esquema = {}", proceso.getNombreEsquemaPrincipal());
					resultado = this.agrupacionPoliticaService.cargarCandidatos(proceso.getNombreEsquemaPrincipal().toLowerCase(), rpta, mensaje);
					if(resultado!=null){
						logger.info("resultado de la ps de cargar candidatos: {}", resultado.get("po_resultado"));
						logger.info("mensaje final de la ps de cargar candidatos: {}", resultado.get("po_mensaje"));
					}
				} else {
					logger.info("El registro ID={} no tiene un proceso configurado", proceso.getId());
				}
			}
		  }
	  } catch (Exception e){
		  logger.error("Error al ejecutar el ps de carga de candidatos", e);
	  }
	  
	  
  }
  
}
