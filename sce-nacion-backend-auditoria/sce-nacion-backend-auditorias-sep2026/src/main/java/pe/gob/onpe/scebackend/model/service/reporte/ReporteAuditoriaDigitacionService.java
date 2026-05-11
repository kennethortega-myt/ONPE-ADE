package pe.gob.onpe.scebackend.model.service.reporte;

import lombok.extern.java.Log;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.dto.reportes.AuditoriaDigitacionCPRJasperDto;
import pe.gob.onpe.scebackend.model.dto.request.comun.UbiEleccionAgrupolRequestDto;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteActaRequestDto;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteAuditoriaDigitacionActaRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteAuditoriaDigitacionActaCPRDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteAuditoriaDigitacionActaDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteAuditoriaDigitacionActaPrefencialDto;
import pe.gob.onpe.scebackend.model.orc.repository.ActaRepository;
import pe.gob.onpe.scebackend.model.orc.repository.comun.IUbiEleccionAgrupolRepositoryCustom;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.model.service.mapper.ReporteAuditoriaDigitacionMapper;
import pe.gob.onpe.scebackend.utils.TransactionalLogUtil;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesReportes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log
public class ReporteAuditoriaDigitacionService implements IReporteAuditoriaDigitacionService {

    private final IUbiEleccionAgrupolRepositoryCustom ubiEleccionAgrupolRepositoryCustom;
    private final ActaRepository actaRepository;
    private final ReporteAuditoriaDigitacionMapper reporteAuditoriaDigitacionMapper;
    private final UtilSceService utilSceService;
    private final ITabLogTransaccionalService logService;

    public ReporteAuditoriaDigitacionService(
            IUbiEleccionAgrupolRepositoryCustom ubiEleccionAgrupolRepositoryCustom,
            ActaRepository actaRepository,
            ReporteAuditoriaDigitacionMapper reporteAuditoriaDigitacionMapper, UtilSceService utilSceService, ITabLogTransaccionalService logService) {
        this.ubiEleccionAgrupolRepositoryCustom = ubiEleccionAgrupolRepositoryCustom;
        this.actaRepository = actaRepository;
        this.reporteAuditoriaDigitacionMapper = reporteAuditoriaDigitacionMapper;
        this.utilSceService = utilSceService;
        this.logService = logService;
    }

    @Override
    @Transactional("locationTransactionManager")
    public byte[] reporteAuditoriaDigitacion(ReporteAuditoriaDigitacionActaRequestDto filtro) throws JRException, SQLException {
        List<?> auditoriaDigitacionJasperDtoList = null;
        filtro.setMesa(filtro.getMesa() != null && filtro.getMesa().trim().isEmpty() ? null : filtro.getMesa());
        filtro.setDepartamento(filtro.getDepartamento() != null && filtro.getDepartamento().trim().isEmpty() ? null : filtro.getDepartamento());
        filtro.setProvincia(filtro.getProvincia() != null && filtro.getProvincia().trim().isEmpty() ? null : filtro.getProvincia());
        filtro.setDistrito(filtro.getDistrito() != null && filtro.getDistrito().trim().isEmpty() ? null : filtro.getDistrito());

        ReporteActaRequestDto requestDto = mapearParametros(filtro);
        Map<String, Object> parametros = new HashMap<>();
        List<ReporteAuditoriaDigitacionActaDto> reporteAuditoriaDigitacionActaDtoList = null;
        List<ReporteAuditoriaDigitacionActaPrefencialDto> reporteAuditoriaDigitacionActaPrefencialDtoList = null;
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON + "onpe.jpg");//logo onpe

        parametros.put("url_imagen", imagen);
        parametros.put("sinvaloroficial", utilSceService.getSinValorOficial());
        parametros.put("usuario", filtro.getUsuario());
        parametros.put("version", utilSceService.getVersionSistema());
        parametros.put("reporte", ConstantesReportes.NAME_REPORTE_AUDITORIA_DIGITACION);
        parametros.put("tituloPrincipal", filtro.getProceso());
        parametros.put("tituloEleccionCompleto", "");
        parametros.put("tituloEleccionSimple", filtro.getEleccion());
        parametros.put("tituloRep", ConstantesComunes.TITULO_ELECCION_VERIFICACION_DIGITACION_ACTA);
        parametros.put("resolucionJne", utilSceService.getResolucionJne());

        String nombreReporte = "";

        //Flujo para revocatoria
        if (filtro.getCodigoEleccion().equals(ConstantesComunes.COD_ELEC_REV_DIST)) {
            parametros.put("p_reporte", ConstantesReportes.NAME_REPORTE_AUDITORIA_DIGITACION);

            List<ReporteAuditoriaDigitacionActaCPRDto> reporteAuditoriaDigitacionActaCPRDtoList =
                actaRepository.auditoriaDigitacionCPR(
                        requestDto.getEsquema(),
                        requestDto.getIdEleccion(),
                        requestDto.getIdAmbitoElectoral(),
                        requestDto.getIdCentroComputo(),
                        requestDto.getUbigeo(),
                        requestDto.getMesa())
                    .parallelStream()
                    .map(this::mapearResponseCPR)
                    .sorted(Comparator
                        .comparing(ReporteAuditoriaDigitacionActaCPRDto::getMesa)
                        .thenComparing(ReporteAuditoriaDigitacionActaCPRDto::getPosicion))
                    .toList();

            auditoriaDigitacionJasperDtoList = reporteAuditoriaDigitacionMapper.toAuditoriaDigitacionCPRJasperDtoList(reporteAuditoriaDigitacionActaCPRDtoList);

            if (auditoriaDigitacionJasperDtoList instanceof List) {
                @SuppressWarnings("unchecked")
                List<AuditoriaDigitacionCPRJasperDto> jasperList = (List<AuditoriaDigitacionCPRJasperDto>) auditoriaDigitacionJasperDtoList;

                jasperList.forEach(jasperDto -> {
                    if (jasperDto.getNumActa() != null) {
                        jasperDto.setNumActa(
                            jasperDto.getNumActa().replaceAll(" - null$", " -")
                        );
                    }
                });
            }
            nombreReporte = ConstantesComunes.REPORTE_AUDITORIA_DIGITACION_REVOCATORIA;

        } else {
            //Flujo par eleccciones generales
            if (filtro.getPreferencial() == 0) {
                parametros.put("p_reporte", ConstantesReportes.NAME_REPORTE_AUDITORIA_DIGITACION);
                reporteAuditoriaDigitacionActaDtoList = actaRepository.auditoriaDigitacion(
                        requestDto.getEsquema(),
                        requestDto.getIdEleccion(),
                        requestDto.getIdAmbitoElectoral(),
                        requestDto.getIdCentroComputo(),
                        requestDto.getUbigeo(),
                        requestDto.getMesa())
                    .parallelStream()
                    .map(this::mapearResponse)
                    .sorted(Comparator
                        .comparing(ReporteAuditoriaDigitacionActaDto::getMesa)
                        .thenComparing(ReporteAuditoriaDigitacionActaDto::getPosicion))
                    .toList();

                reporteAuditoriaDigitacionActaDtoList.forEach(dto -> {
                    if (dto.getNumeroCopiaActa() == null) {
                        dto.setNumeroCopiaActa("");
                    }
                    if (dto.getDigitoChequeoEscrutinio() == null) {
                        dto.setDigitoChequeoEscrutinio("");
                    }
                });

                auditoriaDigitacionJasperDtoList = reporteAuditoriaDigitacionMapper.toAuditoriaDigitacionJasperDtoList(reporteAuditoriaDigitacionActaDtoList);
                nombreReporte = ConstantesComunes.REPORTE_AUDITORIA_DIGITACION;

            } else {
                parametros.put("p_reporte", ConstantesReportes.NAME_REPORTE_AUDITORIA_DIGITACION_PREFERENCIAL );
                reporteAuditoriaDigitacionActaPrefencialDtoList = actaRepository.auditoriaDigitacionPreferencial(
                        requestDto.getEsquema(),
                        requestDto.getIdEleccion(),
                        requestDto.getIdAmbitoElectoral(),
                        requestDto.getIdCentroComputo(),
                        requestDto.getUbigeo(),
                        requestDto.getMesa())
                    .parallelStream()
                    .map(this::mapearResponsePreferencial)
                    .sorted(Comparator
                        .comparing(ReporteAuditoriaDigitacionActaPrefencialDto::getMesa)
                        .thenComparing(ReporteAuditoriaDigitacionActaPrefencialDto::getPosicion))
                    .toList();

                auditoriaDigitacionJasperDtoList = reporteAuditoriaDigitacionMapper.toAuditoriaDigitacionPreferencialJasperDtoList(reporteAuditoriaDigitacionActaPrefencialDtoList);

                if (auditoriaDigitacionJasperDtoList != null && !auditoriaDigitacionJasperDtoList.isEmpty()) {
                    Integer cantidadAgrupacionPreferencial = null;
                    if ((Integer.parseInt(ConstantesComunes.COD_ELEC_PAR)) == (requestDto.getIdEleccion())) {
                        cantidadAgrupacionPreferencial = ConstantesComunes.CANTIDAD_VOTOS_PREFERENCIALES_PARLAMENTO;
                    } else {
                        UbiEleccionAgrupolRequestDto ubiEleccionAgrupolRequestDto = new UbiEleccionAgrupolRequestDto();
                        ubiEleccionAgrupolRequestDto.setIdEleccion(requestDto.getIdEleccion());
                        ubiEleccionAgrupolRequestDto.setEsquema(requestDto.getEsquema());
                        ubiEleccionAgrupolRequestDto.setUbigeo(requestDto.getUbigeo());
                        ubiEleccionAgrupolRequestDto.setMesa(requestDto.getMesa());

                        cantidadAgrupacionPreferencial = ubiEleccionAgrupolRepositoryCustom.obtenerCantidadAgrupacionPreferencial(ubiEleccionAgrupolRequestDto);
                    }

                    parametros.put("cantidadColumna", cantidadAgrupacionPreferencial);
                    if (cantidadAgrupacionPreferencial <= 9) {
                        nombreReporte = ConstantesComunes.REPORTE_AUDITORIA_DIGITACION_PREFERENCIAL_9;
                    } else if (cantidadAgrupacionPreferencial <= 18) {
                        nombreReporte = ConstantesComunes.REPORTE_AUDITORIA_DIGITACION_PREFERENCIAL_16;
                    } else if (cantidadAgrupacionPreferencial <= 27) {
                        nombreReporte = ConstantesComunes.REPORTE_AUDITORIA_DIGITACION_PREFERENCIAL_27;
                    } else {
                        nombreReporte = ConstantesComunes.REPORTE_AUDITORIA_DIGITACION_PREFERENCIAL;
                    }

                }
            }
        }
        this.logService.registrarLog(filtro.getUsuario(),
            Thread.currentThread().getStackTrace()[1].getMethodName(),
            this.getClass().getName(),
            TransactionalLogUtil.crearMensajeLog(ConstantesComunes.TITULO_ELECCION_VERIFICACION_DIGITACION_ACTA),
            "",
            filtro.getCodigoCentroComputo(),
            0, 1);

        return Funciones.generarReporte(this.getClass(), auditoriaDigitacionJasperDtoList, nombreReporte, parametros);
    }

    private ReporteAuditoriaDigitacionActaDto mapearResponse(Map<String, Object> reporte) {
        ReporteAuditoriaDigitacionActaDto response = new ReporteAuditoriaDigitacionActaDto();
        response.setPosicion((Short) reporte.get("n_posicion"));
        response.setDescripcionAgrupacionPolitica((String) reporte.get("c_descripcion"));
        response.setTotalVotos((String) reporte.get("c_total_votos"));
        response.setNombreCentroComputo((String) reporte.get("c_nombre_centro_computo"));
        response.setNumeroActa((Long) reporte.get("n_numero_acta"));
        response.setNumeroCopiaActa((String) (reporte.get("c_numero_copia_acta")!=null?reporte.get("c_numero_copia_acta"):""));
        response.setNombreAmbitoElectoral((String) reporte.get("c_nombre_ambito_electoral"));
        response.setCvas((String) reporte.get("c_cvas"));
        response.setDepartamento((String) reporte.get("c_departamento"));
        response.setProvincia((String) reporte.get("c_provincia"));
        response.setDistrito((String) reporte.get("c_distrito"));
        response.setMesa((String) reporte.get("c_mesa"));
        response.setDigitoChequeoEscrutinio((String) reporte.get("c_digito_chequeo_escrutinio"));
        response.setCodigoUbigeo((String) reporte.get("c_codigo_ubigeo"));
        return response;
    }

    private ReporteAuditoriaDigitacionActaCPRDto mapearResponseCPR(Map<String, Object> reporte) {
        ReporteAuditoriaDigitacionActaCPRDto response = new ReporteAuditoriaDigitacionActaCPRDto();
        response.setPosicion((Short) reporte.get("n_posicion"));
        response.setNumeroActa((Long) reporte.get("n_numero_acta"));
        response.setVotoOpcionSi((String) reporte.get("c_voto_opcion_si"));
        response.setVotoOpcionNulo((String) reporte.get("c_voto_opcion_nulo"));
        response.setVotoOpcionNo((String) reporte.get("c_voto_opcion_no"));
        response.setVotoOpcionImpugnados((String) reporte.get("c_voto_opcion_impugnados"));
        response.setVotoOpcionBlanco((String) reporte.get("c_voto_opcion_blanco"));
        response.setTotalVotos((String) reporte.get("c_total_votos"));
        response.setProvincia((String) reporte.get("c_provincia"));
        response.setNumeroCopiaActa((String) reporte.get("c_numero_copia_acta"));
        response.setNombresApellidos((String) reporte.get("c_nombres_apellidos"));
        response.setNombreCentroComputo((String) reporte.get("c_nombre_centro_computo"));
        response.setNombreAmbitoElectoral((String) reporte.get("c_nombre_ambito_electoral"));
        response.setMesa((String) reporte.get("c_mesa"));
        response.setDocumentoIdentidad((String) reporte.get("c_documento_identidad"));
        response.setDistrito((String) reporte.get("c_distrito"));
        response.setDigitoChequeoEscrutinio((String) reporte.get("c_digito_chequeo_escrutinio"));
        response.setDepartamento((String) reporte.get("c_departamento"));
        response.setCvas((String) reporte.get("c_cvas"));
        response.setCodigoUbigeo((String) reporte.get("c_codigo_ubigeo"));
        response.setCodigoCentroComputo((String) reporte.get("c_codigo_centro_computo"));
        response.setCodigoAmbitoElectoral((String) reporte.get("c_codigo_ambito_electoral"));
        response.setEstadoActa((String) reporte.get("c_estado_acta"));
        response.setEstadoActaDescripcion((String) reporte.get("c_estado_acta_descripcion"));
        response.setEstadoActaResolucion((String) reporte.get("c_estado_acta_resolucion"));
        response.setEstadoComputo((String) reporte.get("c_estado_computo"));
        return response;
    }


    private ReporteAuditoriaDigitacionActaPrefencialDto mapearResponsePreferencial(Map<String, Object> reporte) {
        ReporteAuditoriaDigitacionActaPrefencialDto response = new ReporteAuditoriaDigitacionActaPrefencialDto();
        response.setPosicion((Short) reporte.get("n_posicion"));
        response.setDescripcionAgrupacionPolitica((String) reporte.get("c_descripcion_agrupacion_politica"));
        response.setTotalVotos((String) reporte.get("c_total_votos"));
        response.setNombreCentroComputo((String) reporte.get("c_nombre_centro_computo"));
        response.setNumeroActa((Long) reporte.get("n_numero_acta"));
        response.setNumeroCopiaActa((String) (reporte.get("c_numero_copia_acta")!=null?reporte.get("c_numero_copia_acta"):""));
        response.setNombreAmbitoElectoral((String) reporte.get("c_nombre_ambito_electoral"));
        response.setCvas((String) reporte.get("c_cvas"));
        response.setDepartamento((String) reporte.get("c_departamento"));
        response.setProvincia((String) reporte.get("c_provincia"));
        response.setDistrito((String) reporte.get("c_distrito"));
        response.setMesa((String) reporte.get("c_mesa"));
        response.setDigitoChequeoEscrutinio((String) reporte.get("c_digito_chequeo_escrutinio"));
        response.setCodigoUbigeo((String) reporte.get("c_codigo_ubigeo"));
        response.setVotoPreferencial01((String) reporte.get("c_voto_pref_lista_1"));
        response.setVotoPreferencial02((String) reporte.get("C_voto_pref_lista_2"));
        response.setVotoPreferencial03((String) reporte.get("C_voto_pref_lista_3"));
        response.setVotoPreferencial04((String) reporte.get("C_voto_pref_lista_4"));
        response.setVotoPreferencial05((String) reporte.get("C_voto_pref_lista_5"));
        response.setVotoPreferencial06((String) reporte.get("C_voto_pref_lista_6"));
        response.setVotoPreferencial07((String) reporte.get("C_voto_pref_lista_7"));
        response.setVotoPreferencial08((String) reporte.get("C_voto_pref_lista_8"));
        response.setVotoPreferencial09((String) reporte.get("C_voto_pref_lista_9"));
        response.setVotoPreferencial10((String) reporte.get("C_voto_pref_lista_10"));
        response.setVotoPreferencial11((String) reporte.get("C_voto_pref_lista_11"));
        response.setVotoPreferencial12((String) reporte.get("C_voto_pref_lista_12"));
        response.setVotoPreferencial13((String) reporte.get("C_voto_pref_lista_13"));
        response.setVotoPreferencial14((String) reporte.get("C_voto_pref_lista_14"));
        response.setVotoPreferencial15((String) reporte.get("C_voto_pref_lista_15"));
        response.setVotoPreferencial16((String) reporte.get("C_voto_pref_lista_16"));
        response.setVotoPreferencial17((String) reporte.get("C_voto_pref_lista_17"));
        response.setVotoPreferencial18((String) reporte.get("C_voto_pref_lista_18"));
        response.setVotoPreferencial19((String) reporte.get("C_voto_pref_lista_19"));
        response.setVotoPreferencial20((String) reporte.get("C_voto_pref_lista_20"));
        response.setVotoPreferencial21((String) reporte.get("C_voto_pref_lista_21"));
        response.setVotoPreferencial22((String) reporte.get("C_voto_pref_lista_22"));
        response.setVotoPreferencial23((String) reporte.get("C_voto_pref_lista_23"));
        response.setVotoPreferencial24((String) reporte.get("C_voto_pref_lista_24"));
        response.setVotoPreferencial25((String) reporte.get("C_voto_pref_lista_25"));
        response.setVotoPreferencial26((String) reporte.get("C_voto_pref_lista_26"));
        response.setVotoPreferencial27((String) reporte.get("C_voto_pref_lista_27"));
        response.setVotoPreferencial28((String) reporte.get("C_voto_pref_lista_28"));
        response.setVotoPreferencial29((String) reporte.get("C_voto_pref_lista_29"));
        response.setVotoPreferencial30((String) reporte.get("C_voto_pref_lista_30"));
        response.setVotoPreferencial31((String) reporte.get("C_voto_pref_lista_31"));
        response.setVotoPreferencial32((String) reporte.get("C_voto_pref_lista_32"));
        response.setVotoPreferencial33((String) reporte.get("C_voto_pref_lista_33"));
        response.setVotoPreferencial34((String) reporte.get("C_voto_pref_lista_34"));
        response.setVotoPreferencial35((String) reporte.get("C_voto_pref_lista_35"));
        response.setVotoPreferencial36((String) reporte.get("C_voto_pref_lista_36"));

        return response;
    }

    private ReporteActaRequestDto mapearParametros(ReporteAuditoriaDigitacionActaRequestDto filtro) {
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
