package pe.gob.onpe.sceorcbackend.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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

import pe.gob.onpe.sceorcbackend.model.dto.ParametroDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.EstadoCentroComputoResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CentroComputo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ProcesoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabAutorizacion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.UsuarioTemporal;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.TokenBlacklistService;
import pe.gob.onpe.sceorcbackend.sasa.dto.LoginDatosOutputDto;
import pe.gob.onpe.sceorcbackend.sasa.dto.LoginInputDto;
import pe.gob.onpe.sceorcbackend.sasa.dto.LoginPerfilesOutputDto;
import pe.gob.onpe.sceorcbackend.sasa.service.SasaAuthService;
import pe.gob.onpe.sceorcbackend.security.dto.LoginRequest;
import pe.gob.onpe.sceorcbackend.security.dto.UserContext;
import pe.gob.onpe.sceorcbackend.security.exceptions.AuthMethodNotSupportedException;
import pe.gob.onpe.sceorcbackend.security.utils.CryptoUtils;
import pe.gob.onpe.sceorcbackend.utils.ConstantesCatalogo;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesParametros;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class AjaxAuthenticationProvider implements AuthenticationProvider {
	
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private final SasaAuthService sasaAuthService;

  private final CentroComputoService maeCentroComputoService;

  private final MaeProcesoElectoralService maeProcesoElectoralService;

  private final UsuarioService tabUsuarioService;

  private final TabAutorizacionService tabAutorizacionService;

  private final UsuarioTemporalService tabUsuarioTemporalService;
  
  private final AgrupacionPoliticaService agrupacionPoliticaService;

  private final CierreActividadesService cierreActividadesService;

  public final ITabLogService iTabLogService;

  public final IMessageService messageService;

  public final TokenBlacklistService tokenBlacklistService;
  private final AccesoPcService accesoPcService;
  private final ParametroService parametroService;
  
  @Value("${spring.jpa.properties.hibernate.default_schema}")
  private String esquema;

  public AjaxAuthenticationProvider(
		  SasaAuthService sasaAuthService,
		  CentroComputoService maeCentroComputoService, 
		  MaeProcesoElectoralService maeProcesoElectoralService,
	      UsuarioService tabUsuarioService, 
	      TabAutorizacionService tabAutorizacionService, 
	      UsuarioTemporalService tabUsuarioTemporalService,
	      AgrupacionPoliticaService agrupacionPoliticaService,
	      ITabLogService iTabLogService, 
	      IMessageService messageService,
        CierreActividadesService cierreActividadesService,
          TokenBlacklistService tokenBlacklistService,
          AccesoPcService accesoPcService,
          ParametroService parametroService) {
    this.sasaAuthService = sasaAuthService;
    this.maeCentroComputoService = maeCentroComputoService;
    this.maeProcesoElectoralService = maeProcesoElectoralService;
    this.tabUsuarioService = tabUsuarioService;
    this.tabAutorizacionService = tabAutorizacionService;
    this.tabUsuarioTemporalService = tabUsuarioTemporalService;
    this.agrupacionPoliticaService = agrupacionPoliticaService;
    this.iTabLogService = iTabLogService;
    this.messageService = messageService;
    this.cierreActividadesService = cierreActividadesService;
    this.tokenBlacklistService = tokenBlacklistService;
    this.accesoPcService = accesoPcService;
    this.parametroService = parametroService;
  }

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    Assert.notNull(authentication, "No authentication data provided");

    ParametroDto parametroDto = parametroService.obtenerParametro(ConstantesParametros.CAB_PARAM_EJECUCION_PUESTA_CERO);
    if(parametroDto !=null && parametroDto.getValor()!=null && parametroDto.getValor().toString().equals(ConstantesComunes.TEXT_TRUE)) {
      throw new InsufficientAuthenticationException(
              "El sistema está realizando una puesta a cero. Por favor, intente ingresar en unos minutos."
      );
    }

    BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
    String username = (String) authentication.getPrincipal();
    LoginRequest l = (LoginRequest) authentication.getCredentials();
    LoginDatosOutputDto userDto;
    String idSessionHash;

    String ipAddress = (String) l.getAdicional().get("ipAddress");
    try {
    	
      String idSession = getHeader(SceConstantes.HEADER_IDSESSION);
	  if(idSession==null){
		  throw new InsufficientAuthenticationException("El IdSession es requerido");  
	  }
    	
	  idSessionHash = enc.encode(idSession); 
      userDto = validateUserCredentials(username, l.getPassword());
      validateUserRoles(userDto);
      validateProcesoElectoral(userDto);
      validateCentroComputo(userDto);

      Usuario tabUsuario = validateUsuarioSesion(username);
      if (tabUsuario != null) {
        handleUsuarioSesion(tabUsuario, username);
      } else {
        if(!tabUsuarioService.findAll().isEmpty()){
          throw new InsufficientAuthenticationException(
                  "El usuario no está registrado en el sistema. Contacte al administrador."
          );
        }
        handleUsuarioTemporal(username);
      }

      registrarAccesoSiEsPrimerLogin(username, ipAddress);

    } catch (InsufficientAuthenticationException e) {
      throw new AuthMethodNotSupportedException(e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      throw new AuthMethodNotSupportedException("Error interno del sistema.");
    }
    
    this.cargarCandidatos();

    try {
      this.iTabLogService.registrarLog(username,
              Thread.currentThread().getStackTrace()[1].getMethodName(),
              this.getClass().getName(),
              "El usuario "+ username +" ingresó al sistema.", userDto.getDatos().getUsuario().getCodigoCentroComputo(), 0, 1);
    } catch (Exception ex) {
      logger.error("Error al registrar el login por usuario {}", ex.getMessage());
    }

    return createAuthenticationToken(username, userDto, idSessionHash);
  }

  private LoginDatosOutputDto validateUserCredentials(String username, String password) throws InsufficientAuthenticationException {
    LoginInputDto loginInput = new LoginInputDto();
    loginInput.setUsuario(username);
    loginInput.setClave(password);
    LoginDatosOutputDto userDto = sasaAuthService.accederSistema(loginInput);

    if (userDto.getMensaje()==null){
      throw new InsufficientAuthenticationException("Usuario/contraseña incorrecto.");
    }
    if(userDto.getResultado() != null && userDto.getResultado().equals(ConstantesComunes.SASA_RESPUESTA_ERROR)) {
    	throw new InsufficientAuthenticationException(userDto.getMensaje());
    }
    if (userDto.getDatos() == null ||
            userDto.getDatos().getUsuario() == null ||
            userDto.getDatos().getUsuario().getPersonaAsignada() == null ||
            userDto.getDatos().getUsuario().getPersonaAsignada() <= 0) {
      throw new InsufficientAuthenticationException("El Usuario no dispone de permisos suficientes.");
    }

    if (userDto.getDatos().getUsuario().getAcronimoProceso() == null) {
      throw new InsufficientAuthenticationException("El Usuario no dispone de un proceso electoral configurado.");
    }

    return userDto;
  }

  private void validateUserRoles(LoginDatosOutputDto userDto) {
      List<LoginPerfilesOutputDto> perfiles = userDto.getDatos().getPerfiles();

      if (perfiles == null || perfiles.isEmpty()) {
          throw new InsufficientAuthenticationException("El usuario seleccionado no cuenta con roles activos.");
      }

      boolean tienePerfilProhibido = perfiles.stream()
              .anyMatch(perfil -> ConstantesComunes.PERFIL_USUARIO_ADMINISTRADOR_NAC.equals(perfil.getAbreviatura()));

      if (tienePerfilProhibido) {
          throw new InsufficientAuthenticationException("Verifique si el usuario tiene perfil autorizado.");
      }
  }

  private void validateCentroComputo(LoginDatosOutputDto userDto) {
    List<CentroComputo> centros = maeCentroComputoService.findAll();
    if (centros.isEmpty()) return;

    Optional<CentroComputo> optionalCentroComputo = centros.stream()
        .filter(c -> c.getCentroComputoPadre()!=null)
        .findFirst();

    if (optionalCentroComputo.isPresent()) {
      CentroComputo centroComputo = optionalCentroComputo.get();
      String centroComputoORC = centroComputo.getCodigo();

      String codigoCentroUsuario = userDto.getDatos().getUsuario().getCodigoCentroComputo();
      if (codigoCentroUsuario == null || !codigoCentroUsuario.equals(centroComputoORC)) {
        throw new InsufficientAuthenticationException(
            String.format("El usuario no pertenece al centro de cómputo %s-%s.",
                centroComputoORC, centroComputo.getNombre())
        );
      }
    }
  }

  private void validateProcesoElectoral(LoginDatosOutputDto userDto) {
    ProcesoElectoral procesoElectoral = maeProcesoElectoralService.findByActivo();
    if (procesoElectoral != null && !userDto.getDatos().getUsuario().getAcronimoProceso().equals(procesoElectoral.getAcronimo())) {
      throw new InsufficientAuthenticationException(String.format("El usuario no pertenece al proceso electoral %s.", procesoElectoral.getAcronimo()));
    }

  }

  private Usuario validateUsuarioSesion(String username) {
    return tabUsuarioService.findByUsername(username);
  }

  private void handleUsuarioSesion(Usuario usuario, String username) {

    String activeToken = tokenBlacklistService.getActiveToken(username);
    String activeRefreshToken = tokenBlacklistService.getActiveRefreshToken(username);

    if (usuario.getSesionActiva() == 1) {
      if (activeToken != null && !tokenBlacklistService.isBlacklisted(activeToken)) {
        Long ttl = tokenBlacklistService.getExpireToken(username);
        if (ttl != null && ttl > 0) {
          throw new InsufficientAuthenticationException("El usuario ya cuenta con una sesión activa. ¿Desea cerrar la sesión anterior?");
        }
      }
      if (activeRefreshToken != null && !tokenBlacklistService.isBlacklisted(activeRefreshToken)) {
          Long refreshTtl = tokenBlacklistService.getExpireRefreshToken(username);
          if (refreshTtl != null && refreshTtl > 0) {
            throw new InsufficientAuthenticationException("El usuario ya cuenta con una sesión activa. ¿Desea cerrar la sesión anterior?");
          }
      }

      //ambos tokens expiraron
        tokenBlacklistService.removeFromRedis(username);
        tokenBlacklistService.removeRefreshTokenFromRedis(username);
        updateSesionActivaInactiva(usuario, username, 0);
    }

    validateAutorizacionPuestaCero(usuario.getUsuario());
    updateSesionActivaInactiva(usuario, username, 1);

  }


  private void validateAutorizacionPuestaCero(String usuario) {
    if (usuario.startsWith(ConstantesComunes.PERFIL_USUARIO_VERIFICADOR) ||
        usuario.startsWith(ConstantesComunes.PERFIL_USUARIO_SCE_SCANNER) ||
        usuario.startsWith(ConstantesComunes.PERFIL_USUARIO_CONTROL_DIGITALIZACION)) {

      List<TabAutorizacion> autorizaciones = tabAutorizacionService.findByAutorizacionAndTipoAutorizacionAndActivo(
          ConstantesCatalogo.DET_CAT_EST_COD_AUTH_PCCC, ConstantesComunes.TIPO_AUTORIZACION_PUESTA_CERO, ConstantesComunes.ACTIVO);

      boolean hasPending = autorizaciones.stream()
          .anyMatch(auth -> auth.getEstadoAprobacion().equals(ConstantesComunes.ESTADO_PENDIENTE));

      boolean hasApproved = autorizaciones.stream()
          .anyMatch(auth -> auth.getEstadoAprobacion().equals(ConstantesComunes.ESTADO_APROBADO));

      if (hasPending) {
        throw new InsufficientAuthenticationException("Existe una Autorización de Puesta Cero pendiente de Aprobación.");
      }

      if (hasApproved) {
        throw new InsufficientAuthenticationException("Existe una Autorización de Puesta Cero pendiente de Ejecución.");
      }
    }
  }

  private void updateSesionActivaInactiva(Usuario tabUsuario, String username, int activa) {
    tabUsuario.setSesionActiva(activa);
    tabUsuario.setUsuarioModificacion(username);
    tabUsuario.setFechaModificacion(new Date());
    tabUsuarioService.save(tabUsuario);
  }


  private void updateSesionActivaInactivaTemporal(UsuarioTemporal tabUsuario, String username, int activa) {
    tabUsuario.setSesionActiva(activa);
    tabUsuario.setUsuarioModificacion(username);
    tabUsuario.setFechaModificacion(new Date());
    tabUsuarioTemporalService.save(tabUsuario);
  }


  private void handleUsuarioTemporal(String username) {
    if (!username.startsWith(ConstantesComunes.PERFIL_USUARIO_ADMINISTRADOR_CC)) {
      throw new InsufficientAuthenticationException("El usuario debe ser perfil Administrador de centro de Cómputo.");
    }

    String activeToken = tokenBlacklistService.getActiveToken(username);

    UsuarioTemporal usuarioTemporal = tabUsuarioTemporalService.findByUsername(username);
    if (usuarioTemporal == null) {
      usuarioTemporal = createUsuarioTemporal(username);
    } else {
      if (usuarioTemporal.getSesionActiva() == 1) {
        if (activeToken != null && !tokenBlacklistService.isBlacklisted(activeToken)) {
          Long ttl = tokenBlacklistService.getExpireToken(username);

          if (ttl != null && ttl > 0) {
            throw new InsufficientAuthenticationException("El usuario temporal ya cuenta con una sesión activa.");
          } else {
            tokenBlacklistService.removeFromRedis(username );
            updateSesionActivaInactivaTemporal(usuarioTemporal, username, 0);
          }
        } else {
          updateSesionActivaInactivaTemporal(usuarioTemporal, username, 0);
        }
      }
    }

    updateSesionActivaInactivaTemporal(usuarioTemporal, username, 1);

  }

  private UsuarioTemporal createUsuarioTemporal(String username) {
    UsuarioTemporal usuarioTemporal = new UsuarioTemporal();
    usuarioTemporal.setUsuario(username);
    usuarioTemporal.setUsuarioCreacion(ConstantesComunes.ABREV_NOMBRE_SISTEMA);
    usuarioTemporal.setSesionActiva(1);
    usuarioTemporal.setPerfil("ADMINISTRADOR CENTRO DE COMPUTO");
    usuarioTemporal.setFechaCreacion(new Date());
    tabUsuarioTemporalService.save(usuarioTemporal);
    return usuarioTemporal;
  }

  private Authentication createAuthenticationToken(String username, LoginDatosOutputDto userDto, String idSession) {
    List<GrantedAuthority> authorities = new ArrayList<>();
    String abreviaturaPerfil = userDto.getDatos().getPerfiles().getFirst().getAbreviatura();
    authorities.add(new SimpleGrantedAuthority(abreviaturaPerfil));



    UserContext userContext = UserContext.create(CryptoUtils.getHash(username, "SHA1"),
        userDto.getDatos().getUsuario().getNombres(), authorities);

    userContext.setUsernameSinEncriptar(username);
    userContext.setUserId(userDto.getDatos().getUsuario().getIdUsuario());
    userContext.setAcronimoProceso(userDto.getDatos().getUsuario().getAcronimoProceso());
    userContext.setCodigoCentroComputo(userDto.getDatos().getUsuario().getCodigoCentroComputo());
    userContext.setNombreCentroComputo(userDto.getDatos().getUsuario().getNombreCentroComputo());
    userContext.setPerfilId(userDto.getDatos().getPerfiles().get(0).getIdPerfil());
    userContext.setClaveNueva(userDto.getDatos().getUsuario().getClaveNueva());
    userContext.setIdSession(idSession);

    String codigoCentroComputo = userDto.getDatos().getUsuario().getCodigoCentroComputo();
    if(codigoCentroComputo!=null && !codigoCentroComputo.trim().isEmpty()){
      try {
        EstadoCentroComputoResponse estadoCentroComputo = cierreActividadesService.consultarEstadoCCAutenticacion(codigoCentroComputo);
        if(estadoCentroComputo!=null && estadoCentroComputo.isCerrado()){
          SimpleDateFormat sdf = new SimpleDateFormat(SceConstantes.PATTERN_DD_MM_YYYY_SLASHED);
          userContext.setFechaCierreCC(sdf.format(estadoCentroComputo.getFechaCierre()));
          userContext.setEstadoCentroComputo(1);
          userContext.setUsernameCierreCC(estadoCentroComputo.getUsuarioCierre());
        }
      } catch (Exception e) {
        logger.error("Error consultando estado centro cómputo: {}", e.getMessage());
      }
    }

    return new UsernamePasswordAuthenticationToken(userContext, null, userContext.getAuthorities());
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
  }
  
  private void cargarCandidatos() {
	  
	  try {
		  	long cantidad = this.maeProcesoElectoralService.contarTodos();
		  	if(cantidad>=1){
		  		logger.info("Se inicio la carga de candidatos");
				Integer rpta = 0;
				String mensaje = "";
				Map<String, Object> resultado = null;
				if(esquema!=null){
					logger.info("Carga de candidatos en el esquema = {}", esquema);
					resultado = this.agrupacionPoliticaService.cargarCandidatos(esquema.toLowerCase(), rpta, mensaje);
					if(resultado!=null){
						logger.info("resultado de la ps de cargar candidatos: {}", resultado.get("po_resultado"));
						logger.info("mensaje final de la ps de cargar candidatos: {}", resultado.get("po_mensaje"));
					}
				}
		  	}
	  } catch (Exception e){
		  logger.error("Error al ejecutar el ps de carga de candidatos", e);
	  }
	  
	
  }

  private void registrarAccesoSiEsPrimerLogin(String username, String ipAddress){
      try {
          if (ipAddress == null || ipAddress.trim().isEmpty()) {
              logger.warn("No se pudo registrar acceso: IP no disponible para usuario {}", username);
              return;
          }

          if(esIpLoopBack(ipAddress)){
              return;
          }

          // Verificar si es el primer login
          boolean esPrimerLogin = accesoPcService.esPrimerLogin(ipAddress);

          if (esPrimerLogin) {
              accesoPcService.registrarAcceso(username, ipAddress);
              logger.info("PRIMER LOGIN REGISTRADO - Usuario: {} | IP: {}", username, ipAddress);
          } else {
              logger.info("Login detectado (no es primer login) - Usuario: {} | IP: {}", username, ipAddress);
          }

      } catch (Exception e) {
          logger.error("Error al registrar acceso para usuario {}: {}", username, e.getMessage(), e);
          // No lanzar excepción para no bloquear el login
      }
  }

    private boolean esIpLoopBack(String ipAddress) {
        if (ipAddress == null || ipAddress.trim().isEmpty()) {
            return false;
        }
        try {
            return java.net.InetAddress.getByName(ipAddress.trim()).isLoopbackAddress();
        } catch (Exception e) {
            return false;
        }
    }
    
    private String getHeader(String name) {
	    ServletRequestAttributes attrs =
	        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
	    if (attrs == null) return null;
	    HttpServletRequest request = attrs.getRequest();
	    return request.getHeader(name);
    }
}
