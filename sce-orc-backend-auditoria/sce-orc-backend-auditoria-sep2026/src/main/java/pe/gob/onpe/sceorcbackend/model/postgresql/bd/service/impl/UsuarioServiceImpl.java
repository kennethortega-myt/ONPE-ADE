package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.sceorcbackend.exception.InternalServerErrorException;
import pe.gob.onpe.sceorcbackend.model.dto.TokenInfo;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteUsuarioJasperDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.SearchFilterResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.usuario.UsuarioResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.request.usuario.UsuarioUpdateRequestDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CentroComputo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.UsuarioTemporal;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.CentroComputoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.UsuarioRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.UsuarioTemporalRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.OrcDetalleCatalogoEstructuraService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UsuarioService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.usuario.UsuarioFilter;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.spec.usuario.UsuarioSpec;
import pe.gob.onpe.sceorcbackend.model.postgresql.dto.estructura.DetCatalogoEstructuraDTO;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import pe.gob.onpe.sceorcbackend.utils.ConstantesCatalogo;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesReportes;
import pe.gob.onpe.sceorcbackend.utils.TransactionalLogUtil;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

  private static final String CAMPO_FECHA_CREACION = "fechaCreacion";

  Logger logger = LoggerFactory.getLogger(UsuarioServiceImpl.class);
  private final UsuarioRepository usuarioRepository;
  private final UsuarioTemporalRepository usuarioTemporalRepository;
  private final CentroComputoRepository centroComputoRepository;
  private final UsuarioSpec spec;
  private final ITabLogService logService;
  private final TokenBlacklistService tokenBlacklistService;
  private final OrcDetalleCatalogoEstructuraService detalleCatalogoEstructuraService;
  private final UtilSceService utilSceService;

  @Override
  public void save(Usuario usuario) {
    this.usuarioRepository.save(usuario);
  }

  @Override
  @Transactional
  public UsuarioResponseDto update(Usuario usuario, UsuarioUpdateRequestDto usuarioDto, TokenInfo tokenInfo) {
    if (usuarioDto.getTipoDocumento() != null) {
      usuario.setTipoDocumento(usuarioDto.getTipoDocumento());
    }

    if (usuarioDto.getDocumento() != null) {
      usuario.setDocumento(usuarioDto.getDocumento());
    }

    // Validando Tipo Documento DNI
    if (usuario.getTipoDocumento().equals(1) && usuario.getDocumento().length() != 8) {
      throw new IllegalArgumentException("El tipo de documento dni debe tener 8 caracteres");
    }

    // Validando Tipo Documento CARNET EXTRANGERIA
    if (usuario.getTipoDocumento().equals(2) && usuario.getDocumento().length() != 9) {
      throw new IllegalArgumentException("El tipo de documento carnet extranjería debe tener 9 caracteres");
    }

    if (usuarioDto.getApellidoPaterno() != null) {
      usuario.setApellidoPaterno(usuarioDto.getApellidoPaterno().trim().toUpperCase());
    }

    if (usuarioDto.getApellidoMaterno() != null) {
      usuario.setApellidoMaterno(usuarioDto.getApellidoMaterno().trim().toUpperCase());
    }

    if (usuarioDto.getNombres() != null) {
      usuario.setNombres(usuarioDto.getNombres().trim().toUpperCase());
    }

    if (usuarioDto.getActivo() != null) {
      usuario.setActivo(usuarioDto.getActivo());
    }

    if (usuarioDto.getCorreo() != null) {
      usuario.setCorreo(usuarioDto.getCorreo());
    }

    usuario.setPersonaAsignada(1);

    usuario.setFechaModificacion(new Date());
    usuario.setUsuarioModificacion(tokenInfo.getNombreUsuario());

    var usuarioResponseDto = this.convertirUsuarioDto(this.usuarioRepository.save(usuario));

    logService.registrarLog(
        tokenInfo.getNombreUsuario(),
        Thread.currentThread().getStackTrace()[1].getMethodName(),
        String.format("El usuario %s editó los datos del usuario %s", tokenInfo.getNombreUsuario(), usuario.getUsuario()),
        tokenInfo.getCodigoCentroComputo(),
        ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO,
        ConstantesComunes.LOG_TRANSACCIONES_ACCION);

    return usuarioResponseDto;
  }

  @Override
  @Transactional
  public void updateDesincronizado(Long idUsuario, Boolean desincronizadoSasa) {
    this.usuarioRepository.updateDesincronizado(idUsuario, desincronizadoSasa);
  }

  @Override
  public void saveAll(List<Usuario> k) {
    this.usuarioRepository.saveAll(k);
  }

  @Override
  public void deleteAll() {
    this.usuarioRepository.deleteAll();
  }

  @Override
  public List<Usuario> findAll() {
    return this.usuarioRepository.findAll();
  }

  @Override
  public Usuario findByUsername(String usuario) {
    return this.usuarioRepository.findByUsuario(usuario);
  }

  @Override
  public List<DetCatalogoEstructuraDTO> listPerfilesCentroComputo() {
    return detalleCatalogoEstructuraService.findByMaestroAndColumna(
        ConstantesCatalogo.MAE_PERFIL,
        ConstantesCatalogo.DET_PERFIL)
        .stream()
        .filter(cat -> !cat.getCodigoS().endsWith(ConstantesCatalogo.PERFIL_NACION_ENDS_WITH))
        .toList();
  }

  @Override
  public List<DetCatalogoEstructuraDTO> listTiposDocumento() {
    return detalleCatalogoEstructuraService.findByMaestroAndColumna(
        ConstantesCatalogo.MAE_TIPO_DOCUMENTO_IDENTIDAD,
        ConstantesCatalogo.DET_TIPO_DOCUMENTO_IDENTIDAD);
  }

  @Override
  public GenericResponse<String> cerrarSession(String usuario) {
    GenericResponse<String> genericResponse = new GenericResponse<>();
    try {
        Usuario tabUsuario = this.usuarioRepository.findByUsuario(usuario);
        UsuarioTemporal tabUsuarioTemporal = this.usuarioTemporalRepository.findByUsuario(usuario);
        if (tabUsuario != null) {
            tabUsuario.setSesionActiva(0);
            tabUsuario.setUsuarioModificacion(usuario);
            tabUsuario.setFechaModificacion(new Date());
            this.usuarioRepository.save(tabUsuario);
        }

        if (tabUsuarioTemporal != null) {
            tabUsuarioTemporal.setSesionActiva(0);
            tabUsuarioTemporal.setUsuarioModificacion(usuario);
            tabUsuarioTemporal.setFechaModificacion(new Date());
            this.usuarioTemporalRepository.save(tabUsuarioTemporal);
        }

        genericResponse.setSuccess(true);
        genericResponse.setMessage("Sesión cerrada.");

    } catch (Exception e) {
        logger.error("Error al cerrar sesión: ", e);
        // Mensaje genérico
        genericResponse.setSuccess(true);
        genericResponse.setMessage("Sesión cerrada.");
    }

    return genericResponse;
  }

  @Override
  public SearchFilterResponse<UsuarioResponseDto> listPaginted(String usuario, Integer page, Integer size) {
    List<Sort.Order> orderList = new ArrayList<>();
    orderList.add(new Sort.Order(Direction.DESC, CAMPO_FECHA_CREACION));
    Sort sort = Sort.by(orderList);
    final Pageable pageable = PageRequest.of(page, size, sort);

    UsuarioFilter filter = UsuarioFilter.builder().usuario(usuario).build();
    Specification<Usuario> specLog = this.spec.filterSesionActiva(filter);

    Page<Usuario> usuarioPage = this.usuarioRepository.findAll(specLog, pageable);
    List<Usuario> usuarioResponse = usuarioPage.getContent();

    // Si no hay usuarios en tab_usuario, buscar en tab_usuario_temporal
    if (usuarioResponse.isEmpty()) {
      // Aplicar paginación también a temporales
      Page<UsuarioTemporal> temporalPage = this.usuarioTemporalRepository.findAll(pageable);

      // Convertir a Usuario
      usuarioResponse = temporalPage.getContent().stream()
              .filter(temporal -> temporal.getSesionActiva() == 1 && temporal.getUsuario().contains(usuario)) // ← Filtrar activos
              .map(this::convertirTemporalAUsuario)
              .toList();

      return new SearchFilterResponse<>(
              usuarioResponse.stream().map(this::convertirUsuarioDto).toList(),
              page <= 1 ? usuarioResponse.size() : (size * (page - 1)) + usuarioResponse.size(),
              page,
              temporalPage.getTotalElements(),
              temporalPage.getTotalPages()
      );
    }

    return new SearchFilterResponse<>(
            usuarioResponse.stream().map(this::convertirUsuarioDto).toList(),
            page <= 1 ? usuarioResponse.size() : (size * (page - 1)) + usuarioResponse.size(),
            page,
            usuarioPage.getTotalElements(),
            usuarioPage.getTotalPages()
    );
  }

  @Override
  public SearchFilterResponse<UsuarioResponseDto> listPaginatedAll(UsuarioFilter filter, Integer page, Integer size) {
    List<Sort.Order> orderList = new ArrayList<>();
    orderList.add(new Sort.Order(Direction.DESC, CAMPO_FECHA_CREACION));
    Sort sort = Sort.by(orderList);
    final Pageable pageable = PageRequest.of(page, size, sort);
    Specification<Usuario> specLog = this.spec.filter(filter);

    Page<Usuario> usuarioPage = this.usuarioRepository.findAll(specLog, pageable);

    List<DetCatalogoEstructuraDTO> listPerfiles = this.listPerfilesCentroComputo();

    List<Usuario> usuarioResponse = usuarioPage.getContent();
    var listUsuarios = usuarioResponse.stream().map(u -> {
      var dtoUsuario = this.convertirUsuarioDto(u);
      var optPerfil = listPerfiles.stream().filter(p -> p.getCodigoS().equals(u.getPerfil())).findFirst();
      if (optPerfil.isPresent()) {
        dtoUsuario.setNombrePerfil(optPerfil.get().getNombre());
      }
      return dtoUsuario;
    }).toList();

    return new SearchFilterResponse<>(
        listUsuarios,
        page <= 1 ? usuarioResponse.size() : (size * (page - 1)) + usuarioResponse.size(),
        page,
        usuarioPage.getTotalElements(),
        usuarioPage.getTotalPages());
  }

  public UsuarioResponseDto convertirUsuarioDto(Usuario usuario) {
    return UsuarioResponseDto.builder()
        .id(usuario.getId())
        .usuario(usuario.getUsuario())
        .tipoDocumento(usuario.getTipoDocumento())
        .documento(usuario.getDocumento())
        .apellidoPaterno(usuario.getApellidoPaterno())
        .apellidoMaterno(usuario.getApellidoMaterno())
        .nombres(usuario.getNombres())
        .correo(usuario.getCorreo())
        .perfil(usuario.getPerfil())
        .acronimoProceso(usuario.getAcronimoProceso())
        .centroComputo(usuario.getCentroComputo())
        .nombreCentroComputo(usuario.getNombreCentroComputo())
        .sesionActiva(usuario.getSesionActiva())
        .actasAsignadas(usuario.getActasAsignadas())
        .actasAtendidas(usuario.getActasAtendidas())
        .activo(usuario.getActivo())
        .usuarioCreacion(usuario.getUsuarioCreacion())
        .fechaCreacion(usuario.getFechaCreacion())
        .usuarioModificacion(usuario.getUsuarioModificacion())
        .fechaModificacion(usuario.getFechaModificacion())
        .desincronizadoSasa(usuario.getDesincronizadoSasa())
        .personaAsignada(usuario.getPersonaAsignada())
        .build();
  }

    @Override
    public byte[] getReporteUsuariosPdf(UsuarioFilter filter, Integer page, Integer size, String user) throws JRException {
        Map<String, Object> parametrosReporte = new java.util.HashMap<>();
        String nombreReporte = "";
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON +  ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe
        parametrosReporte.put("url_imagen", imagen);
        parametrosReporte.put("sinValorOficial", this.utilSceService.getSinValorOficial());
        parametrosReporte.put("usuario", user);
        List<ReporteUsuarioJasperDto> lista = listReportAll(filter).stream().map(usuarioResponseDto -> {
            ReporteUsuarioJasperDto dto = new ReporteUsuarioJasperDto();
            dto.setUsuario(usuarioResponseDto.getUsuario());
            dto.setDocumento(String.join( " ", obtenerTipoDocumento(usuarioResponseDto.getTipoDocumento()), usuarioResponseDto.getDocumento()));
            dto.setApellidos(String.join(" ", usuarioResponseDto.getApellidoPaterno(), usuarioResponseDto.getApellidoMaterno(), usuarioResponseDto.getNombres()));
            dto.setCorreo(usuarioResponseDto.getCorreo());
            dto.setPerfil(usuarioResponseDto.getNombrePerfil());
            dto.setAsignado(verificarSiNo(usuarioResponseDto.getPersonaAsignada()));
            dto.setActivo(verificarSiNo(usuarioResponseDto.getActivo()));
            return dto;
        }).toList();
        nombreReporte = ConstantesComunes.REPORTE_LISTA_USUARIOS;
        parametrosReporte.put("tituloReporte", ConstantesReportes.TITULO_REPORTE_LISTA_USUARIOS);
        mapearCamposCabeceraReporte(filter,parametrosReporte);

        this.logService.registrarLog(filter.getUsuario(),
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                this.getClass().getName(),
                TransactionalLogUtil.crearMensajeLog(ConstantesReportes.TITULO_REPORTE_LISTA_USUARIOS),
                filter.getCentroComputo(),
                ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

        return Funciones.generarReporte(this.getClass(), lista, nombreReporte, parametrosReporte);
    }

    @Override
    public List<UsuarioResponseDto> listReportAll(UsuarioFilter filter) {
        List<Sort.Order> orderList = new ArrayList<>();
        orderList.add(new Sort.Order(Direction.DESC, CAMPO_FECHA_CREACION));
        Sort sort = Sort.by(orderList);
        Specification<Usuario> specLog = this.spec.filter(filter);

        List<Usuario> usuarioPage = this.usuarioRepository.findAll(specLog,sort);

        List<DetCatalogoEstructuraDTO> listPerfiles = this.listPerfilesCentroComputo();

        return usuarioPage.stream().map(u -> {
            var dtoUsuario = this.convertirUsuarioDto(u);
            var optPerfil = listPerfiles.stream().filter(p -> p.getCodigoS().equals(u.getPerfil())).findFirst();
            if (optPerfil.isPresent()) {
                dtoUsuario.setNombrePerfil(optPerfil.get().getNombre());
            }
            return dtoUsuario;
        }).toList();
    }

    @Override
    public List<Usuario> listUsuariosByPerfil(String perfil) {
        return this.usuarioRepository.findByPerfil(perfil);
    }

    private Usuario convertirTemporalAUsuario(UsuarioTemporal temporal) {
    return Usuario.builder()
            .id(temporal.getId())
            .usuario(temporal.getUsuario())
            .perfil(temporal.getPerfil())
            .sesionActiva(temporal.getSesionActiva())
            .usuarioCreacion(temporal.getUsuarioCreacion())
            .fechaCreacion(temporal.getFechaCreacion())
            .usuarioModificacion(temporal.getUsuarioModificacion())
            .fechaModificacion(temporal.getFechaModificacion())
            // Campos que no existen en UsuarioTemporal - valores por defecto
            .centroComputo(null)
            .actasAsignadas(0)
            .actasAtendidas(0)
            .activo(1)
            .idUsuario(null)
            .acronimoProceso(null)
            .nombreCentroComputo(null)
            .codigo1(null)
            .idPerfil(null)
            .codigo2(null)
            .nombres(null)
            .apellidoPaterno(null)
            .apellidoMaterno(null)
            .personaAsignada(null)
            .build();
  }

  @Override
  @Transactional
  public void resetSesionActiva(String username, TokenInfo tokenInfo) {
    try {

      Usuario tabUsuario = usuarioRepository.findByUsuario(username);
      UsuarioTemporal  tabUsuarioTemporal = null;
      String usuario;

      if(tabUsuario == null){

        tabUsuarioTemporal= usuarioTemporalRepository.findByUsuario(username);
        if(tabUsuarioTemporal == null){
          throw new InternalServerErrorException("Usuario no encontrado.");
        }else{
          usuario = tabUsuarioTemporal.getUsuario();
        }
      }else{
        usuario = tabUsuario.getUsuario();
      }

      String token = tokenBlacklistService.getActiveToken(usuario);
      if (token != null) {
        long ttl = tokenBlacklistService.getExpireToken(usuario);
        if (ttl > 0) {
          String message = String.format(ConstantesComunes.SESION_CERRADA_POR_ADMINISTRADOR,
              tokenInfo.getNombreUsuario());
          tokenBlacklistService.addToBlacklist(token, ttl, message);
        }
        // eliminar token activo del usuario en Redis
        tokenBlacklistService.removeFromRedis(usuario);
      }

      String refreshToken = tokenBlacklistService.getActiveRefreshToken(usuario);
      if (refreshToken != null) {
        long ttlRefreshToken = tokenBlacklistService.getExpireRefreshToken(usuario);
        if (ttlRefreshToken > 0) {
          String message = String.format(ConstantesComunes.SESION_CERRADA_POR_ADMINISTRADOR,
              tokenInfo.getNombreUsuario());
          tokenBlacklistService.addToBlacklist(refreshToken, ttlRefreshToken, message);
        }
        tokenBlacklistService.removeRefreshTokenFromRedis(usuario);
      }

      if(tabUsuario !=null) {
        tabUsuario.setSesionActiva(0);
        tabUsuario.setFechaModificacion(new Date());
        usuarioRepository.save(tabUsuario);
      }


      if(tabUsuarioTemporal !=null) {
        tabUsuarioTemporal.setSesionActiva(0);
        tabUsuarioTemporal.setFechaModificacion(new Date());
        usuarioTemporalRepository.save(tabUsuarioTemporal);
      }

      logService.registrarLog(
              tokenInfo.getNombreUsuario(),
              Thread.currentThread().getStackTrace()[1].getMethodName(),
              String.format("El usuario %s cerró la sesión del usuario %s", tokenInfo.getNombreUsuario(), usuario),
              tokenInfo.getCodigoCentroComputo(),
              ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO,
              ConstantesComunes.LOG_TRANSACCIONES_ACCION
      );

    } catch (Exception e) {
      throw new InternalServerErrorException("Error al inactivar la sesión del usuario.", e);
    }
  }


	@Override
	public boolean estaActivo(String username) {
		Usuario usuario = this.usuarioRepository.findByUsuario(username);
        if (usuario == null) {
            return false;
        }
        return usuario.getSesionActiva().equals(1);
	}

  @Override
  public List<Usuario> usuarioActivos() {
    return this.usuarioRepository.findBySesionActiva(ConstantesComunes.ACTIVO);
  }

  @Override
  public Long contarOtrosUsuarioActivos(String nombreUsuario){
    return this.usuarioRepository.countBySesionActivaAndUsuarioNot(ConstantesComunes.ACTIVO, nombreUsuario);
  }

  @Override
  public boolean validarSessionActiva(String nombreUsuario) {
    Usuario usuario =  this.usuarioRepository.findByUsuario(nombreUsuario);
    if(usuario!=null)
      return usuario.getSesionActiva().equals(ConstantesComunes.ACTIVO);
    else return true;
  }

    private void mapearCamposCabeceraReporte(
            UsuarioFilter filtro, Map<String, Object> parametrosReporte) {
        parametrosReporte.put("proceso", utilSceService.obtenerNombreProcesoByAcronimo(filtro.getAcronimoProceso()));
        parametrosReporte.put("asignado", verificarSiNo(filtro.getPersonaAsignada()));
        parametrosReporte.put("perfil", StringUtils.isBlank(filtro.getPerfil()) ? "TODOS" : filtro.getPerfil());
        parametrosReporte.put("version", utilSceService.getVersionSistema());

        Optional<CentroComputo> cc = centroComputoRepository.findByCodigo(filtro.getCentroComputo());
        parametrosReporte.put("centroComputo", cc.map(centroComputo -> centroComputo.getCodigo() + " - " + centroComputo.getNombre()).orElse(""));

    }

    private String verificarSiNo(Integer estado){
        if (Objects.isNull(estado)) {
            return "TODOS";
        }
        return estado == 1 ? "SI" : "NO";
    }

    public static String obtenerTipoDocumento(Integer codigo) {
      if(Objects.isNull(codigo)) return "";

      return switch (codigo) {
            case 1 -> "DNI";
            case 2 -> "CE";
            default -> "DESCONOCIDO";
        };
    }

}
