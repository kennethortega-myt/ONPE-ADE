package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import pe.gob.onpe.sceorcbackend.exception.BusinessValidationException;
import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.AutorizacionNacionResponseDto;
import pe.gob.onpe.sceorcbackend.model.dto.request.AccesoPcRequest;
import pe.gob.onpe.sceorcbackend.model.dto.response.AccesoPcResponse;
import pe.gob.onpe.sceorcbackend.model.dto.response.GenericResponse;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.AccesoPc;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ProcesoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Usuario;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.AccesoPcRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ProcesoElectoralRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.UsuarioRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AccesoPcService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesAutorizacion;
import static pe.gob.onpe.sceorcbackend.utils.ConstantesComunes.*;

import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccesoPcServiceImpl implements AccesoPcService {

    private static final Logger logger = LoggerFactory.getLogger(AccesoPcServiceImpl.class);
    private final AccesoPcRepository accesoPcRepository;
    @Value("${sce.nacion.url}")
    private String urlNacion;
    private final RestTemplate clientExport;
    
    private final UtilSceService utilSceService;
	
	private final ITabLogService logService;
	
	private final ProcesoElectoralRepository procesoElectoralRepository;

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public AccesoPc registrarAcceso(String usuario, String ipAddress) {
        try {
            AccesoPc acceso = AccesoPc.builder()
                    .usuarioAccesoPc(usuario)
                    .ipAccesoPc(ipAddress)
                    .fechaAccesoPc(new Date())
                    .activo(1)
                    .usuarioCreacion(usuario)
                    .fechaCreacion(new Date())
                    .build();
            AccesoPc saved = accesoPcRepository.save(acceso);
            logger.info("Registro de acceso guardado para usuario: {} desde IP: {}", usuario, ipAddress);
            return saved;
        } catch (Exception e) {
            logger.error("Error al registrar acceso para usuario: {} - {}", usuario, e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean esPrimerLogin(String ipAddress) {
        try {
            boolean existe = accesoPcRepository.existsByIpAccesoPcAndActivo(ipAddress, ACTIVO);
            logger.info("Verificación primer login para la ip: {} - Es primer login: {}", ipAddress, !existe);
            return !existe;
        } catch (Exception e) {
            logger.error("Error al verificar primer login para la ip: {} - {}", ipAddress, e.getMessage());
            return false;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccesoPcResponse> listarTodos() {
        List<AccesoPc> accesos = accesoPcRepository.findAllByActivoOrderByFechaAccesoPcDesc(ACTIVO);
        if (CollectionUtils.isEmpty(accesos)) {
            throw new BusinessValidationException(ConstantesComunes.NO_SE_ENCONTRARON_REGISTROS);
        }
        return convertirAResponse(accesos);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccesoPcResponse> listarTodosPaginado(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AccesoPc> accesos = accesoPcRepository.findAllByActivoOrderByFechaAccesoPcDesc(ACTIVO, pageable);
        return accesos.map(this::convertirAResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AccesoPcResponse> listarPorUsuario(String usuario) {
        List<AccesoPc> accesos = accesoPcRepository.findByUsuarioAccesoPcAndActivoOrderByFechaAccesoPcDesc(usuario, ACTIVO);
        if (CollectionUtils.isEmpty(accesos)) {
            throw new BusinessValidationException(ConstantesComunes.NO_SE_ENCONTRARON_REGISTROS);
        }
        return convertirAResponse(accesos);
    }

    @Override
    public AutorizacionNacionResponseDto getAutorizacionNacion(String usuario, String cc, AccesoPcRequest accesoPcRequest, String abrevProceso) {
        AutorizacionNacionRequestDto request = new AutorizacionNacionRequestDto();
        request.setCc(cc);
        request.setUsuario(usuario);
        request.setTipoAutorizacion(ConstantesAutorizacion.TIPO_AUTORIZACION_LISTADO_PC);
        request.setTipoDocumento(ACCESO_PC_TIPO_DOCUMENTO_ID_PC);
        request.setIdDocumento(accesoPcRequest.getIdAccesoPc());

        HttpEntity<AutorizacionNacionRequestDto> httpEntity = new HttpEntity<>(request, getHeaderAutorizacion(abrevProceso));

        ResponseEntity<AutorizacionNacionResponseDto> response = this.clientExport.exchange(
                urlNacion + URL_NACION_RECIBIR_AUTORIZACION,
                HttpMethod.PATCH,
                httpEntity,
                AutorizacionNacionResponseDto.class);
        return response.getBody();
    }

    @Override
    public Boolean solicitarAutorizacion(String usuario, String cc, String nombreCentroComputo, String abrevProceso, AccesoPcRequest accesoPcRequest) {
        AutorizacionNacionRequestDto request = new AutorizacionNacionRequestDto();
        request.setCc(cc);
        request.setUsuario(usuario);
        request.setTipoAutorizacion(ConstantesAutorizacion.TIPO_AUTORIZACION_LISTADO_PC);
        request.setTipoDocumento(ACCESO_PC_TIPO_DOCUMENTO_ID_PC);
        request.setIdDocumento(accesoPcRequest.getIdAccesoPc());

        HttpEntity<AutorizacionNacionRequestDto> httpEntity = new HttpEntity<>(request, getHeaderAutorizacion(abrevProceso));

        @SuppressWarnings("rawtypes")
        ResponseEntity<GenericResponse> response = this.clientExport.exchange(
                urlNacion + URL_NACION_RECIBIR_SOLICITUD_AUTORIZACION,
                HttpMethod.PATCH,
                httpEntity,
                GenericResponse.class);

        GenericResponse<?> body = response.getBody();
        boolean isSuccess = body != null && body.isSuccess();
        if (isSuccess){
            String mensaje = String.format("Solicitud del usuario %s para eliminar una PC del listado del centro de cómputo %s.",
                    usuario,
                    nombreCentroComputo);
            logService.registrarLog(
                    usuario,
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    mensaje,
                    cc,
                    LOG_TRANSACCIONES_AUTORIZACION_SI,
                    ConstantesComunes.LOG_TRANSACCIONES_ACCION
            );
        }
        return isSuccess;
    }

    @Override
    @Transactional
    public Boolean actualizarEstado(AccesoPcRequest accesoPcRequest, String usuarioModifica, String codigoCentroComputo) {
        Optional<AccesoPc> accesoPcOpt = accesoPcRepository.findById(accesoPcRequest.getIdAccesoPc());
        if (!accesoPcOpt.isPresent()) {
            logger.error("No se encontró el registro de acceso con IP: {}", accesoPcRequest.getIpAccesoPc());
            throw new BusinessValidationException("No se encontró el registro de acceso con el IP "+ accesoPcRequest.getIpAccesoPc() +".");
        }
        AccesoPc acceso = accesoPcOpt.get();
        if (acceso.getActivo().equals(INACTIVO)) {
            logger.warn("El registro de acceso con IP {} ya se encuentra inactivo", accesoPcRequest.getIpAccesoPc());
            throw new BusinessValidationException("El registro de acceso con el IP "+ accesoPcRequest.getIpAccesoPc() + ", ya se encuentra inactivo.");
        }
        acceso.setActivo(INACTIVO);
        acceso.setUsuarioModificacion(usuarioModifica);
        acceso.setFechaModificacion(new Date());
        accesoPcRepository.save(acceso);
        String mensaje = String.format("El usuario %s eliminó la PC con IP %s del centro de cómputo %s.",
                usuarioModifica,
                accesoPcRequest.getIpAccesoPc(),
                codigoCentroComputo);
        logService.registrarLog(
                usuarioModifica,
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                mensaje,
                codigoCentroComputo,
                LOG_TRANSACCIONES_AUTORIZACION_SI,
                ConstantesComunes.LOG_TRANSACCIONES_ACCION
        );
        logger.info(" Registro de acceso desactivado - ID: {} | Usuario: {} | IP: {} | Modificado por: {}",
                accesoPcRequest.getIdAccesoPc(), acceso.getUsuarioAccesoPc(), acceso.getIpAccesoPc(), usuarioModifica);
        return true;
    }

    @Override
	public byte[] getReportePcs(String acronimoProceso, String usuario, String codigoCentroComputo, String nombreCentroComputo) {
		try {
			List<AccesoPc> listaAccesos = accesoPcRepository.findAllByActivoOrderByFechaAccesoPcDesc(ACTIVO);
			List<AccesoPcResponse> lista = convertirAResponse(listaAccesos);
			
			ProcesoElectoral proceso = procesoElectoralRepository.findByAcronimo(acronimoProceso);
			final String nombreReporte = LISTADO_PCS_JRXML;
			Map<String, Object> parametros = new HashMap<>();

			InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(PATH_IMAGE_COMMON + "onpe.jpg");

			parametros.put("url_imagen", imagen);			
			parametros.put(REPORT_PARAM_SIN_VALOR_OFICIAL, proceso != null ? utilSceService.getSinValorOficial(proceso.getId().intValue()) : "");
			parametros.put(REPORT_PARAM_VERSION, utilSceService.getVersionSistema());
			parametros.put(REPORT_PARAM_USUARIO, usuario);
			parametros.put("tituloPrincipal", proceso != null ? proceso.getNombre() : "");
			parametros.put("tituloSecundario", "LISTADO DE PC");
			parametros.put("descCompu", codigoCentroComputo + " - " + nombreCentroComputo);

            this.logService.registrarLog(
                    usuario,
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    this.getClass().getSimpleName(), "Se consultó el Reporte de listado de Pcs.",
                    codigoCentroComputo, LOG_TRANSACCIONES_AUTORIZACION_NO, LOG_TRANSACCIONES_ACCION);

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
        if(usuario != null){
            builder.nombre(usuario.getNombres())
                    .apellidoPaterno(usuario.getApellidoPaterno())
                    .apellidoMaterno(usuario.getApellidoMaterno());
        }

        return builder.build();
    }

    private String formatDate(Date date){
        if(date == null){
            return null;
        }
        SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(date);
    }

    private HttpHeaders getHeaderAutorizacion(String abrevProceso){
        HttpHeaders headers = new HttpHeaders();
        headers.set(SceConstantes.USERAGENT_HEADER, SceConstantes.USERAGENT_HEADER_VALUE);
        headers.set(SceConstantes.TENANT_HEADER, abrevProceso);
        headers.set(SceConstantes.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        return headers;
    }
}
