package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import pe.gob.onpe.sceorcbackend.exception.BusinessValidationException;
import pe.gob.onpe.sceorcbackend.exception.GenericException;
import pe.gob.onpe.sceorcbackend.exception.InternalServerErrorException;
import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CentroComputo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.TabCierreCentroComputo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.CierreCentroComputoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.*;
import pe.gob.onpe.sceorcbackend.sasa.dto.LoginDatosOutputDto;
import pe.gob.onpe.sceorcbackend.sasa.dto.LoginInputDto;
import pe.gob.onpe.sceorcbackend.sasa.service.SasaAuthService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesAutorizacion;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class CierreActividadesServiceImpl implements CierreActividadesService {

    @Value("${sce.nacion.url}")

    private String urlNacion;

    private final RestTemplate clientExport;
    Logger logger = LoggerFactory.getLogger(CierreActividadesServiceImpl.class);
    private final UsuarioService usuarioService;
    private final CentroComputoService centroComputoService;
    private final CierreCentroComputoRepository cierreCentroComputoRepository;
    private final SasaAuthService sasaAuthService;
    private final MaeProcesoElectoralService procesoElectoralService;
    private final ITabLogService logService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CierreCentroComputoResponse cerrarCC(String nombreUsuario, String codigoCentroComputo, String motivo, String clave) throws Exception {

        try{
            // Validaciones de negocio
            Usuario usuario = validarUsuarioAdministrador(nombreUsuario);
            CentroComputo centroComputo = validarCentroComputo(codigoCentroComputo);
            validarCierreActivo(centroComputo.getId().intValue());
            validarSesionesActivas(nombreUsuario);
             validarUsuarioClave(nombreUsuario,clave);

            //realizar cierre de cc
            TabCierreCentroComputo cierre = realizarCierre(usuario, motivo, centroComputo.getId().intValue());

            logService.registrarLog(
                    nombreUsuario,
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    String.format("El usuario %s realizó el cierre del centro de cómputo %s", nombreUsuario, codigoCentroComputo),
                    codigoCentroComputo,
                    ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO,
                    ConstantesComunes.LOG_TRANSACCIONES_ACCION);

            logger.info("Cierre completado exitosamente. ID: {}, Centro: {}, Usuario: {}", cierre.getId(), codigoCentroComputo, nombreUsuario);
            return CierreCentroComputoResponse.builder()
                    .cierreId(cierre.getId())
                    .mensaje("Centro de cómputo cerrado correctamente")
                    .fechaCierre(cierre.getFechaCierre())
                    .usuarioCierre(cierre.getUsuarioCierre())
                    .motivoCierre(cierre.getMotivoCierre())
                    .correlativo(cierre.getCorrelativo())
                    .build();
        }catch (BusinessValidationException e) {
            throw e;
        }catch (Exception e) {
            throw new InternalServerErrorException("Error interno del sistema: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReaperturaCentroComputoResponse reabrirCC(String nombreUsuario, String codigoCentroComputo, boolean conAutorizacionNacion) throws Exception {
        try {
            // Validaciones
            CentroComputo centroComputo = validarCentroComputo(codigoCentroComputo);

            // Buscar cierre activo del usuario
            Optional<TabCierreCentroComputo> cierreActivoOpt =
                    cierreCentroComputoRepository.findCierreActivoByCentroComputo(centroComputo.getId().intValue());

            if (!cierreActivoOpt.isPresent()) {
                throw new GenericException("No existe un cierre activo para este centro de cómputo");
            }

            TabCierreCentroComputo cierreActivo = cierreActivoOpt.get();
            // Realizar reapertura
            realizarReapertura(cierreActivo, nombreUsuario);

            logService.registrarLog(
                    nombreUsuario,
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    String.format("El usuario %s realizó la reapertura del centro de cómputo %s", nombreUsuario, codigoCentroComputo),
                    codigoCentroComputo,
                    ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_SI,
                    ConstantesComunes.LOG_TRANSACCIONES_ACCION
            );




            logger.info("Reapertura completada exitosamente. ID: {}, Centro: {}, Usuario: {}",
                    cierreActivo.getId(), codigoCentroComputo, nombreUsuario);

            return ReaperturaCentroComputoResponse.builder()
                    .cierreId(cierreActivo.getId())
                    .mensaje("Centro de cómputo reabierto correctamente")
                    .fechaReapertura(cierreActivo.getFechaReapertura())
                    .usuarioReapertura(cierreActivo.getUsuarioReapertura())
                    .correlativo(cierreActivo.getCorrelativo())
                    .build();

        } catch (GenericException e) {
            throw e;
        } catch (Exception e) {
            throw new GenericException("Error interno del sistema: " + e.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ValidarUsuarioReaperturaResponse validarUsuarioReapertura(String nombreUsuario, String codigoCentroComputo) throws Exception {
        try {
            // Validaciones
            CentroComputo centroComputo = validarCentroComputo(codigoCentroComputo);

            // Buscar cierre activo del usuario
            Optional<TabCierreCentroComputo> cierreActivoOpt =
                    cierreCentroComputoRepository.findCierreActivoByCentroComputo(centroComputo.getId().intValue());

            if (!cierreActivoOpt.isPresent()) {
                throw new GenericException("No existe un cierre activo para este centro de cómputo");
            }

            TabCierreCentroComputo cierreActivo = cierreActivoOpt.get();
            boolean esElMismoUsuarioCierre = true;

            // verificar si es el mismo usuario que realizo el cierre de CC
            if (!cierreActivo.getUsuarioCierre().equals(nombreUsuario)) {
                esElMismoUsuarioCierre = false;
            }

            return ValidarUsuarioReaperturaResponse.builder()
                    .mensaje("Validacion de usuario reapertura correctamente")
                    .mismoUsuario(esElMismoUsuarioCierre)
                    .build();

        } catch (GenericException e) {
            throw e;
        } catch (Exception e) {
            throw new GenericException("Error interno del sistema: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public EstadoCentroComputoResponse consultarEstadoCC(String codigoCentroComputo) throws Exception {
        try {
            CentroComputo centroComputo = validarCentroComputo(codigoCentroComputo);

            Optional<TabCierreCentroComputo> cierreActivoOpt =
                    cierreCentroComputoRepository.findCierreActivoByCentroComputo(centroComputo.getId().intValue());

            if (cierreActivoOpt.isPresent()) {
                TabCierreCentroComputo cierreActivo = cierreActivoOpt.get();
                return EstadoCentroComputoResponse.builder()
                        .cerrado(true)
                        .cierreActivoId(cierreActivo.getId())
                        .fechaCierre(cierreActivo.getFechaCierre())
                        .usuarioCierre(cierreActivo.getUsuarioCierre())
                        .motivoCierre(cierreActivo.getMotivoCierre())
                        .correlativo(cierreActivo.getCorrelativo())
                        .build();
            } else {
                return EstadoCentroComputoResponse.builder()
                        .cerrado(false)
                        .build();
            }

        } catch (GenericException e) {
            throw e;
        } catch (Exception e) {
            throw new GenericException("Error interno del sistema: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public EstadoCentroComputoResponse consultarEstadoCCAutenticacion(String codigoCentroComputo) {
        try {
            Optional<CentroComputo> optionalCentroComputo = this.centroComputoService.findByCodigo(codigoCentroComputo);
            if (!optionalCentroComputo.isPresent()) {
                logger.debug("Centro de cómputo no encontrado: {}", codigoCentroComputo);
                return null;
            }

            CentroComputo centroComputo = optionalCentroComputo.get();
            Optional<TabCierreCentroComputo> cierreActivoOpt =
                    cierreCentroComputoRepository.findCierreActivoByCentroComputo(centroComputo.getId().intValue());

            if (cierreActivoOpt.isPresent()) {
                TabCierreCentroComputo cierreActivo = cierreActivoOpt.get();
                return EstadoCentroComputoResponse.builder()
                        .cerrado(true)
                        .cierreActivoId(cierreActivo.getId())
                        .fechaCierre(cierreActivo.getFechaCierre())
                        .usuarioCierre(cierreActivo.getUsuarioCierre())
                        .motivoCierre(cierreActivo.getMotivoCierre())
                        .correlativo(cierreActivo.getCorrelativo())
                        .build();
            } else {
                return EstadoCentroComputoResponse.builder()
                        .cerrado(false)
                        .build();
            }

        } catch (Exception e) {
            logger.warn("Error en consultarEstadoCCAutenticacion al consultar estado del centro de cómputo: {}, Error: {}",
                    codigoCentroComputo, e.getMessage());
            return null;
        }
    }

    @Override
    public AutorizacionNacionResponseDto getAutorizacionNacion(String usuario, String proceso, String cc) {

        AutorizacionNacionRequestDto request = new AutorizacionNacionRequestDto();
        request.setCc(cc);
        request.setUsuario(usuario);
        request.setTipoAutorizacion(ConstantesAutorizacion.TIPO_AUTORIZACION_REAPERTURA_CC);

        HttpEntity<AutorizacionNacionRequestDto> httpEntity = new HttpEntity<>(request, getHeaderAutorizacion(proceso));

        ResponseEntity<AutorizacionNacionResponseDto> response = this.clientExport.exchange(
                urlNacion + ConstantesComunes.URL_NACION_RECIBIR_AUTORIZACION,
                HttpMethod.PATCH,
                httpEntity,
                AutorizacionNacionResponseDto.class);
        return response.getBody();
    }

    @Override
    public Boolean solicitaAutorizacionReapertura(String usuario, String cc, String proceso, String tipoAutorizacion) {
        AutorizacionNacionRequestDto request = new AutorizacionNacionRequestDto();
        request.setCc(cc);
        request.setUsuario(usuario);
        request.setTipoAutorizacion(tipoAutorizacion);

        HttpEntity<AutorizacionNacionRequestDto> httpEntity = new HttpEntity<>(request, getHeaderAutorizacion(proceso));

        @SuppressWarnings("rawtypes")
        ResponseEntity<GenericResponse> response = this.clientExport.exchange(
                urlNacion + ConstantesComunes.URL_NACION_RECIBIR_SOLICITUD_AUTORIZACION,
                HttpMethod.PATCH,
                httpEntity,
                GenericResponse.class);

        GenericResponse<?> body = response.getBody();
        boolean isSuccessful = body != null && body.isSuccess();

        if(isSuccessful){
            String mensaje = String.format("Solicitud del usuario %s para realizar una reapertura del centro de cómputo %s",
                    usuario,
                    cc);
            logService.registrarLog(
                    usuario,
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    mensaje,
                    cc,
                    ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_SI,
                    ConstantesComunes.LOG_TRANSACCIONES_ACCION
            );
        }

        return isSuccessful;
    }

    @Override
    public Boolean consultaAutorizacion(String usuario, String cc, String proceso, String tipoAutorizacion) {
        return procesoElectoralService.verificarHabilitacionDiaEleccion(proceso, SceConstantes.PATTERN_DD_MM_YYYY_DASH);
    }

    private TabCierreCentroComputo realizarCierre(Usuario usuario, String motivo, Integer centroComputoId) {
        Date fechaActual = new Date();
        // Obtener siguiente correlativo
        Integer ultimoCorrelativo = cierreCentroComputoRepository
                .getUltimoCorrelativoByCentroComputo(centroComputoId);
        Integer nuevoCorrelativo = (ultimoCorrelativo != null ? ultimoCorrelativo : 0) + 1;

        // Crear registro de cierre
        TabCierreCentroComputo cierre = TabCierreCentroComputo.builder()
                .centroComputo(centroComputoId)
                .correlativo(nuevoCorrelativo)
                .fechaCierre(fechaActual)
                .usuarioCierre(usuario.getUsuario())
                .motivoCierre(motivo.trim())
                .reapertura(0)
                .fechaReapertura(null)
                .usuarioReapertura(usuario.getUsuario())
                .activo(1)
                .usuarioCreacion(usuario.getUsuario())
                .fechaCreacion(fechaActual)
                .usuarioModificacion(null)
                .fechaModificacion(null)
                .build();

        return cierreCentroComputoRepository.save(cierre);
    }

    private Usuario validarUsuarioAdministrador(String nombreUsuario) throws BusinessValidationException {
        try {
            Usuario tabUsuario = this.usuarioService.findByUsername(nombreUsuario);

            if (tabUsuario == null) {
                throw new BusinessValidationException("Usuario no encontrado: " + nombreUsuario);
            }

            if (!tabUsuario.getPerfil().equals(ConstantesComunes.PERFIL_USUARIO_ADMINISTRADOR_CC)) {
                throw new BusinessValidationException(
                        String.format("No cuenta con el perfil %s", ConstantesComunes.PERFIL_USUARIO_ADMINISTRADOR_CC)
                );
            }

            return tabUsuario;

        } catch (BusinessValidationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al buscar usuario: {}", nombreUsuario, e);
            throw new BusinessValidationException("Error al validar el usuario");
        }
    }

    private CentroComputo validarCentroComputo(String codigoCentroComputo) throws BusinessValidationException{
        try {
            Optional<CentroComputo> optionalCentroComputo = this.centroComputoService.findByCodigo(codigoCentroComputo);
            if (!optionalCentroComputo.isPresent()) {
                throw new BusinessValidationException(
                        String.format("No existe el centro de cómputo %s", codigoCentroComputo));
            }
            return optionalCentroComputo.get();

        } catch (BusinessValidationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al buscar centro de cómputo: {}", codigoCentroComputo, e);
            throw new BusinessValidationException("Error al validar el centro de cómputo");
        }
    }

    private void validarSesionesActivas(String nombreUsuario) throws BusinessValidationException{
        try {
            StringBuilder usuariosConectados = new StringBuilder();
            List<Usuario> tabUsuarioList = this.usuarioService.usuarioActivos();

            for (Usuario tabUsuario : tabUsuarioList) {
                if (tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_VERIFICADOR) ||
                        tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_SCE_SCANNER) ||
                        tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_CONTROL_DIGITALIZACION) ||
                        (tabUsuario.getUsuario().startsWith(ConstantesComunes.PERFIL_USUARIO_ADMINISTRADOR_CC) &&
                                !tabUsuario.getUsuario().equals(nombreUsuario))) {
                    usuariosConectados.append("\n").append(tabUsuario.getUsuario()).append(",");
                }
            }

            if (!usuariosConectados.isEmpty()) {
                usuariosConectados.deleteCharAt(usuariosConectados.length() - 1);
                usuariosConectados.append(".");
                throw new BusinessValidationException(
                        "Para proceder con el cierre del centro de cómputo, es necesario que los siguientes usuarios cierren sesión: " + usuariosConectados
                );
            }

        } catch (BusinessValidationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al validar sesiones activas para usuario: {}", nombreUsuario, e);
            throw new BusinessValidationException("Error al validar las sesiones activas");
        }
    }

    private void validarUsuarioClave(String nombreUsuario, String clave) throws BusinessValidationException{
        try {
            LoginInputDto input = new LoginInputDto();
            input.setUsuario(nombreUsuario);
            input.setClave(clave);

            LoginDatosOutputDto resultado = this.sasaAuthService.accederSistema(input);

            // Validación simple: cualquier error se considera de negocio para UX
            if (resultado == null || resultado.getMensaje() == null) {
                throw new BusinessValidationException("Usuario/contraseña incorrecto");
            }

            if (resultado.getResultado() != null &&
                    resultado.getResultado().equals(ConstantesComunes.SASA_RESPUESTA_ERROR)) {
                throw new BusinessValidationException(resultado.getMensaje());
            }

        } catch (BusinessValidationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al validar credenciales", e);
            throw new BusinessValidationException("No se pudo validar las credenciales en este momento");
        }
    }

    private void validarCierreActivo(Integer centroComputoId) throws BusinessValidationException {
        try {
            Optional<TabCierreCentroComputo> cierreActivoOpt =
                    cierreCentroComputoRepository.findCierreActivoByCentroComputo(centroComputoId);

            if (cierreActivoOpt.isPresent()) {
                TabCierreCentroComputo cierreActivo = cierreActivoOpt.get();

                // Formato de fecha para mostrar al usuario
                SimpleDateFormat sdf = new SimpleDateFormat(SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_SLASHED);
                String fechaCierreFormateada = sdf.format(cierreActivo.getFechaCierre());

                throw new BusinessValidationException(
                        String.format(
                                "Ya existe un cierre activo para este centro de cómputo. " +
                                        "Cierre realizado el %s por el usuario %s. " +
                                        "Debe realizar primero la reapertura antes de poder cerrar nuevamente.",
                                fechaCierreFormateada,
                                cierreActivo.getUsuarioCierre()
                        )
                );
            }

        } catch (BusinessValidationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error al validar cierre activo para centro de cómputo: {}", centroComputoId, e);
            throw new BusinessValidationException("Error al validar el estado del centro de cómputo");
        }
    }

    private HttpHeaders getHeaderAutorizacion(String proceso){
        HttpHeaders headers = new HttpHeaders();
        headers.set(SceConstantes.USERAGENT_HEADER, SceConstantes.USERAGENT_HEADER_VALUE);
        headers.set(SceConstantes.TENANT_HEADER, proceso);
        headers.set(SceConstantes.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }

    private void realizarReapertura(TabCierreCentroComputo cierre, String nombreUsuario) {
        Date fechaActual = new Date();

        cierre.setReapertura(1);
        cierre.setFechaReapertura(fechaActual);
        cierre.setUsuarioModificacion(nombreUsuario);
        cierre.setFechaModificacion(fechaActual);

        cierreCentroComputoRepository.save(cierre);
    }

    @Override
    public void save(TabCierreCentroComputo tabCierreCentroComputo) {
        this.cierreCentroComputoRepository.save(tabCierreCentroComputo);
    }

    @Override
    public void saveAll(List<TabCierreCentroComputo> k) {
        this.cierreCentroComputoRepository.saveAll(k);
    }

    @Override
    public void deleteAll() {
        this.cierreCentroComputoRepository.deleteAll();
    }

    @Override
    public List<TabCierreCentroComputo> findAll() {
        return this.cierreCentroComputoRepository.findAll();
    }
}
