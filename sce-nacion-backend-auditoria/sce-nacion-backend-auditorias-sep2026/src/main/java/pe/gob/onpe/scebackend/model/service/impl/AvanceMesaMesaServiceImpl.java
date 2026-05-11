package pe.gob.onpe.scebackend.model.service.impl;

import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.dto.AvanceMesaReporteDto;
import pe.gob.onpe.scebackend.model.dto.FiltroAvanceMesaDto;
import pe.gob.onpe.scebackend.model.dto.request.comun.UbiEleccionAgrupolRequestDto;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteActaRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteAvanceMesaReportePreferencialDto;
import pe.gob.onpe.scebackend.model.orc.repository.ActaRepository;
import pe.gob.onpe.scebackend.model.orc.repository.comun.IUbiEleccionAgrupolRepositoryCustom;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.model.service.reporte.AvanceMesaMesaService;
import pe.gob.onpe.scebackend.utils.DateUtil;
import pe.gob.onpe.scebackend.utils.SceConstantes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesReportes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.util.*;

@Service
public class AvanceMesaMesaServiceImpl implements AvanceMesaMesaService {

    public static final String P_REPORTE = "p_reporte";
    public static final String C_CVAS = "c_cvas";
    public static final String N_ACTA_PK = "n_acta_pk";
    public static final String C_NOMBRE_AMBITO_ELECTORAL = "c_nombre_ambito_electoral";
    public static final String C_CODIGO_AMBITO_ELECTORAL = "c_codigo_ambito_electoral";
    public static final String C_CODIGO_CENTRO_COMPUTO = "c_codigo_centro_computo";
    public static final String C_NOMBRE_CENTRO_COMPUTO = "c_nombre_centro_computo";
    public static final String C_NOMBRE_LOCAL_VOTACION = "c_nombre_local_votacion";
    public static final String C_MESA = "c_mesa";
    public static final String C_NUMERO_COPIA = "c_numero_copia";
    public static final String C_VOTOS = "c_votos";
    public static final String C_AUD_USUARIO_VERIFICADOR = "c_aud_usuario_verificador";
    public static final String N_ELECTORES_HABILES = "n_electores_habiles";
    public static final String C_DESCRIPCION_ESTADO_ACTA = "c_descripcion_estado_acta";
    public static final String N_POSICION = "n_posicion";
    public static final String CODIGO_UBIGEO = "c_codigo_ubigeo";
    public static final String D_AUDITORIA_FECHA_VERI = "d_aud_fecha_verificador";

    private final UtilSceService utilSceService;
    private final ActaRepository actaRepository;
    private final IUbiEleccionAgrupolRepositoryCustom ubiEleccionAgrupolRepositoryCustom;
    private final ITabLogTransaccionalService logService;

    public AvanceMesaMesaServiceImpl(UtilSceService utilSceService, ActaRepository actaRepository,
                                     IUbiEleccionAgrupolRepositoryCustom ubiEleccionAgrupolRepositoryCustom,
            ITabLogTransaccionalService logService) {
        this.utilSceService = utilSceService;
        this.actaRepository = actaRepository;
        this.ubiEleccionAgrupolRepositoryCustom = ubiEleccionAgrupolRepositoryCustom;
        this.logService = logService;
    }

    @Override
    @Transactional("locationTransactionManager")
    public byte[] reporteAvanceMesa(FiltroAvanceMesaDto filtro) throws JRException {
        prepararFiltros(filtro);

        ReporteActaRequestDto requestDto = mapearParametros(filtro);
        Map<String, Object> parametros = inicializarParametros(filtro);

        String nombreReporte;
        List<?> listaAvancesMesa;

        if (esCPR(filtro)) { //CPR
            nombreReporte = ConstantesComunes.AVANCE_MESA_CPR_REPORT_JRXML;
            listaAvancesMesa = obtenerDatosCPR(requestDto, filtro);
        } else if (esPreferencial(filtro)) { //EG
            nombreReporte = obtenerNombreReportePreferencial(requestDto, parametros);
            listaAvancesMesa = obtenerDatosPreferenciales(requestDto);
        } else { //EG o EMC
            nombreReporte = ConstantesComunes.AVANCE_MESA_REPORT_JRXML;
            listaAvancesMesa = obtenerDatosNormales(requestDto, filtro);
        }

        this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE,
                this.getClass().getSimpleName(), "Se consultó el Reporte de Mesa por Mesa.", "",
                filtro.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);

        return Funciones.generarReporte(this.getClass(), listaAvancesMesa, nombreReporte, parametros);
    }

    private void prepararFiltros(FiltroAvanceMesaDto filtro) {
        filtro.setMesa(StringUtils.isBlank(filtro.getMesa()) ? null : filtro.getMesa());
        filtro.setDepartamento(StringUtils.isBlank(filtro.getDepartamento()) ? null : filtro.getDepartamento());
        filtro.setProvincia(StringUtils.isBlank(filtro.getProvincia()) ? null : filtro.getProvincia());
        filtro.setDistrito(StringUtils.isBlank(filtro.getDistrito()) ? null : filtro.getDistrito());
    }

    private Map<String, Object> inicializarParametros(FiltroAvanceMesaDto filtro) {
        Map<String, Object> parametros = new HashMap<>();
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON + "onpe.jpg");

        parametros.put("url_imagen", imagen);
        parametros.put("sinValorOficial", utilSceService.getSinValorOficial());
        parametros.put("p_eleccion", filtro.getEleccion());
        parametros.put("p_proceso", filtro.getProceso());
        parametros.put("p_usuario", filtro.getUsuario());
        parametros.put("p_version", utilSceService.getVersionSistema());
        parametros.put(P_REPORTE, ConstantesReportes.NAME_REPORTE_AVANCE_MESA_POR_MESA1);
        parametros.put("p_ambito_electoral", filtro.getCodigoAmbitoElectoral() + " - " + filtro.getAmbitoElectoral());
        parametros.put("p_centro_computo", filtro.getCodigoCentroComputo() + " - " + filtro.getCentroComputo());
        parametros.put("resolucionJne", utilSceService.getResolucionJne());

        return parametros;
    }

    private boolean esCPR(FiltroAvanceMesaDto filtro) {
        return filtro.getCodigoEleccion().equals(ConstantesComunes.COD_ELEC_REV_DIST);
    }

    private boolean esPreferencial(FiltroAvanceMesaDto filtro) {
        return filtro.getPreferencial() == 1;
    }

    private List<AvanceMesaReporteDto> obtenerDatosCPR(ReporteActaRequestDto requestDto, FiltroAvanceMesaDto filtro) {
        return actaRepository.avanceMesaMesaCPR(requestDto.getEsquema(),
                        requestDto.getIdEleccion(),
                        requestDto.getIdAmbitoElectoral(),
                        requestDto.getIdCentroComputo(),
                        requestDto.getUbigeo(),
                        requestDto.getMesa(),
                        requestDto.getUsuario())
                .parallelStream()
                .map(reporte -> mapearResponseCPR(reporte, filtro))
                .sorted(Comparator
                        .comparing(AvanceMesaReporteDto::getIdActa)
                        .thenComparing(AvanceMesaReporteDto::getPosicion))
                .toList();
    }

    private List<ReporteAvanceMesaReportePreferencialDto> obtenerDatosPreferenciales(ReporteActaRequestDto requestDto) {
        List<Map<String, Object>> resultados = actaRepository.avanceMesaMesaPreferencial(
                requestDto.getEsquema(),
                requestDto.getIdEleccion(),
                requestDto.getIdAmbitoElectoral(),
                requestDto.getIdCentroComputo(),
                requestDto.getUbigeo(),
                requestDto.getMesa(),
                requestDto.getUsuario()
        );

        return resultados.parallelStream()
                .map(this::mapearResponsePreferencial)
                .sorted(Comparator
                        .comparing(ReporteAvanceMesaReportePreferencialDto::getIdActa)
                        .thenComparing(ReporteAvanceMesaReportePreferencialDto::getPosicion))
                .toList();
    }

    private String obtenerNombreReportePreferencial(ReporteActaRequestDto requestDto, Map<String, Object> parametros) {
        parametros.put(P_REPORTE, ConstantesReportes.NAME_REPORTE_AVANCE_MESA_POR_MESA_PREFERENCIAL);

        Integer cantidadAgrupacionPreferencial = obtenerCantidadAgrupacionPreferencial(requestDto);
        parametros.put("p_cantidad_columna", cantidadAgrupacionPreferencial);

        return seleccionarReporteSegunCantidad(cantidadAgrupacionPreferencial);
    }

    private Integer obtenerCantidadAgrupacionPreferencial(ReporteActaRequestDto requestDto) {
        if (ConstantesComunes.ELECCION_PARLAMENTO_ANDINO.equals(requestDto.getIdEleccion())) {
            return ConstantesComunes.CANTIDAD_VOTOS_PREFERENCIALES_PARLAMENTO;
        } else {
            UbiEleccionAgrupolRequestDto ubiEleccionAgrupolRequestDto = new UbiEleccionAgrupolRequestDto();
            ubiEleccionAgrupolRequestDto.setIdEleccion(requestDto.getIdEleccion());
            ubiEleccionAgrupolRequestDto.setEsquema(requestDto.getEsquema());
            ubiEleccionAgrupolRequestDto.setUbigeo(requestDto.getUbigeo());
            ubiEleccionAgrupolRequestDto.setMesa(requestDto.getMesa());
            return ubiEleccionAgrupolRepositoryCustom.obtenerCantidadAgrupacionPreferencial(ubiEleccionAgrupolRequestDto);
        }
    }

    private String seleccionarReporteSegunCantidad(Integer cantidad) {
        if (cantidad <= 9) {
            return ConstantesComunes.AVANCE_MESA_PREFERENCIAL_REPORT_JRXML_9;
        } else if (cantidad <= 18) {
            return ConstantesComunes.AVANCE_MESA_PREFERENCIAL_REPORT_JRXML_18;
        } else if (cantidad <= 27) {
            return ConstantesComunes.AVANCE_MESA_PREFERENCIAL_REPORT_JRXML_27;
        } else {
            return ConstantesComunes.AVANCE_MESA_PREFERENCIAL_REPORT_JRXML;
        }
    }

    private List<AvanceMesaReporteDto> obtenerDatosNormales(ReporteActaRequestDto requestDto, FiltroAvanceMesaDto filtro) {
        List<Map<String, Object>> resultados = actaRepository.avanceMesaMesa(
                requestDto.getEsquema(),
                requestDto.getIdEleccion(),
                requestDto.getIdAmbitoElectoral(),
                requestDto.getIdCentroComputo(),
                requestDto.getUbigeo(),
                requestDto.getMesa(),
                requestDto.getUsuario()
        );

        return resultados.parallelStream()
                .map(reporte -> mapearResponse(reporte, filtro))
                .sorted(Comparator
                        .comparing(AvanceMesaReporteDto::getIdActa)
                        .thenComparing(AvanceMesaReporteDto::getPosicion))
                .toList();
    }

    private String seguridadString(Map<String, Object> reporte, String key) {
        return Optional.ofNullable((String) reporte.get(key)).orElse("");
    }

    private void mapearCamposComunes(AvanceMesaReporteDto mesaReporte, Map<String, Object> reporte, FiltroAvanceMesaDto filtro) {
        mesaReporte.setIdActa((Long) reporte.get(N_ACTA_PK));
        mesaReporte.setProceso(filtro.getProceso().toUpperCase());
        mesaReporte.setEleccion(filtro.getEleccion());
        mesaReporte.setAmbito((String) reporte.get(C_NOMBRE_AMBITO_ELECTORAL));
        mesaReporte.setAmbitoElectoral((String) reporte.get(C_NOMBRE_AMBITO_ELECTORAL));
        mesaReporte.setCodigoAmbitoElectoral((String) reporte.get(C_CODIGO_AMBITO_ELECTORAL));
        mesaReporte.setCodigoCentroComputo((String) reporte.get(C_CODIGO_CENTRO_COMPUTO));
        mesaReporte.setCentroComputo((String) reporte.get(C_NOMBRE_CENTRO_COMPUTO));
        mesaReporte.setDepartamento((String) reporte.get("c_departamento"));
        mesaReporte.setProvincia((String) reporte.get("c_provincia"));
        mesaReporte.setDistrito((String) reporte.get("c_distrito"));
        mesaReporte.setLocalVotacion((String) reporte.get(C_NOMBRE_LOCAL_VOTACION));
        mesaReporte.setNumeroMesa((String) reporte.get(C_MESA));
        mesaReporte.setCopia(seguridadString(reporte, C_NUMERO_COPIA));
        mesaReporte.setVerificador(seguridadString(reporte, C_AUD_USUARIO_VERIFICADOR));
    }

    private AvanceMesaReporteDto mapearResponse(Map<String, Object> reporte, FiltroAvanceMesaDto filtro) {
        AvanceMesaReporteDto mesaReporte = new AvanceMesaReporteDto();
        mapearCamposComunes(mesaReporte,reporte,filtro);

        mesaReporte.setFechaHora(DateUtil.getDateString((Date) reporte.get(D_AUDITORIA_FECHA_VERI), SceConstantes.FORMATO_FECHA_REPORTE));
        String cantidad = (String) reporte.get(C_VOTOS);
        mesaReporte.setCvas((String) reporte.get(C_CVAS));
        mesaReporte.setCantidadElectoresHabiles(((Integer) reporte.get(N_ELECTORES_HABILES)).longValue());
        mesaReporte.setEstado((String) reporte.get(C_DESCRIPCION_ESTADO_ACTA));
        mesaReporte.setEtiquetaEstado((String) reporte.get(C_DESCRIPCION_ESTADO_ACTA));
        mesaReporte.setAgrupacionPolitica((String) reporte.get("c_descripcion"));
        mesaReporte.setIdAgrupacionPolitica((Integer) reporte.get("n_agrupacion_politica_pk"));
        Short posi = (Short) reporte.get(N_POSICION);
        mesaReporte.setPosicion(Long.valueOf(posi));
        mesaReporte.setVotos(cantidad);
        mesaReporte.setTotalVotos(cantidad);
        mesaReporte.setCodigoUbigeo((String) reporte.get(CODIGO_UBIGEO));
        
        return mesaReporte;
    }

    private ReporteAvanceMesaReportePreferencialDto mapearResponsePreferencial(Map<String, Object> reporte) {
        ReporteAvanceMesaReportePreferencialDto response = new ReporteAvanceMesaReportePreferencialDto();
        response.setIdAmbitoElectoral((Integer)reporte.get("n_ambito_electoral"));
        response.setCodigoAmbitoElectoral((String) reporte.get(C_CODIGO_AMBITO_ELECTORAL));
        response.setNombreAmbitoElectoral((String) reporte.get(C_NOMBRE_AMBITO_ELECTORAL));
        response.setIdCentroComputo((Integer) reporte.get("n_centro_computo"));
        response.setCodigoCentroComputo((String) reporte.get(C_CODIGO_CENTRO_COMPUTO));
        response.setNombreCentroComputo((String) reporte.get(C_NOMBRE_CENTRO_COMPUTO));
        response.setIdDistritoElectoral((Integer) reporte.get("n_distrito_electoral"));
        response.setCodigoDistritoElectoral((String) reporte.get("c_codigo_distrito_electoral"));
        response.setNombreDistritoElectoral((String) reporte.get("c_nombre_distrito_electoral"));
        response.setIdUbigeo((Integer) reporte.get("n_ubigeo_pk"));
        response.setCodigoUbigeo((String) reporte.get(CODIGO_UBIGEO));
        response.setNombreUbigeoNivelUno((String) reporte.get("c_nombre_ubigeo_nivel_01"));
        response.setNombreUbigeoNivelDos((String) reporte.get("c_nombre_ubigeo_nivel_02"));
        response.setNombreUbigeoNivelTres((String) reporte.get("c_nombre_ubigeo_nivel_03"));
        response.setIdLocalVotacion((Integer) reporte.get("n_local_votacion_pk"));
        response.setCodigoLocalVotacion((String) reporte.get("c_codigo_local_votacion"));
        response.setNombreLocalVotacion((String) reporte.get(C_NOMBRE_LOCAL_VOTACION));
        response.setIdMesa((Integer) reporte.get("n_mesa_pk"));
        response.setMesa((String) reporte.get(C_MESA));
        response.setIdActa((Long) reporte.get(N_ACTA_PK));
        response.setNumeroCopia((String) reporte.get(C_NUMERO_COPIA));
        response.setElectoresHabiles((Integer) reporte.get(N_ELECTORES_HABILES));
        response.setCvas((String) reporte.get(C_CVAS));
        response.setEstadoActa((String) reporte.get("c_estado_acta"));
        response.setDescripcionEstadoActa((String) reporte.get(C_DESCRIPCION_ESTADO_ACTA));
        response.setPosicion((Short) reporte.get(N_POSICION));
        Integer codigoAgrupacionPolitica = (Integer) reporte.get("n_agrupacion_politica_pk");
        response.setIdAgrupacionPolitica(codigoAgrupacionPolitica);
        response.setDescripcionAgrupacionPolitica((String) reporte.get("c_descripcion_agrupacion_politica"));
        response.setVotos((String) reporte.get(C_VOTOS));
        response.setEsAgrupacionPolitica(esAgrupacionPolitica(codigoAgrupacionPolitica));
        response.setVotoPreferencial01((String) reporte.get("c_voto_pref_lista_1"));
        response.setVotoPreferencial02((String) reporte.get("c_voto_pref_lista_2"));
        response.setVotoPreferencial03((String) reporte.get("c_voto_pref_lista_3"));
        response.setVotoPreferencial04((String) reporte.get("c_voto_pref_lista_4"));
        response.setVotoPreferencial05((String) reporte.get("c_voto_pref_lista_5"));
        response.setVotoPreferencial06((String) reporte.get("c_voto_pref_lista_6"));
        response.setVotoPreferencial07((String) reporte.get("c_voto_pref_lista_7"));
        response.setVotoPreferencial08((String) reporte.get("c_voto_pref_lista_8"));
        response.setVotoPreferencial09((String) reporte.get("c_voto_pref_lista_9"));
        response.setVotoPreferencial10((String) reporte.get("c_voto_pref_lista_10"));
        response.setVotoPreferencial11((String) reporte.get("c_voto_pref_lista_11"));
        response.setVotoPreferencial12((String) reporte.get("c_voto_pref_lista_12"));
        response.setVotoPreferencial13((String) reporte.get("c_voto_pref_lista_13"));
        response.setVotoPreferencial14((String) reporte.get("c_voto_pref_lista_14"));
        response.setVotoPreferencial15((String) reporte.get("c_voto_pref_lista_15"));
        response.setVotoPreferencial16((String) reporte.get("c_voto_pref_lista_16"));
        response.setVotoPreferencial17((String) reporte.get("c_voto_pref_lista_17"));
        response.setVotoPreferencial18((String) reporte.get("c_voto_pref_lista_18"));
        response.setVotoPreferencial19((String) reporte.get("c_voto_pref_lista_19"));
        response.setVotoPreferencial20((String) reporte.get("c_voto_pref_lista_20"));
        response.setVotoPreferencial21((String) reporte.get("c_voto_pref_lista_21"));
        response.setVotoPreferencial22((String) reporte.get("c_voto_pref_lista_22"));
        response.setVotoPreferencial23((String) reporte.get("c_voto_pref_lista_23"));
        response.setVotoPreferencial24((String) reporte.get("c_voto_pref_lista_24"));
        response.setVotoPreferencial25((String) reporte.get("c_voto_pref_lista_25"));
        response.setVotoPreferencial26((String) reporte.get("c_voto_pref_lista_26"));
        response.setVotoPreferencial27((String) reporte.get("c_voto_pref_lista_27"));
        response.setVotoPreferencial28((String) reporte.get("c_voto_pref_lista_28"));
        response.setVotoPreferencial29((String) reporte.get("c_voto_pref_lista_29"));
        response.setVotoPreferencial30((String) reporte.get("c_voto_pref_lista_30"));
        response.setVotoPreferencial31((String) reporte.get("c_voto_pref_lista_31"));
        response.setVotoPreferencial32((String) reporte.get("c_voto_pref_lista_32"));
        response.setVotoPreferencial33((String) reporte.get("c_voto_pref_lista_33"));
        response.setVotoPreferencial34((String) reporte.get("c_voto_pref_lista_34"));
        response.setVotoPreferencial35((String) reporte.get("c_voto_pref_lista_35"));
        response.setVotoPreferencial36((String) reporte.get("c_voto_pref_lista_36"));
        response.setFechaProcesamiento(DateUtil.getDateString((Date) reporte.get(D_AUDITORIA_FECHA_VERI), SceConstantes.FORMATO_FECHA_REPORTE));
        response.setVerificador((String) reporte.get(C_AUD_USUARIO_VERIFICADOR));
        return response;
    }

    Boolean esAgrupacionPolitica(Integer codigoAgrupacionPolitica) {

        return codigoAgrupacionPolitica.longValue() != ConstantesComunes.NCODI_AGRUPOL_VOTOS_BLANCOS &&
                codigoAgrupacionPolitica.longValue() != ConstantesComunes.NCODI_AGRUPOL_VOTOS_NULOS &&
                codigoAgrupacionPolitica.longValue() != ConstantesComunes.NCODI_AGRUPOL_VOTOS_IMPUGNADOS;
    }

    private AvanceMesaReporteDto mapearResponseCPR(Map<String, Object> reporte, FiltroAvanceMesaDto filtro) {
        AvanceMesaReporteDto mesaReporte = new AvanceMesaReporteDto();
        mapearCamposComunes(mesaReporte, reporte, filtro);

        mesaReporte.setCodUbigeo((String) reporte.get(CODIGO_UBIGEO));
        mesaReporte.setCodLocal( (String) reporte.get("c_codigo_local_votacion") );
        mesaReporte.setFechaHora(DateUtil.getDateString((Date) reporte.get(D_AUDITORIA_FECHA_VERI), SceConstantes.FORMATO_FECHA));
        String cantidad = (String) reporte.get(C_VOTOS);
        mesaReporte.setCvas((String) reporte.get(C_CVAS));

        mesaReporte.setCantidadElectoresHabiles(((Integer) reporte.get(N_ELECTORES_HABILES)).longValue());
        mesaReporte.setEstado((String) reporte.get(C_DESCRIPCION_ESTADO_ACTA));
        mesaReporte.setEtiquetaEstado((String) reporte.get(C_DESCRIPCION_ESTADO_ACTA));
        mesaReporte.setDescAgrupol((String) reporte.get("c_nombres_apellidos"));
        Short posi = (Short) reporte.get(N_POSICION);
        mesaReporte.setPosicion(Long.valueOf(posi));
        mesaReporte.setVotos(cantidad);
        mesaReporte.setTotalVotos(cantidad);
        mesaReporte.setVotosSI((String) reporte.get("c_voto_opcion_si"));
        mesaReporte.setVotosNO((String) reporte.get("c_voto_opcion_no"));
        mesaReporte.setVotosBL((String) reporte.get("c_voto_opcion_blanco"));
        mesaReporte.setVotosNL((String) reporte.get("c_voto_opcion_nulo"));
        mesaReporte.setVotosIM((String) reporte.get("c_voto_opcion_impugnados"));

        return mesaReporte;
    }

    private ReporteActaRequestDto mapearParametros(FiltroAvanceMesaDto filtro) {
        String schema = filtro.getSchema();
        String usuario = filtro.getUsuario();
        Integer idEleccion = filtro.getIdEleccion();
        Integer idAmbito = filtro.getIdAmbito() != null ? Math.toIntExact(filtro.getIdAmbito()) : null;
        Integer idCentroComputo = filtro.getIdCentroComputo() != null ? Math.toIntExact(filtro.getIdCentroComputo()) : null;
        String ubigeo = obtenerUbigeo(filtro.getDepartamento(), filtro.getProvincia(), filtro.getDistrito());

        String mesa = StringUtils.isBlank(filtro.getMesa()) ? null : filtro.getMesa();

        ReporteActaRequestDto requestDto = new ReporteActaRequestDto();
        requestDto.setEsquema(schema);
        requestDto.setUsuario(usuario);
        requestDto.setUbigeo(ubigeo);
        requestDto.setIdEleccion(idEleccion);
        requestDto.setIdAmbitoElectoral(idAmbito);
        requestDto.setIdCentroComputo(idCentroComputo);
        requestDto.setMesa(mesa);
        return requestDto;
    }

    private String obtenerUbigeo(String ubigeoNivelUno,String ubigeoNivelDos,String ubigeoNivelTres) {
        String ubigeRetorno = ConstantesReportes.CODIGO_UBIGEO_NACION;
        if(!ConstantesReportes.CODIGO_UBIGEO_NACION.equals(ubigeoNivelTres)) {
            ubigeRetorno = ubigeoNivelTres;
        }
        else if(!ConstantesReportes.CODIGO_UBIGEO_NACION.equals(ubigeoNivelDos)) {
            ubigeRetorno = ubigeoNivelDos;
        }
        else if(!ConstantesReportes.CODIGO_UBIGEO_NACION.equals(ubigeoNivelUno)) {
            ubigeRetorno = ubigeoNivelUno;
        }
        return ubigeRetorno;
    }
}
