package pe.gob.onpe.scebackend.model.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.exeption.GenericException;
import pe.gob.onpe.scebackend.model.dto.ResponseAutorizacionDTO;
import pe.gob.onpe.scebackend.model.dto.request.CargaDataRequestDTO;
import pe.gob.onpe.scebackend.model.dto.request.ConfiguracionProcesoElectoralRequestDTO;
import pe.gob.onpe.scebackend.model.dto.request.ProcesoElectoralOtherRequestDTO;
import pe.gob.onpe.scebackend.model.dto.response.*;
import pe.gob.onpe.scebackend.model.entities.ConfiguracionProcesoElectoral;
import pe.gob.onpe.scebackend.model.mapper.IConfiguracionProcesoElectoralMapper;
import pe.gob.onpe.scebackend.model.orc.entities.CentroComputo;
import pe.gob.onpe.scebackend.model.orc.entities.Usuario;
import pe.gob.onpe.scebackend.model.orc.repository.UsuarioRepository;
import pe.gob.onpe.scebackend.model.repository.ConfiguracionProcesoElectoralRepository;
import pe.gob.onpe.scebackend.model.repository.Procedures;
import pe.gob.onpe.scebackend.model.service.ICentroComputoService;
import pe.gob.onpe.scebackend.model.service.IConfiguracionProcesoElectoralService;
import pe.gob.onpe.scebackend.model.service.ITabAutorizacionService;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.sasa.dto.AplicacionUsuariosResponseDto;
import pe.gob.onpe.scebackend.sasa.dto.LoginDatosOutputDto;
import pe.gob.onpe.scebackend.sasa.dto.LoginInputDto;
import pe.gob.onpe.scebackend.sasa.service.UsuarioServicio;
import pe.gob.onpe.scebackend.utils.ConstantesAutorizacion;
import pe.gob.onpe.scebackend.utils.DateUtil;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ConfiguracionProcesoElectoralService implements IConfiguracionProcesoElectoralService {

  private static final String COD_SCE = "SCE";

  private final IConfiguracionProcesoElectoralMapper configuracionProcesoElectoralMapper;

  private final ConfiguracionProcesoElectoralRepository configuracionProcesoElectoralRepository;

  private final UsuarioRepository usuarioRepository;

  private final Procedures procedures;

  private final UsuarioServicio usuarioServicio;

  private final ITabAutorizacionService autorizacionService;

  private final ITabLogTransaccionalService logService;

  private final ICentroComputoService centroComputoService;


    @Override
  @Transactional("tenantTransactionManager")
  public ConfiguracionProcesoElectoralResponseDTO guardarConfiguracionProcesoElectoral(
      ConfiguracionProcesoElectoralRequestDTO procesoElectoralRequestDTO, String usuario) throws GenericException {
    ConfiguracionProcesoElectoral configuracionProcesoElectoral =
        this.configuracionProcesoElectoralMapper.dtoToProceso(procesoElectoralRequestDTO);

    try {

      ConfiguracionProcesoElectoral proceso =
          this.configuracionProcesoElectoralRepository.findByAcronimo(configuracionProcesoElectoral.getAcronimo().toUpperCase());
      ConfiguracionProcesoElectoral procesoEsquema = this.configuracionProcesoElectoralRepository.findByNombreEsquemaPrincipal(
          configuracionProcesoElectoral.getNombreEsquemaPrincipal().toUpperCase());
      if (Objects.nonNull(proceso) && !proceso.getId().equals(configuracionProcesoElectoral.getId())) {
        throw new GenericException("Proceso ya existe con el acrónimo: " + configuracionProcesoElectoral.getAcronimo());
      }
      if (Objects.nonNull(procesoEsquema) && !procesoEsquema.getId().equals(configuracionProcesoElectoral.getId())) {
        throw new GenericException("Proceso ya existe con el esquema principal: " + configuracionProcesoElectoral.getNombreEsquemaPrincipal());
      }
      List<ConfiguracionProcesoElectoral> procesos = this.configuracionProcesoElectoralRepository.findAll();
      boolean existeNombre =procesos.stream()
              .anyMatch(p ->
                      p.getNombre().equalsIgnoreCase(configuracionProcesoElectoral.getNombre())
                              && !p.getId().equals(configuracionProcesoElectoral.getId()));
      if (existeNombre) {
        throw new GenericException("Proceso ya existe con el mismo nombre: " + configuracionProcesoElectoral.getNombre());
      }
      LocalDate fechaActual = this.configuracionProcesoElectoralRepository.getCurrentDate();
      LocalDate fechaProceso = procesoElectoralRequestDTO.getFechaConvocatoria().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
      final ConfiguracionProcesoElectoral procesoElectoralExistente = procesos.stream().filter(p-> p.getId().
              equals(configuracionProcesoElectoral.getId())).findFirst().orElse(null);

        configuracionProcesoElectoral.setUsuarioCreacion(Objects.nonNull(procesoElectoralExistente) ? procesoElectoralExistente.getUsuarioCreacion() : usuario);
        configuracionProcesoElectoral.setUsuarioModificacion(Objects.nonNull(configuracionProcesoElectoral.getId()) && configuracionProcesoElectoral.getId() != 0  ? usuario : null);
        configuracionProcesoElectoral.setFechaModificacion(Objects.nonNull(configuracionProcesoElectoral.getId()) && configuracionProcesoElectoral.getId() != 0  ? new Date() : null);
        configuracionProcesoElectoral.setEtapa(Objects.nonNull(procesoElectoralExistente) ? procesoElectoralExistente.getEtapa() : 0);

      if (fechaProceso.isBefore(fechaActual) && !proceso.getId().equals(configuracionProcesoElectoral.getId())) {
        throw new GenericException("Fecha del proceso debe ser mayor o igual a la fecha actual ");
      }
      this.configuracionProcesoElectoralRepository.save(configuracionProcesoElectoral);

        try {
            String accion = Objects.nonNull(procesoElectoralExistente) ? "actualizó" : "creó";
            CentroComputo ccNacion = centroComputoService.getPadreNacion();
            this.logService.registrarLog(
                    usuario,
                    "guardarConfiguracionProcesoElectoral",
                    this.getClass().getSimpleName(),
                    String.format("El usuario %s %s la configuración del proceso electoral %s.",
                            usuario, accion, configuracionProcesoElectoral.getNombre()),
                    "",
                    ccNacion.getCodigo(),
                    ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO,
                    ConstantesComunes.LOG_TRANSACCIONES_ACCION
            );
        } catch (Exception e) {
            log.error("Error al registrar log: {}", e.getMessage());
        }

    } catch (Exception e) {
      throw new GenericException(e.getMessage());
    }
    return this.configuracionProcesoElectoralMapper.configuracionProcesoElectoralToDTO(configuracionProcesoElectoral);
  }

  @Override
  @Transactional("tenantTransactionManager")
  public List<ConfiguracionProcesoElectoralResponseDTO> listAll(Integer activo) {
    List<ConfiguracionProcesoElectoralResponseDTO> lista = this.configuracionProcesoElectoralRepository.findByActivoOrderById(activo).stream()
        .map(this.configuracionProcesoElectoralMapper::configuracionProcesoElectoralToDTO).
        collect(Collectors.toList());
    lista.forEach(config -> {
      config.setTipoEleccion(
          this.procedures.listarEleccionEsquema(config.getNombreEsquemaPrincipal().toLowerCase(), config.getUsuario()).stream()
              .map(tipo -> {
                DatosGeneralesResponseDto datos = new DatosGeneralesResponseDto();
                datos.setId(Objects.nonNull(tipo.getId()) ? tipo.getId().intValue() : 0);
                datos.setNombre(Objects.nonNull(tipo.getNombre()) ? tipo.getNombre() : "");
                datos.setPrincipal(tipo.getPrincipal());
                datos.setCodigo(tipo.getCodigo());
                return datos;
              }).collect(Collectors.toList()));
      config.getTipoEleccion().forEach(t->{
        if(t.getPrincipal() == 1){
          config.setExistePrincipal(true);

        }

      });
      config.setIsEditar(Boolean.TRUE);
    });
    return lista;
  }

  @Transactional("tenantTransactionManager")
  @Override
  public GenericResponse actualizarEstado(Integer estado, Integer id, String usuario) {
        GenericResponse response = new GenericResponse();
        response.setSuccess(Boolean.FALSE);
        try{
            final Integer diasParaEliminar = 1;
            ConfiguracionProcesoElectoral procesoElectoral = this.configuracionProcesoElectoralRepository.findById(id).orElse(null);
            if(Objects.isNull(procesoElectoral)){
                return response;
            }
            if(Boolean.FALSE.equals(DateUtil.puedeEditarOrEliminar(procesoElectoral.getFechaConvocatoria(), new Date(), diasParaEliminar))){
                response.setMessage("No se puede eliminar faltando " +diasParaEliminar + " días para la elección." );
                return response;
            }
            this.configuracionProcesoElectoralRepository.updateEstado(estado, id);
            response.setSuccess(Boolean.TRUE);
            try {
                CentroComputo ccNacion = centroComputoService.getPadreNacion();
                this.logService.registrarLog(
                        usuario,
                        "actualizarEstado",
                        this.getClass().getSimpleName(),
                        String.format("El usuario %s eliminó el proceso electoral.", usuario),
                        "",
                        ccNacion.getCodigo(),
                        ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO,
                        ConstantesComunes.LOG_TRANSACCIONES_ACCION
                );
            } catch (Exception e) {
                log.error("Error al registrar log: {}", e.getMessage());
            }
            return response;
        }catch (Exception e) {
            throw new GenericException(e.getMessage());
        }
  }

  @Override
  @Transactional("tenantTransactionManager")
  public GenericResponse cargardatos(CargaDataRequestDTO request) throws GenericException {
    GenericResponse response = new GenericResponse();

    try{
      boolean resp = this.procedures.executeProcedurecargaBDOnpe(request.getNombreDbLink(), request.getNombreEsquemaBdOnpe(),
              request.getNombreEsquemaPrincipal(), request.getIdProceso(), request.getUsuario());

      response.setSuccess(resp);
      return response;
    }catch (Exception e) {
      throw new GenericException(e.getMessage());
    }


  }

  @Override
  @Transactional("tenantTransactionManager")
  public String getEsquema(String proceso) {
    return this.configuracionProcesoElectoralRepository.getEsquema(proceso);
  }

  @Override
  public ConfiguracionProcesoElectoralResponseDTO getProcesoVigente() {
    List<ConfiguracionProcesoElectoralResponseDTO> lista = this.configuracionProcesoElectoralRepository.findByVigente(1).stream()
        .map(this.configuracionProcesoElectoralMapper::configuracionProcesoElectoralToDTO).toList();
    Optional<ConfiguracionProcesoElectoralResponseDTO> proceso = lista.stream().findFirst();
    return proceso.orElse(new ConfiguracionProcesoElectoralResponseDTO());
  }

  @Override
  @Transactional("tenantTransactionManager")
  public GenericResponse cargarUsuarios(String tentat, String user, String clave) throws GenericException {
    LoginDatosOutputDto u;
    GenericResponse response = new GenericResponse();
    response.setSuccess(Boolean.TRUE);
    try {
      ConfiguracionProcesoElectoral optional = this.configuracionProcesoElectoralRepository.findByAcronimo(tentat);
      if (Objects.nonNull(optional)) {
        ResponseAutorizacionDTO responseAutorizacionDTO = this.autorizacionService.verificarAutorizacion(optional.getFechaConvocatoria(), user, ConstantesAutorizacion.TIPO_AUTORIZACION_CARGA_USUARIOS,
                "la Carga de usuarios", String.format("Solicitud para la Carga de usuarios por el usuario %s", user));

        if(!responseAutorizacionDTO.isContinue()){
          response.setSuccess(Boolean.FALSE);
          response.setMessage(responseAutorizacionDTO.getMessage());
          return response;
        }
      }
      LoginInputDto login = new LoginInputDto();
      login.setUsuario(user);
      login.setClave(clave);
      login.setCodigo(COD_SCE);
      u = this.usuarioServicio.accederSistema(login);
      if (u == null) {
        throw new InsufficientAuthenticationException("Usuario/contraseña incorrecto.");
      }
      if (u.getDatos().getPerfiles().getFirst() == null) {
        throw new InsufficientAuthenticationException("El usuario selecionado no cuenta con roles activos.");
      }

      AplicacionUsuariosResponseDto responseList =
          this.usuarioServicio.listAplicacionUsuarios(COD_SCE,tentat, u.getDatos().getUsuario().getToken());
      if (Objects.isNull(responseList)) {
        throw new InsufficientAuthenticationException("token incorrecto");
      }

      this.usuarioRepository.deleteAll();

      List<Usuario> listaServicio = new ArrayList<>();
      if (!responseList.getLista().isEmpty()) {
          Set<String> usuariosUnicos = new HashSet<>();
          listaServicio = responseList.getLista().stream().map(usu -> {
          Usuario usuario = new Usuario();
          usuario.setIdUsuario(usu.getIdUsuario());
          usuario.setUsuario(usu.getUsuario());
          usuario.setNombres(usu.getNombres());
          usuario.setApellidoPaterno(usu.getApellidoPaterno());
          usuario.setApellidoMaterno(usu.getApellidoMaterno());
          usuario.setClave(usu.getCodigo1());
          usuario.setClaveTemporal(usu.getCodigo2());
          usuario.setPersonaAsignada(usu.getPersonaAsignada());
          usuario.setIdPerfil(usu.getIdPerfil());
          usuario.setPerfil(usu.getAbreviaturaPerfil());
          usuario.setCentroComputo(usu.getCodCentroComputo());
          usuario.setNombreCentroComputo(usu.getNombreCentroComputo());
          usuario.setAcronimoProceso(tentat);
          usuario.setActivo(1);
          usuario.setActasAsignadas(0);
          usuario.setSesionActiva(0);
          usuario.setActasAtendidas(0);
          usuario.setFechaCreacion(new Date());
          usuario.setUsuarioCreacion(user);
          usuario.setTipoDocumentoIdentidad(usu.getTipoDocumento());
          usuario.setDocumentoIdentidad(usu.getNumeroDocumento());
          usuario.setCorreos(usu.getCorreo());
          return usuario;
        }).filter(usuario -> usuariosUnicos.add(usuario.getUsuario())).toList();
      }
      this.usuarioRepository.saveAll(listaServicio);

    } catch (Exception e) {
      throw new GenericException(e.getMessage());
    }
    return response;
  }

  @Override
  @Transactional("tenantTransactionManager")
  public boolean actualizarPrincipal(CargaDataRequestDTO request) {
    return this.procedures.executeUpdateprincipal(request);
  }

  @Override
  @Transactional("tenantTransactionManager")
  public List<DetalleTipoEleccionDocumentoElectoralEscrutinioResponseDTO> listaTipoEleccionEscrutinio(
      ProcesoElectoralOtherRequestDTO request) {
    List<Map<String, Object>> mapList =
        this.configuracionProcesoElectoralRepository.listaTipoEleccionEscrutinio(request.getEsquema(), request.getIdEleccion(),
            request.getUsuario());
    return mapList.stream().map(tip -> {
          DetalleTipoEleccionDocumentoElectoralEscrutinioResponseDTO response =
              new DetalleTipoEleccionDocumentoElectoralEscrutinioResponseDTO();
          response.setId((Integer) tip.get("n_det_tipo_eleccion_documento_electoral_escrutinio_pk"));
          response.setEleccion((Integer) tip.get("n_eleccion"));
          response.setDistritoElectoral((Integer) tip.get("n_distrito_electoral"));
          response.setCodigoDistritoElectoral((String) tip.get("c_codigo_distrito_electoral"));
          response.setNombreDistritoElectoral((String) tip.get("c_nombre_distrito_electoral"));
          response.setDocumentoElectoral((Integer) tip.get("n_documento_electoral"));
          response.setAbreviaturaDocumentoElectoral((String) tip.get("c_abreviatura_documento_electoral"));
          response.setNombreDocumentoElectoral((String) tip.get("c_nombre_documento_electoral"));
          return response;
        }).sorted(Comparator.comparing(DetalleTipoEleccionDocumentoElectoralEscrutinioResponseDTO::getNombreDistritoElectoral))
        .toList();
  }

  @Override
  public List<ConfiguracionProcesoElectoral> listarActivos() {
	  return this.configuracionProcesoElectoralRepository.findByActivoOrderById(ConstantesComunes.ACTIVO);
  }


  @Override
  public List<ConfiguracionProcesoElectoral> listarVigentesYActivos() {
	  return this.configuracionProcesoElectoralRepository.findByVigenteAndActivoOrderById(
			  ConstantesComunes.ACTIVO, 
			  ConstantesComunes.ACTIVO);
  }

  @Override
  public void actualizarEtapaProceso(Integer idProceso, boolean isUpdate) {
      try{
        this.configuracionProcesoElectoralRepository.updateEtapa(isUpdate ? ConstantesComunes.ETAPA_SIN_CARGA:
                ConstantesComunes.ETAPA_CON_CARGA, idProceso, new Date());
      } catch (Exception e) {
        throw new GenericException(e.getMessage());
      }
  }
  
  @Override
  @Transactional("tenantTransactionManager")
  public Optional<ConfiguracionProcesoElectoral> findByAcronimo(Integer idProceso) {
	  return this.configuracionProcesoElectoralRepository.findById(idProceso);
  }
  
  @Override
  @Transactional("tenantTransactionManager")
  public ConfiguracionProcesoElectoral findByProceso(String proceso) {
    return this.configuracionProcesoElectoralRepository.findByProceso(proceso);
  }


}
