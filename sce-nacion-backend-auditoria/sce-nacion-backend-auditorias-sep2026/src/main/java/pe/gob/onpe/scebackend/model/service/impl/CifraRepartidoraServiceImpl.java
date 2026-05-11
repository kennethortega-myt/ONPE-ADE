package pe.gob.onpe.scebackend.model.service.impl;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.scebackend.exeption.BusinessValidationException;
import pe.gob.onpe.scebackend.model.dto.*;
import pe.gob.onpe.scebackend.model.dto.request.*;
import pe.gob.onpe.scebackend.model.dto.response.*;
import pe.gob.onpe.scebackend.model.orc.entities.ProcesoElectoral;
import pe.gob.onpe.scebackend.model.orc.repository.DistritoElectoralRepository;
import pe.gob.onpe.scebackend.model.orc.repository.ProcesoElectoralRepository;
import pe.gob.onpe.scebackend.model.service.ICifraRepartidoraService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

@Service
@Slf4j
@RequiredArgsConstructor
public class CifraRepartidoraServiceImpl implements ICifraRepartidoraService {

    private static final String PO_RESULTADO = "po_resultado";
    private static final String PO_MENSAJE = "po_mensaje";
    private static final String SIN_MENSAJE = "Sin mensaje";
    private static final String C_ESTADO_CIFRA = "c_estado_cifra";
    private static final String N_FACTOR_CIFRA = "n_factor_cifra";
    private static final String D_FECHA_PROCESO = "d_fecha_proceso";
    private static final String N_ESCANOS_OBTENIDOS = "n_escanos_obtenidos";
    private static final String N_VOTOS_VALIDOS = "n_votos_validos";
    private static final String C_DESCRIPCION_DISTRITO_ELECTORAL = "c_descripcion_distrito_electoral";
    private static final String C_DESCRIPCION_AGRUPACION_POLITICA = "c_descripcion_agrupacion_politica";
    private static final String C_DISTRITO_ELECTORAL = "c_distrito_electoral";
    private static final Integer RESULTADO_PROCESADO = 1;
    private static final Integer RESULTADO_EMPATE = 2;
    
    private final DistritoElectoralRepository distritoElectoralRepository;
    private final UtilSceService utilSceService;
    private final ProcesoElectoralRepository procesoElectoralRepository;
    
    @Override
    public List<DistritoElectoralResponseDto> listDistritoElectoral(DistritoElectoralRequestDto filtro) {
        log.info("Iniciando listDistritoElectoral con filtro: esquema={}, codEleccion={}",
                filtro.getEsquema(), filtro.getCodEleccion());
        
        if (filtro.getCodEleccion() == null) {
            throw new IllegalArgumentException("El codigo de eleccion no puede ser nulo.");
        }        
        
        List<Map<String, Object>> resultado = distritoElectoralRepository.obtenerDistritoElectoralByEleccion(filtro.getEsquema(), filtro.getCodEleccion());

        return resultado.parallelStream()
                .map(this::mapearResponseDistritoElectoral)
                .toList();
    }

    private DistritoElectoralResponseDto mapearResponseDistritoElectoral(Map<String, Object> reporte) {
        DistritoElectoralResponseDto response = new DistritoElectoralResponseDto();
        response.setCodigo((String) reporte.get("c_codigo"));
        response.setNombre((String) reporte.get("c_nombre"));
        return response;
    }   

    @Override
    public GenericResponse consultaCifraRepartidora(String esquema, ConsultaCifraRepartidoraRequestDto filtro) {        

        if (filtro.getCodEleccion() == null || filtro.getCodEleccion().isEmpty()) {
            throw new IllegalArgumentException("El tipo de elección no puede ser nulo o vacío.");
        }
        if (filtro.getTipoCifra() == null || filtro.getTipoCifra().isEmpty()) {
            throw new IllegalArgumentException("El tipo de cifra no puede ser nulo o vacío.");
        }

        GenericResponse response = new GenericResponse();
        ConsultaCifraRepartidoraResponseDto cifraRepartidoraResult = new ConsultaCifraRepartidoraResponseDto(); 

        try {
            
            List<Map<String, Object>> resultado = distritoElectoralRepository.obtenerReporteResultados(
                esquema, filtro.getCodEleccion(), filtro.getCodDistritoElectoral(), filtro.getEstadoCifra(), filtro.getTipoCifra());

            if (resultado.isEmpty()) {
                
                response.setMessage("No se encontró información para esta consulta.");
                response.setSuccess(Boolean.FALSE);
                return response;             
            }

            List<ReporteResultadosDto> listCifraRepartidora = resultado.parallelStream()
                .map(this::llenarDatosReporteResultados)
                .toList();

            cifraRepartidoraResult.setListReporteResultados(listCifraRepartidora);
            
            Map<String, Object> resultadoConsultaResumen = distritoElectoralRepository.obtenerConsultaResumen(
                esquema, filtro.getCodEleccion(), filtro.getCodDistritoElectoral(), filtro.getEstadoCifra(), filtro.getTipoCifra()
            );

            ConsultaResumenDto consultaResumen = this.llenarDatosConsultaResumen(resultadoConsultaResumen);
            cifraRepartidoraResult.setConsultaResumen(consultaResumen);
            
            BigDecimal porcentajeAvance = distritoElectoralRepository.obtenerPorcentajeAvance(
                esquema, filtro.getCodEleccion(), filtro.getCodDistritoElectoral() );
            cifraRepartidoraResult.setPorcentajeAvance(porcentajeAvance != null ? porcentajeAvance.toString() : "0");

            response.setMessage("Se obtuvo la información correctamente.");
            response.setSuccess(Boolean.TRUE);
            response.setData(cifraRepartidoraResult);
            return response;
            
        }  catch (IllegalArgumentException e) {
            log.warn("Error de validación en consultaCifraRepartidora: {}", e.getMessage());
            response.setSuccess(Boolean.FALSE);
            response.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado en consultaCifraRepartidora: {}", e.getMessage(), e);
            response.setSuccess(Boolean.FALSE);
            response.setMessage("Error al procesar la consulta de cifra repartidora.");
        } 
        
        return response;        
        
    }

    private ReporteResultadosDto llenarDatosReporteResultados(Map<String, Object> data) {
        ReporteResultadosDto response = new ReporteResultadosDto();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        response.setDistritoElectoral((String) data.get(C_DISTRITO_ELECTORAL));
        response.setDescripcionDistritoElectoral((String) data.get(C_DESCRIPCION_DISTRITO_ELECTORAL));
        response.setDescripcionAgrupacionPolitica((String) data.get(C_DESCRIPCION_AGRUPACION_POLITICA));
        
        BigDecimal votosValidos = (BigDecimal) data.get(N_VOTOS_VALIDOS);
        response.setVotosValidos(votosValidos != null ? votosValidos.toString() : null);

        response.setDivision((String) data.get("c_division"));
        
        BigDecimal cocienteObtenido = (BigDecimal) data.get("c_cociente_obtenido");
        response.setCocienteObtenido(cocienteObtenido != null ? cocienteObtenido.toString() : null);

        Timestamp fechaProceso = (Timestamp) data.get(D_FECHA_PROCESO);
        String fechaFormateada = null;
        fechaFormateada = dateFormat.format(fechaProceso);
        response.setFechaProceso(fechaFormateada);

        response.setObservacionCifra((String) data.get("c_observacion_cifra"));        
        
        BigDecimal escanosObtenidos = (BigDecimal) data.get(N_ESCANOS_OBTENIDOS);
        response.setEscanioObtenidos(escanosObtenidos != null ? escanosObtenidos.toString() : null);
        
        BigDecimal cantidadCurules = (BigDecimal) data.get("n_cantidad_curules");
        response.setCantidadCurules(cantidadCurules != null ? cantidadCurules.toString() : null);
        
        BigDecimal factorCifra = (BigDecimal) data.get(N_FACTOR_CIFRA);
        response.setFactorCifra(factorCifra != null ? factorCifra.toString() : null);
        response.setEstadoCifra((String) data.get(C_ESTADO_CIFRA));
        
        Integer ubicacionAgrupacion = (Integer) data.get("n_ubicacion_agrupacion_politica");
        response.setUbicacionAgrupacionPolitica(ubicacionAgrupacion != null ? ubicacionAgrupacion : null);
        
        response.setEstadoDistritoElectoral((String) data.get("c_estado_distrito_electoral"));
        response.setAgrupacionPolitica((String) data.get("c_agrupacion_politica"));

        return response;
    }

    private VariableSistemaDto llenarVariableSistema(Map<String, Object> data) {
        VariableSistemaDto variableSistemaDto = new VariableSistemaDto();
        variableSistemaDto.setCodigoVarible((String) data.get("c_codigo_variable"));
        variableSistemaDto.setValorVariable((String) data.get("c_valor_variable"));
        return variableSistemaDto;
    }
    
    private ConsultaResumenDto llenarDatosConsultaResumen(Map<String, Object> data) {
        ConsultaResumenDto response = new ConsultaResumenDto();

        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String fechaFormateada = null;
        if (data.get(D_FECHA_PROCESO) != null) {
            fechaFormateada = dateFormat.format(data.get(D_FECHA_PROCESO));
        }
        BigDecimal factorCifra = (BigDecimal) data.get(N_FACTOR_CIFRA);
        response.setCifraRepartidora(factorCifra != null ? factorCifra.toString() : null);
        
        BigDecimal totalEscanios = (BigDecimal) data.get(N_ESCANOS_OBTENIDOS);
        response.setTotalEscanios(totalEscanios != null ? totalEscanios.toString() : null);

        response.setHoraProceso(fechaFormateada);
        response.setEstado((String) data.get(C_ESTADO_CIFRA));
        
        BigDecimal totalVotosValidos = (BigDecimal) data.get("n_total_votos_validos");
        response.setTotalVotosValidos(totalVotosValidos != null ? totalVotosValidos.longValue() : null);
        
        BigDecimal vallaPorcentajeVotos = (BigDecimal) data.get("n_valla_porcentaje_votos");
        response.setVallaPorcentajeVotos(vallaPorcentajeVotos != null ? vallaPorcentajeVotos.toString() : null);        
        
        BigDecimal vallaNumeroMiembros = (BigDecimal) data.get("n_valla_numero_miembros");
        response.setVallaNumeroMiembros(vallaNumeroMiembros != null ? vallaNumeroMiembros.toString() : null);
        
        return response;
    }

    private VotosEmpateResultadoDto llenarDatosVotosEmpate(Map<String, Object> data) {
        VotosEmpateResultadoDto response = new VotosEmpateResultadoDto();

        response.setTipoEleccion((String) data.get("c_tipo_eleccion"));
        response.setDescripcionTipoEleccion( (String) data.get("c_descripcion_tipo_eleccion"));
        response.setDistritoElectoral((String) data.get(C_DISTRITO_ELECTORAL));
        response.setDescripcionDistritoElectoral((String) data.get(C_DESCRIPCION_DISTRITO_ELECTORAL));
        response.setAgrupacionPolitica((String) data.get("c_agrupacion_politica"));
        response.setDescripcionAgrupacionPolitica((String) data.get(C_DESCRIPCION_AGRUPACION_POLITICA));

        Integer votosValidos = (Integer) data.get(N_VOTOS_VALIDOS);
        response.setVotosValidos(votosValidos != null ? votosValidos.toString() : null);

        response.setEstadoResolucion((String) data.get("c_estado_resolucion"));
        response.setNumeroResolucion((String) data.get("c_numero_resolucion"));

        BigDecimal escanosObtenidos = (BigDecimal)  data.get(N_ESCANOS_OBTENIDOS);
        response.setEscanosObtenidos(escanosObtenidos != null ? escanosObtenidos.toString() : null);

        BigDecimal escanosResolucion = (BigDecimal) data.get("n_escanos_resolucion");
        response.setEscanosResolucion(escanosResolucion != null ? escanosResolucion.toString() : null);

        response.setDescripcionEstado((String) data.get("c_descripcion_estado"));

        return response;
    }

    @Override
    @Transactional(value = "locationTransactionManager")
    public GenericResponse consolidarVotosAgrupacion(String esquema, ConsolidarVotosAgrupacionRequestDto filtro) {
        log.info("Iniciando consolidación de votos por agrupación - tipoEleccion: {}, distritoElectoral: {}, usuario: {}", 
                filtro.getCodEleccion(), filtro.getCodDistritoElectoral(), filtro.getCodigoUsuario());
        
        GenericResponse genericResponse = new GenericResponse();
        try {
            // Validación del DTO antes de procesarlo
            validarConsolidarVotosAgrupacionRequest(filtro);  
            Integer resultado = 0;          
		    String mensaje = "";
            String codDistritoElectoralNacion = null;
            List<VariableSistemaDto> listVariables = this.obtenerVariablesSistema(esquema);
            if (!listVariables.isEmpty()) {
                codDistritoElectoralNacion = listVariables.stream()
                        .filter(variableSistemaDto -> variableSistemaDto.getCodigoVarible().equals(ConstantesComunes.CODIGO_DISTRITO_ELECTORAL_NACION))
                        .map(VariableSistemaDto::getValorVariable)
                        .findFirst()
                        .orElse(null);
            }

            if (codDistritoElectoralNacion == null){
                genericResponse.setSuccess(Boolean.FALSE);
                genericResponse.setMessage(String.format("No se encontró el distrito electoral %s.",ConstantesComunes.NOMBRE_NACION_DISTRITO_ELECTORAL));
                return genericResponse;
            }
            
            if(filtro.getCodEleccion().equals(ConstantesComunes.COD_ELEC_DIPUTADO) &&
            !filtro.getCodDistritoElectoral().equals(codDistritoElectoralNacion)) {
                genericResponse.setSuccess(Boolean.FALSE);
                genericResponse.setMessage("Este tipo de elección solo permite consolidar el distrito electoral Nación.");
                return genericResponse;
            }
            
            log.debug("Ejecutando procedimiento almacenado para consolidación de votos");
            Map<String, Object> resultadoConsolidacion = this.distritoElectoralRepository.cifraConsolidaVotosAgrupacion(
                esquema,
                filtro.getCodEleccion(),
                filtro.getCodDistritoElectoral(),
                filtro.getCodigoUsuario(),
                filtro.getNombrePc(),
                resultado,
                mensaje
            );
            
            if (resultadoConsolidacion != null) {

                resultado = resultadoConsolidacion.get(PO_RESULTADO) != null ? 
                    ((Number) resultadoConsolidacion.get(PO_RESULTADO)).intValue() : -1;
                mensaje = resultadoConsolidacion.get(PO_MENSAJE) != null ? 
                    resultadoConsolidacion.get(PO_MENSAJE).toString() : SIN_MENSAJE;
                
                if (resultado == 1) {                    
                    genericResponse.setSuccess(Boolean.TRUE);
                    genericResponse.setMessage(mensaje);
                } else {
                    log.warn("Consolidación de votos falló - resultado: {}, mensaje: {}", 
                            resultado, mensaje);
                    genericResponse.setSuccess(Boolean.FALSE);
                    genericResponse.setMessage(mensaje);                
                }
            } else {                
                genericResponse.setSuccess(Boolean.FALSE);
                genericResponse.setMessage("Error: Respuesta inválida del procedimiento almacenado");
            }
        } catch (IllegalArgumentException e) {
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage("Error al consolidar votos por agrupación: " + e.getMessage());
        }        
        
        return genericResponse;
    }

    @Override
    @Transactional(value = "locationTransactionManager")
    public GenericResponse reparteCurules(String esquema, ReparteCurulesRequestDto filtro) {
        log.info("Iniciando reparte curules - tipoEleccion: {}, distritoElectoral: {}, usuario: {}, nombrePc: {}", 
                filtro.getCodEleccion(), filtro.getCodDistritoElectoral(), filtro.getCodigoUsuario(), filtro.getNombrePc());
        
        GenericResponse genericResponse = new GenericResponse();

        try {

            Integer resultado = 0;          
		    String mensaje = "";
            
            log.debug("Ejecutando procedimiento almacenado para repartir curules");
            Map<String, Object> maps = this.distritoElectoralRepository.cifraReparteCurules(
                esquema,
                filtro.getCodEleccion(),
                filtro.getCodDistritoElectoral(),
                filtro.getCodigoUsuario(),
                filtro.getNombrePc(),
                filtro.getForzarCalculo(),
                resultado,
                mensaje
            );
            
            if (maps != null) {
                resultado = maps.get(PO_RESULTADO) != null ? 
                    ((Number) maps.get(PO_RESULTADO)).intValue() : -1;
                mensaje = maps.get(PO_MENSAJE) != null ? 
                    maps.get(PO_MENSAJE).toString() : SIN_MENSAJE;

                if (resultado.equals(RESULTADO_PROCESADO)) {
                    genericResponse.setSuccess(Boolean.TRUE);
                    genericResponse.setMessage(mensaje);
                    genericResponse.setData(1);
                } else if (resultado.equals(RESULTADO_EMPATE)){
                    genericResponse.setSuccess(Boolean.FALSE);
                    genericResponse.setData(2);
                    genericResponse.setMessage(mensaje);
                } else {
                    genericResponse.setSuccess(Boolean.FALSE);
                    genericResponse.setMessage(mensaje);                
                }
            } else {
                
                genericResponse.setSuccess(Boolean.FALSE);
                genericResponse.setMessage("Error: Respuesta inválida del procedimiento almacenado");
            }
        } catch (IllegalArgumentException e) {            
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage("Error de validación: " + e.getMessage());
        } catch (Exception e) {            
            genericResponse.setSuccess(Boolean.FALSE);
            genericResponse.setMessage("Error al repartir curules: " + e.getMessage());
        }

        log.info("Finalizando reparto de curules - exitoso: {}", 
                genericResponse.isSuccess());
        return genericResponse;
    }

    @Override
    public List<DistritoElectoralEmpateDTO> obtenerVotosEmpate(String esquema, ConsultaCifraRepartidoraRequestDto filtro) {
        List<DistritoElectoralEmpateDTO> response = new ArrayList<>();
        // Obtener los datos del procedimiento almacenado
        List<Map<String, Object>> resultado = distritoElectoralRepository.obtenerListadoVotosEmpate(esquema, filtro.getCodEleccion(), null);

        if (resultado == null || resultado.isEmpty()) {
            throw new BusinessValidationException("No se encontraron votos para el tipo de elección.");
        }


        List<VotosEmpateResultadoDto> listResultado = resultado.parallelStream()
                .map(this::llenarDatosVotosEmpate)
                .toList();

        // Agrupar por distrito electoral
        Map<String, List<VotosEmpateResultadoDto>> agrupados = listResultado.stream()
                .collect(Collectors.groupingBy(VotosEmpateResultadoDto::getDistritoElectoral));

        agrupados.forEach((codigoDistrito, agrupaciones) -> {
            DistritoElectoralEmpateDTO distrito = new DistritoElectoralEmpateDTO();

            // Tomar datos comunes del primer elemento
            VotosEmpateResultadoDto primera = agrupaciones.getFirst();
            distrito.setDistritoElectoral(primera.getDistritoElectoral());
            distrito.setDescripcionDistritoElectoral(primera.getDescripcionDistritoElectoral());
            distrito.setTipoEleccion(primera.getTipoEleccion());
            distrito.setDescripcionTipoEleccion(primera.getDescripcionTipoEleccion());
            distrito.setVotosValidos(primera.getVotosValidos());
            distrito.setAgrupacionesPoliticas(agrupaciones);

            response.add(distrito);
        });

        // Ordenar por código de distrito
        response.sort(
                Comparator.comparing(DistritoElectoralEmpateDTO::getDistritoElectoral)
        );

        return response;
    }

    @Override
    @Transactional(value = "locationTransactionManager")
    public ActualizarResolucionResponseDto actualizarResolucion(String esquema, ActualizarResolucionRequestDto filtro) {
        validarResoluciones(filtro);

        StringBuilder mensajeCompleto = new StringBuilder();
        boolean todasExitosas = true;
        int resolucionesExitosas = 0;
        int resolucionesFallidas = 0;

        for (ActualizarResolucionRequestBeanItem resolucion : filtro.getResoluciones()) {
            boolean exitoso = procesarYAcumularResultado(
                    esquema,
                    filtro.getUsuario(),
                    resolucion,
                    mensajeCompleto
            );

            if (exitoso) {
                resolucionesExitosas++;
            } else {
                resolucionesFallidas++;
                todasExitosas = false;
            }
        }

        return construirRespuesta(todasExitosas, resolucionesExitosas, resolucionesFallidas, mensajeCompleto.toString());

    }

    private List<VariableSistemaDto> obtenerVariablesSistema(String esquema){
        List<Map<String, Object>> listResultadoVariables = this.distritoElectoralRepository.obtenerVariablesSistema(esquema);
        return listResultadoVariables.parallelStream().map(this::llenarVariableSistema).toList();
    }
    
    private void validarConsolidarVotosAgrupacionRequest(ConsolidarVotosAgrupacionRequestDto filtro) {
        if (filtro == null) {
            throw new IllegalArgumentException("El objeto filtro no puede ser nulo");
        }
        
        if (filtro.getCodEleccion() == null || filtro.getCodEleccion().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de elección es obligatorio y no puede estar vacío");
        }
        
        if (filtro.getCodigoUsuario() == null || filtro.getCodigoUsuario().trim().isEmpty()) {
            throw new IllegalArgumentException("El código de usuario es obligatorio y no puede estar vacío");
        }
        
        if (filtro.getNombrePc() == null || filtro.getNombrePc().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del PC es obligatorio y no puede estar vacío");
        }
        
        // Validaciones de formato para tipo de elección
        if (!filtro.getCodEleccion().matches("^(10|12|13|14|15)$")) {
            throw new IllegalArgumentException("Tipo de elección inválido. Valores permitidos: 10, 12, 13, 14, 15");
        }       
        
    }

    @Override
    public byte[] reporteCifraRepartidora(String esquema, ConsultaCifraRepartidoraRequestDto filtro) throws JRException {
    	List<Map<String, Object>> resultadoMap = distritoElectoralRepository.obtenerReporteResultados(
                esquema, filtro.getCodEleccion(), filtro.getCodDistritoElectoral(), filtro.getEstadoCifra(), filtro.getTipoCifra());
    	
    	List<ReporteCifraRepartidoraDto> lista = resultadoMap.parallelStream().map(this::llenarDatosReporteCifraRepartidora).toList();
    	
    	BigDecimal porcentajeAvance = distritoElectoralRepository.obtenerPorcentajeAvance(
                esquema, filtro.getCodEleccion(), filtro.getCodDistritoElectoral() );
    	
    	Optional<ProcesoElectoral> proceso = this.procesoElectoralRepository.findById(filtro.getIdProceso());
    	
        Map<String, Object> parametrosReporte = new java.util.HashMap<>();
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe
        parametrosReporte.put("url_imagen", imagen);
        parametrosReporte.put("sinvaloroficial", utilSceService.getSinValorOficial(filtro.getIdProceso()));
        parametrosReporte.put("version", utilSceService.getVersionSistema());
        
        parametrosReporte.put("tituloSecundario", "CONSULTA DE CIFRA REPARTIDORA");        
        parametrosReporte.put("tituloPrincipal", proceso.isPresent() ? proceso.get().getNombre() : "");
        parametrosReporte.put("tipoEleccion", filtro.getCodEleccion());
        parametrosReporte.put("usuario", filtro.getUsuario());
        parametrosReporte.put("estado", filtro.getEstadoCifra());
        parametrosReporte.put("tipoCR", filtro.getTipoCifra());
        parametrosReporte.put("avance", porcentajeAvance != null ? porcentajeAvance.toString() : "0.00");

        return Funciones.generarReporte(this.getClass(), lista, ConstantesComunes.REPORTE_CIFRA_REPARTIDORA, parametrosReporte);
    }
    

    private ReporteCifraRepartidoraDto llenarDatosReporteCifraRepartidora(Map<String, Object> data) {
    	
    	DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Timestamp fechaProceso = (Timestamp) data.get(D_FECHA_PROCESO);
        String fechaFormateada = dateFormat.format(fechaProceso);
        
        BigDecimal factorCifra = (BigDecimal) data.get(N_FACTOR_CIFRA);
        BigDecimal votosValidos = (BigDecimal) data.get(N_VOTOS_VALIDOS);
        BigDecimal cocienteObtenido = (BigDecimal) data.get("c_cociente_obtenido");
        BigDecimal escanosObtenidos = (BigDecimal) data.get(N_ESCANOS_OBTENIDOS);
        BigDecimal nroCurules = (BigDecimal) data.get("n_cantidad_curules");

        String cocienteString = cocienteObtenido == null ? "" : cocienteObtenido.toString();
        String factorCifraString = factorCifra == null ? "" : factorCifra.toString();
        
    	ReporteCifraRepartidoraDto response = new ReporteCifraRepartidoraDto();
    	response.setOrganizacionPolitica((String) data.get(C_DESCRIPCION_AGRUPACION_POLITICA));
    	response.setVotosValidos(votosValidos != null ? votosValidos.intValue() : null);
    	response.setVotosCifraRepartidora((String) data.get("c_division"));
    	response.setCociente(cocienteString);
    	response.setNroRepresentantes(escanosObtenidos != null ? escanosObtenidos.intValue() : null);
    	response.setColumnObservaciones((String) data.get("c_observacion_cifra"));
    	response.setCodDistritoElectoral((String) data.get(C_DISTRITO_ELECTORAL));
    	response.setDescDistritoElectoral((String) data.get(C_DESCRIPCION_DISTRITO_ELECTORAL));
    	response.setFactorCr(factorCifraString);
    	response.setNroEscanos(escanosObtenidos != null ? escanosObtenidos.intValue() : null);
    	response.setEstadoCr((String) data.get(C_ESTADO_CIFRA));
    	response.setFechaProcCr(fechaFormateada);
    	response.setNroCurules(nroCurules != null ? nroCurules.intValue() : null);
        return response;
    }

    private void validarResolucion(ActualizarResolucionRequestBeanItem resolucion) {
        if (resolucion.getTipoEleccion() == null || resolucion.getTipoEleccion().trim().isEmpty()) {
            throw new BusinessValidationException("El tipo de elección es obligatorio");
        }

        if (resolucion.getDistritoElectoral() == null || resolucion.getDistritoElectoral().trim().isEmpty()) {
            throw new BusinessValidationException("El distrito electoral es obligatorio");
        }

        if (resolucion.getAgrupacionesPoliticasGanadoras() == null ||
                resolucion.getAgrupacionesPoliticasGanadoras().isEmpty()) {
            throw new BusinessValidationException(
                    String.format("Debe seleccionar al menos una agrupación ganadora para el distrito %s",
                            resolucion.getDistritoElectoral())
            );
        }

        if (resolucion.getNumeroResolucion() == null || resolucion.getNumeroResolucion().trim().isEmpty()) {
            throw new BusinessValidationException(
                    String.format("El número de resolución es obligatorio para el distrito %s",
                            resolucion.getDistritoElectoral())
            );
        }
    }

    private String construirMensajeResumen(int exitosas, int fallidas) {
        int total = exitosas + fallidas;

        if (fallidas == 0) {
            return String.format("Se actualizaron exitosamente %d resolución(es) de empate.", exitosas);
        } else if (exitosas == 0) {
            return String.format("No se pudo actualizar ninguna resolución. %d error(es).", fallidas);
        } else {
            return String.format(
                    "Se procesaron %d resolución(es): %d exitosa(s) y %d fallida(s).",
                    total, exitosas, fallidas
            );
        }
    }

    private boolean procesarYAcumularResultado(
            String esquema,
            String usuario,
            ActualizarResolucionRequestBeanItem resolucion,
            StringBuilder mensajeCompleto) {

        try {
            validarResolucion(resolucion);

            String agrupacionesPoliticasStr = String.join(",", resolucion.getAgrupacionesPoliticasGanadoras());
            Map<String, Object> maps = ejecutarProcedimientoAlmacenado(esquema, usuario, resolucion, agrupacionesPoliticasStr);

            return procesarYAgregarMensaje(maps, resolucion.getDistritoElectoral(), mensajeCompleto);

        } catch (Exception e) {
            log.error("Error al actualizar resolución para distrito {}: {}",
                    resolucion.getDistritoElectoral(), e.getMessage(), e);
            mensajeCompleto.append(String.format("Distrito %s: Error - %s%n",
                    resolucion.getDistritoElectoral(), e.getMessage()));
            return false;
        }
    }

    private boolean procesarYAgregarMensaje(
            Map<String, Object> maps,
            String distritoElectoral,
            StringBuilder mensajeCompleto) {

        Integer resultado = extraerResultado(maps);
        String mensaje = extraerMensaje(maps);

        mensajeCompleto.append(String.format("Distrito %s: %s%n", distritoElectoral, mensaje));

        return resultado != null && resultado == 1;
    }

    private Integer extraerResultado(Map<String, Object> maps) {
        return maps.get(PO_RESULTADO) != null ?
                ((Number) maps.get(PO_RESULTADO)).intValue() : -1;
    }

    private String extraerMensaje(Map<String, Object> maps) {
        return maps.get(PO_MENSAJE) != null ?
                maps.get(PO_MENSAJE).toString() : SIN_MENSAJE;
    }

    private void validarResoluciones(ActualizarResolucionRequestDto filtro) {
        if (filtro.getResoluciones() == null || filtro.getResoluciones().isEmpty()) {
            throw new BusinessValidationException("No se recibieron resoluciones para actualizar");
        }
    }

    private Map<String, Object> ejecutarProcedimientoAlmacenado(
            String esquema,
            String usuario,
            ActualizarResolucionRequestBeanItem resolucion,
            String agrupacionesPoliticasStr) {

        Map<String, Object> maps = distritoElectoralRepository.actualizarResolucion(
                esquema,
                resolucion.getTipoEleccion(),
                resolucion.getDistritoElectoral(),
                agrupacionesPoliticasStr,
                "1",
                resolucion.getNumeroResolucion(),
                "4",
                "",
                usuario,
                "",
                0,
                ""
        );

        if (maps == null) {
            throw new BusinessValidationException("Error: Respuesta inválida del procedimiento almacenado");
        }

        return maps;
    }

    private ActualizarResolucionResponseDto construirRespuesta(
            boolean todasExitosas,
            int resolucionesExitosas,
            int resolucionesFallidas,
            String mensajeCompleto) {

        ActualizarResolucionResponseDto response = new ActualizarResolucionResponseDto();
        String mensajeResumen = construirMensajeResumen(resolucionesExitosas, resolucionesFallidas);

        response.setTodasExitosas(todasExitosas);
        response.setResolucionesExitosas(resolucionesExitosas);
        response.setResolucionesFallidas(resolucionesFallidas);
        response.setMensajeCompleto(mensajeCompleto);
        response.setMensajeResumen(mensajeResumen);

        return response;
    }
    
}
