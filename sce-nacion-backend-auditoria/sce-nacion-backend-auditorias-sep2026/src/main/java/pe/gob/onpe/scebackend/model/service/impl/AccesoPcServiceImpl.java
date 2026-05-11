package pe.gob.onpe.scebackend.model.service.impl;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import pe.gob.onpe.scebackend.exeption.BusinessValidationException;
import pe.gob.onpe.scebackend.model.dto.request.AccesoPcRequest;
import pe.gob.onpe.scebackend.model.dto.request.AutorizacionNacionRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.AccesoPcResponse;
import pe.gob.onpe.scebackend.model.dto.response.AutorizacionNacionResponseDto;
import pe.gob.onpe.scebackend.model.dto.response.GenericResponse;
import pe.gob.onpe.scebackend.model.entities.ConfiguracionProcesoElectoral;
import pe.gob.onpe.scebackend.model.orc.entities.AccesoPc;
import pe.gob.onpe.scebackend.model.orc.entities.AmbitoElectoral;
import pe.gob.onpe.scebackend.model.orc.entities.CentroComputo;
import pe.gob.onpe.scebackend.model.orc.entities.ProcesoElectoral;
import pe.gob.onpe.scebackend.model.orc.entities.Usuario;
import pe.gob.onpe.scebackend.model.orc.repository.AccesoPcRepository;
import pe.gob.onpe.scebackend.model.orc.repository.UsuarioRepository;
import pe.gob.onpe.scebackend.model.service.IAccesoPcService;
import pe.gob.onpe.scebackend.model.service.IAutorizacionService;
import pe.gob.onpe.scebackend.model.service.ICentroComputoService;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.model.service.comun.IAmbitoElectoralService;
import pe.gob.onpe.scebackend.utils.ConstantesAutorizacion;
import pe.gob.onpe.scebackend.utils.anotation.SetTenantContext;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccesoPcServiceImpl implements IAccesoPcService {

    private final AccesoPcRepository accesoPcRepository;

    private final UsuarioRepository usuarioRepository;

    private final IAutorizacionService autorizacionService;

    private final ProcesoElectoralService procesoElectoralService;

    private final UtilSceService utilSceService;

    private final ITabLogTransaccionalService logService;

    private final ICentroComputoService centroComputoService;

    private final IAmbitoElectoralService ambitoElectoralService;

    @Autowired
    public ConfiguracionProcesoElectoralService configuracionProcesoElectoralService;

    Logger logger = LoggerFactory.getLogger(AccesoPcServiceImpl.class);

    @Override
    @SetTenantContext
    public void registrarAcceso(String usuario, String ip) {
        try {
            List<ConfiguracionProcesoElectoral> procesos = this.configuracionProcesoElectoralService.listarVigentesYActivos();
            ConfiguracionProcesoElectoral primerProceso = procesos.get(0);
            String esquema = primerProceso.getNombreEsquemaPrincipal();

            Map<String, Object> resultado = accesoPcRepository.executeRegistrarAccesoPc(esquema, usuario, ip);

            Integer poResultado = (Integer) resultado.get("po_resultado");
            String poMensaje = (String) resultado.get("po_mensaje");

            if (poResultado != null && poResultado == 1) {
                logger.info("Acceso registrado exitosamente - Usuario: {}, IP: {}", usuario, ip);
            } else {
                logger.error("Error al registrar acceso - Resultado: {}, Mensaje: {}", poResultado, poMensaje);
            }

        } catch (Exception e) {
            logger.warn("Error al registrar acceso - usuario: {} - ip: {}", usuario, ip, e);
        }

    }

    @Override
    @SetTenantContext
    public boolean esPrimerLogin(String ip) {
        try {
            return !accesoPcRepository.existsByIpAccesoPcAndActivo(ip, ConstantesComunes.ACTIVO);
        } catch (Exception e) {
            logger.warn("Error al verificar primer login - IP: {}", ip);
            return false;
        }
    }

    @Override
    @SetTenantContext
    public Page<AccesoPcResponse> listarTodosPaginado(int page, int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<AccesoPc> accesos = accesoPcRepository.findAllByActivoOrderByFechaAccesoPcDesc(ConstantesComunes.ACTIVO, pageable);
            return accesos.map(this::convertirAResponse);
        } catch (Exception e) {
            logger.error("Error al listar accesos de IP: " + e.getMessage());
            return null;
        }
    }

    @Override
    @SetTenantContext
    public AutorizacionNacionResponseDto getAutorizacionNacion(String usuario, AccesoPcRequest accesoPcRequest) {

        try {

            CentroComputo ccNacion = centroComputoService.getPadreNacion();
            AutorizacionNacionRequestDto requestDto = new AutorizacionNacionRequestDto();
            requestDto.setCc(ccNacion.getCodigo());
            requestDto.setUsuario(usuario);
            requestDto.setTipoAutorizacion(ConstantesAutorizacion.TIPO_AUTORIZACION_LISTADO_PC);
            requestDto.setTipoDocumento(ConstantesComunes.ACCESO_PC_TIPO_DOCUMENTO_ID_PC);
            requestDto.setIdDocumento(accesoPcRequest.getIdAccesoPc());

            ResponseEntity<AutorizacionNacionResponseDto> response = this.autorizacionService.recibirAutorizacion(requestDto);

            AutorizacionNacionResponseDto responseDto = new AutorizacionNacionResponseDto();
            responseDto.setAutorizado(response.getBody().isAutorizado());
            responseDto.setMensaje(response.getBody().getMensaje());
            responseDto.setSolicitudGenerada(response.getBody().isSolicitudGenerada());
            return responseDto;

        } catch (Exception e) {
            logger.error("Error al obtener autorizacion", e);
            return null;
        }
    }

    @Override
    @SetTenantContext
    public Boolean solicitarAutorizacion(String usuario, AccesoPcRequest accesoPcRequest) {

        try {
            CentroComputo ccNacion = centroComputoService.getPadreNacion();
            AmbitoElectoral ambito = ambitoElectoralService.getPadreNacion();

            AutorizacionNacionRequestDto requestDto = new AutorizacionNacionRequestDto();
            requestDto.setCc(ccNacion.getCodigo());
            requestDto.setUsuario(usuario);
            requestDto.setTipoAutorizacion(ConstantesAutorizacion.TIPO_AUTORIZACION_LISTADO_PC);
            requestDto.setTipoDocumento(ConstantesComunes.ACCESO_PC_TIPO_DOCUMENTO_ID_PC);
            requestDto.setIdDocumento(accesoPcRequest.getIdAccesoPc());

            ResponseEntity<GenericResponse> response = this.autorizacionService.crearSolicitudAutorizacion(requestDto);

            GenericResponse body = response.getBody();
            boolean isSucces = body != null && body.isSuccess();
            if (isSucces) {
                String mensaje = this.autorizacionService.generarDescripcionGenerica(requestDto);
                this.logService.registrarLog(usuario, Thread.currentThread().getStackTrace()[1].getMethodName(),
                        this.getClass().getSimpleName(),
                        mensaje,
                        ambito.getCodigo(), ccNacion.getCodigo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_SI,
                        ConstantesComunes.LOG_TRANSACCIONES_ACCION);
            }

            return isSucces;
        } catch (Exception e) {
            logger.error("Error al solicitar autorización", e);
            return false;
        }


    }

    @Override
    @SetTenantContext
    public Boolean actualizarEstado(AccesoPcRequest accesoPcRequest, String usuarioModifica) {

        try {
            CentroComputo ccNacion = centroComputoService.getPadreNacion();
            AmbitoElectoral ambito = ambitoElectoralService.getPadreNacion();
            Optional<AccesoPc> accesoPcOpt = accesoPcRepository.findById(accesoPcRequest.getIdAccesoPc());
            if (!accesoPcOpt.isPresent()) {
                log.error("No se encontró el registro de acceso con IP: {}", accesoPcRequest.getIpAccesoPc());
                throw new BusinessValidationException(
                        "No se encontró el registro de acceso con el IP " + accesoPcRequest.getIpAccesoPc() + ".");
            }
            AccesoPc acceso = accesoPcOpt.get();
            if (acceso.getActivo().equals(ConstantesComunes.INACTIVO)) {
                log.warn("El registro de acceso con IP {} ya se encuentra inactivo", accesoPcRequest.getIpAccesoPc());
                throw new BusinessValidationException(
                        "El registro de acceso con el IP " + accesoPcRequest.getIpAccesoPc() + ", ya se encuentra inactivo.");
            }
            acceso.setActivo(ConstantesComunes.INACTIVO);
            acceso.setUsuarioModificacion(usuarioModifica);
            acceso.setFechaModificacion(new Date());
            accesoPcRepository.save(acceso);
            log.info(" Registro de acceso desactivado - ID: {} | Usuario: {} | IP: {} | Modificado por: {}",
                    accesoPcRequest.getIdAccesoPc(), acceso.getUsuarioAccesoPc(), acceso.getIpAccesoPc(), usuarioModifica);

            String mensaje = String.format("El usuario %s eliminó la PC con IP %s de %s", usuarioModifica, acceso.getIpAccesoPc(),
                    ccNacion.getNombre());
            this.logService.registrarLog(usuarioModifica, Thread.currentThread().getStackTrace()[1].getMethodName(),
                    this.getClass().getSimpleName(), mensaje, ambito.getCodigo(),
                    ccNacion.getCodigo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_SI,
                    ConstantesComunes.LOG_TRANSACCIONES_ACCION);

            return true;

        } catch (Exception e) {
            logger.error("Error al actualizar estado de acceso", e);
            return false;
        }

    }

    @Override
    @SetTenantContext
    public byte[] getReportePcs(String usuario) {
        try {
            List<AccesoPc> listaAccesos = accesoPcRepository.findAllByActivoOrderByFechaAccesoPcDesc(ConstantesComunes.ACTIVO);
            List<AccesoPcResponse> lista = convertirAResponse(listaAccesos);

            ProcesoElectoral proceso = procesoElectoralService.findByActivo();
            final String nombreReporte = ConstantesComunes.LISTADO_PCS_JRXML;
            Map<String, Object> parametros = new HashMap<>();

            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON + "onpe.jpg");

            parametros.put("url_imagen", imagen);
            parametros.put(ConstantesComunes.REPORT_PARAM_SIN_VALOR_OFICIAL, proceso != null ?
                    utilSceService.getSinValorOficial(proceso.getId().intValue()) : "");
            parametros.put(ConstantesComunes.REPORT_PARAM_VERSION, utilSceService.getVersionSistema());
            parametros.put(ConstantesComunes.REPORT_PARAM_USUARIO, usuario);
            parametros.put("tituloPrincipal", proceso != null ? proceso.getNombre() : "");
            parametros.put("tituloSecundario", "LISTADO DE PC");
            parametros.put("descCompu", "NACIÓN");

            this.logService.registrarLog(usuario, ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE,
                    this.getClass().getSimpleName(), "Se consultó el Reporte de listado de PC", "",
                    "Nación", ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);

            return Funciones.generarReporte(this.getClass(), lista, nombreReporte, parametros);

        } catch (Exception e) {
            logger.error("Excepción en getReportePcs", e);
            return new byte[0];
        }

    }

    private List<AccesoPcResponse> convertirAResponse(List<AccesoPc> accesos) {
        return accesos.stream()
                .map(this::convertirAResponse)
                .toList();
    }

    private AccesoPcResponse convertirAResponse(AccesoPc acceso) {

        AccesoPcResponse.AccesoPcResponseBuilder builder = AccesoPcResponse.builder()
                .fechaAccesoPc(formatDate(acceso.getFechaAccesoPc()))
                .usuarioAccesoPc(acceso.getUsuarioAccesoPc())
                .ipAccesoPc(acceso.getIpAccesoPc())
                .id(acceso.getId());

        Usuario usuario = usuarioRepository.findByUsuario(acceso.getUsuarioAccesoPc());
        if (usuario != null) {
            builder.nombre(usuario.getNombres())
                    .apellidoPaterno(usuario.getApellidoPaterno())
                    .apellidoMaterno(usuario.getApellidoMaterno());
        }

        return builder.build();
    }

    private String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(date);
    }
}
