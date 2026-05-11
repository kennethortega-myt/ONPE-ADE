package pe.gob.onpe.scebackend.model.service.impl.reporte;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import org.springframework.stereotype.Service;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasEstadoMesaRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteMesasEstadoActaDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteMesasEstadoDigitacionDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteMesasEstadoMesaDto;
import pe.gob.onpe.scebackend.model.orc.repository.ReporteMesasEstadoMesaRepository;
import pe.gob.onpe.scebackend.model.service.IReporteMesasEstadoMesaService;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.TransactionalLogUtil;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesReportes;
import pe.gob.onpe.scebackend.utils.enums.TipoReporteEnum;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Log
public class ReporteMesasEstadoMesaServiceImpl implements IReporteMesasEstadoMesaService {

	private static final String NOMBRE_PROCESO_ELECTORAL = "c_nombre_proceso_electoral";
	private static final String ID_ELECCION = "n_eleccion";
	private static final String NOMBRE_TIPO_ELECCION = "c_nombre_tipo_eleccion";
	private static final String CODIGO_CENTRO_COMPUTO = "c_codigo_centro_computo";
	private static final String NOMBRE_CENTRO_COMPUTO = "c_nombre_centro_computo";
	private static final String CODIGO_AMBITO_ELECTORAL = "c_codigo_ambito_electoral";
	private static final String NOMBRE_AMBITO_ELECTORAL = "c_nombre_ambito_electoral";
	private static final String NOMBRE_DEPARTAMENTO = "c_nombre_departamento";
    private static final String MESAS_DEP = "n_mesas_dep";
    private static final String ELECTORES_DEP = "n_electores_dep";
    private static final String NOMBRE_PROVINCIA = "c_nombre_provincia";
    private static final String MESAS_PRO = "n_mesas_pro";
    private static final String ELECTORES_PRO = "n_electores_pro";
    private static final String NOMBRE_DISTRITO = "c_nombre_distrito";
    private static final String MESAS_DIS = "n_mesas_dis";
    private static final String ELECTORES_DIS = "n_electores_dis";
    private static final String CODIGO_LOCAL = "c_codigo_local";
    private static final String NOMBRE_LOCAL = "c_nombre_local";
    private static final String DIRECCION = "c_direccion";
    private static final String MESA = "c_mesa";
    private static final String MESAS_LOCAL = "n_mesas_local";
    private static final String ELECTORES_LOCAL = "n_electores_local";
    private static final String ESTADO_MESA_DIS = "c_estado_mesa_dis";
    private static final String ID_ESTADO_ACTA = "n_estado_acta";
    
    private final ReporteMesasEstadoMesaRepository reporteMesasEstadoMesaRepository;
    private final ITabLogTransaccionalService logService;
    private final UtilSceService utilSceService;

    @Override
    public byte[] reporteMesasEstadoMesa(ReporteMesasEstadoMesaRequestDto filtro) {
        String mensajeLog = "";
        try {
            List<?> lista = null;
            Map<String, Object> parametrosReporte = new java.util.HashMap<>();
            String nombreReporte = "";
            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe

            parametrosReporte.put("url_imagen", imagen);
            parametrosReporte.put("sinValorOficial", utilSceService.getSinValorOficial(filtro.getIdProceso()));
            parametrosReporte.put("version", utilSceService.getVersionSistema());
            parametrosReporte.put("usuario", filtro.getUsuario());
            parametrosReporte.put("estadoMesa", filtro.getIdEstadoMesa() == null ? "TODOS LOS ESTADOS" : filtro.getEstadoMesa());

            if(TipoReporteEnum.MESAS_POR_ESTADO_DE_MESA.getValue().equals(filtro.getTipoReporte())) {
                lista = procesarReporteMesa(filtro, parametrosReporte);
                nombreReporte = filtro.isPorDistrito()
                        ? ConstantesComunes.REPORTE_MESAS_ESTADO_MESA
                        : ConstantesComunes.REPORTE_MESAS_ESTADO_MESA_X_ESTADO;
                parametrosReporte.put("tituloReporte", ConstantesComunes.TITULO_REPORTE_MESAS_ESTADO_MESA);
                mensajeLog = TransactionalLogUtil.crearMensajeLog(ConstantesComunes.TITULO_REPORTE_MESAS_ESTADO_MESA);
            } else if (TipoReporteEnum.MESAS_POR_ESTADO_DE_ACTA.getValue().equals(filtro.getTipoReporte())) {
                lista = procesarReporteActa(filtro, parametrosReporte);
                nombreReporte = filtro.isPorDistrito()
                        ? ConstantesComunes.REPORTE_MESAS_ESTADO_ACTA
                        : ConstantesComunes.REPORTE_MESAS_ESTADO_ACTA_X_ESTADO;
                parametrosReporte.put("tituloReporte", ConstantesComunes.TITULO_REPORTE_MESAS_ESTADO_ACTA);
                mensajeLog = TransactionalLogUtil.crearMensajeLog(ConstantesComunes.TITULO_REPORTE_MESAS_ESTADO_ACTA);

            } else if (TipoReporteEnum.MESAS_POR_ESTADO_DE_DIGITACION.getValue().equals(filtro.getTipoReporte())) {
                lista = procesarReporteDigitacion(filtro, parametrosReporte);
                nombreReporte = filtro.isPorDistrito()
                        ? ConstantesComunes.REPORTE_MESAS_ESTADO_DIGITACION
                        : ConstantesComunes.REPORTE_MESAS_ESTADO_DIGITACION_X_ESTADO;
                parametrosReporte.put("tituloReporte", ConstantesComunes.TITULO_REPORTE_MESAS_ESTADO_DIGITACION);
                mensajeLog = TransactionalLogUtil.crearMensajeLog(ConstantesComunes.TITULO_REPORTE_MESAS_ESTADO_DIGITACION);
            }

            this.logService.registrarLog(filtro.getUsuario(),
                    Thread.currentThread().getStackTrace()[1].getMethodName(),
                    this.getClass().getName(),
                    mensajeLog,
                    "",
                    filtro.getCodigoCentroComputo(),
                    0, 1);
            
          return Funciones.generarReporte(this.getClass(),lista,nombreReporte,parametrosReporte);

        } catch (Exception e) {
            e.getStackTrace();
            log.warning(e.getMessage());
            return new byte[0];
        }
    }

    private List<ReporteMesasEstadoMesaDto> procesarReporteMesa(ReporteMesasEstadoMesaRequestDto filtro, Map<String, Object> params) {
        List<ReporteMesasEstadoMesaDto> lista = this.reporteMesasEstadoMesaRepository
                .listarReporteMesasEstado(
                        filtro.getEsquema(),
                        filtro.getIdEleccion(),
                        filtro.getAmbitoElectoral(),
                        filtro.getCentroComputo(),
                        filtro.getUbigeoNivelUno(),
                        filtro.getUbigeoNivelDos(),
                        filtro.getUbigeoNivelTres(),
                        filtro.getCodigoEstadoMesa()
                )
                .stream()
                .map(this::llenarDatosMesasEstadoMesa)
                .toList();

        if (!filtro.isPorDistrito()) {
            lista = lista.stream().sorted(Comparator.comparing(ReporteMesasEstadoMesaDto::getEstadoMesaDis)).toList();
            long totalMesas = lista.stream()
                    .collect(Collectors.toMap(ReporteMesasEstadoMesaDto::getCodigoLocal, ReporteMesasEstadoMesaDto::getMesasLocal, (m1, m2) -> m1))
                    .values().stream()
                    .mapToLong(Integer::longValue)
                    .sum();

            long totalElectores = lista.stream()
                    .collect(Collectors.toMap(ReporteMesasEstadoMesaDto::getCodigoLocal, ReporteMesasEstadoMesaDto::getElectoresLocal, (e1, e2) -> e1))
                    .values().stream()
                    .mapToLong(Integer::longValue)
                    .sum();

            params.put("sumMesasTotal", totalMesas);
            params.put("sumElectoresTotal", totalElectores);
        }

        mapearCamposCabeceraReporteMesa(filtro, params, lista);
        return lista;
    }

    private List<ReporteMesasEstadoActaDto> procesarReporteActa(ReporteMesasEstadoMesaRequestDto filtro, Map<String, Object> params) {
        List<ReporteMesasEstadoActaDto> lista = this.reporteMesasEstadoMesaRepository
                .listarReporteMesasEstadoActa(
                        filtro.getEsquema(),
                        filtro.getIdEleccion(),
                        filtro.getAmbitoElectoral(),
                        filtro.getCentroComputo(),
                        filtro.getUbigeoNivelUno(),
                        filtro.getUbigeoNivelDos(),
                        filtro.getUbigeoNivelTres(),
                        filtro.getIdEstadoMesa() == null ? null : Integer.parseInt(filtro.getIdEstadoMesa())
                )
                .stream()
                .map(this::llenarDatosMesasEstadoActa)
                .sorted(Comparator.comparing(ReporteMesasEstadoActaDto::getIdEstadoActa))
                .toList();

        if (!filtro.isPorDistrito()) {
            lista = lista.stream().sorted(Comparator.comparing(ReporteMesasEstadoActaDto::getIdEstadoActa)).toList();
        }
        mapearCamposCabeceraReporteActa(filtro, params, lista);
        return lista;
    }

    private List<ReporteMesasEstadoDigitacionDto> procesarReporteDigitacion(ReporteMesasEstadoMesaRequestDto filtro, Map<String, Object> params) {
        List<ReporteMesasEstadoDigitacionDto> lista = new ArrayList<>(getListaDigitacion(filtro));

        if (!filtro.isPorDistrito()) {
            lista = lista.stream().sorted(Comparator.comparing(ReporteMesasEstadoDigitacionDto::getIdEstadoActa)).toList();
            long totalMesas = lista.stream()
                    .collect(Collectors.toMap(ReporteMesasEstadoDigitacionDto::getCodigoLocal, ReporteMesasEstadoDigitacionDto::getTotalMesasLocal, (m1, m2) -> m1))
                    .values().stream()
                    .mapToLong(Long::longValue)
                    .sum();

            long totalElectores = lista.stream()
                    .collect(Collectors.toMap(ReporteMesasEstadoDigitacionDto::getCodigoLocal, ReporteMesasEstadoDigitacionDto::getTotalElectoresLocal, (e1, e2) -> e1))
                    .values().stream()
                    .mapToLong(Long::longValue)
                    .sum();

            params.put("sumMesasTotal", totalMesas);
            params.put("sumElectoresTotal", totalElectores);
        }

        mapearCamposCabeceraReporteDigitacion(filtro, params, lista);
        return lista;
    }

    private ReporteMesasEstadoMesaDto llenarDatosMesasEstadoMesa(Map<String, Object> mapMesa) {
        ReporteMesasEstadoMesaDto reporte = new ReporteMesasEstadoMesaDto();
        reporte.setNombreProcesoElectoral((String) mapMesa.get(NOMBRE_PROCESO_ELECTORAL));
        reporte.setIdEleccion((Integer) mapMesa.get(ID_ELECCION));

        reporte.setNombreTipoEleccion((String) mapMesa.get(NOMBRE_TIPO_ELECCION));
        reporte.setCodigoCentroComputo((String) mapMesa.get(CODIGO_CENTRO_COMPUTO));
        reporte.setNombreCentroComputo((String) mapMesa.get(NOMBRE_CENTRO_COMPUTO));

        reporte.setCodigoAmbitoElectoral((String) mapMesa.get(CODIGO_AMBITO_ELECTORAL));
        reporte.setNombreAmbitoElectoral((String) mapMesa.get(NOMBRE_AMBITO_ELECTORAL));

        reporte.setNombreDepartamento((String) mapMesa.get(NOMBRE_DEPARTAMENTO));
        reporte.setMesasDep( ((Long) mapMesa.get(MESAS_DEP)).intValue());
        reporte.setElectoresDep(((Long) mapMesa.get(ELECTORES_DEP)).intValue());

        reporte.setNombreProvincia((String) mapMesa.get(NOMBRE_PROVINCIA));
        reporte.setMesasPro(((Long) mapMesa.get(MESAS_PRO)).intValue());
        reporte.setElectoresPro(((Long) mapMesa.get(ELECTORES_PRO)).intValue());

        reporte.setNombreDistrito((String) mapMesa.get(NOMBRE_DISTRITO));
        reporte.setMesasDis(((Long) mapMesa.get(MESAS_DIS)).intValue());
        reporte.setElectoresDis(((Long) mapMesa.get(ELECTORES_DIS)).intValue());

        reporte.setCodigoLocal((String) mapMesa.get(CODIGO_LOCAL));
        reporte.setNombreLocal((String) mapMesa.get(NOMBRE_LOCAL));

        reporte.setDireccion((String) mapMesa.get(DIRECCION));
        reporte.setMesa((String) mapMesa.get(MESA));
        reporte.setMesasLocal(((Long) mapMesa.get(MESAS_LOCAL)).intValue());
        reporte.setElectoresLocal(((Long) mapMesa.get(ELECTORES_LOCAL)).intValue());

        reporte.setEstadoMesaDis((String) mapMesa.get(ESTADO_MESA_DIS));
        return reporte;
    }

    private ReporteMesasEstadoActaDto llenarDatosMesasEstadoActa(Map<String, Object> mapMesa) {
        ReporteMesasEstadoActaDto reporte = new ReporteMesasEstadoActaDto();
        reporte.setNombreProcesoElectoral((String) mapMesa.get(NOMBRE_PROCESO_ELECTORAL));
        reporte.setIdEleccion((Integer) mapMesa.get(ID_ELECCION));

        reporte.setNombreTipoEleccion((String) mapMesa.get(NOMBRE_TIPO_ELECCION));
        reporte.setCodigoCentroComputo((String) mapMesa.get(CODIGO_CENTRO_COMPUTO));
        reporte.setNombreCentroComputo((String) mapMesa.get(NOMBRE_CENTRO_COMPUTO));

        reporte.setCodigoAmbitoElectoral((String) mapMesa.get(CODIGO_AMBITO_ELECTORAL));
        reporte.setNombreAmbitoElectoral((String) mapMesa.get(NOMBRE_AMBITO_ELECTORAL));

        reporte.setNombreUbigeoNivelUno((String) mapMesa.get(NOMBRE_DEPARTAMENTO));
        reporte.setNombreUbigeoNivelDos((String) mapMesa.get(NOMBRE_PROVINCIA));
        reporte.setNombreUbigeoNivelTres((String) mapMesa.get(NOMBRE_DISTRITO));

        reporte.setElectoresHabiles(Long.valueOf((Integer)mapMesa.get("n_electores_habiles")));
        reporte.setCodigoLocal((String) mapMesa.get(CODIGO_LOCAL));
        reporte.setNombreLocal((String) mapMesa.get(NOMBRE_LOCAL));
        reporte.setDireccion((String) mapMesa.get(DIRECCION));
        reporte.setIdActa((Long) mapMesa.get("n_acta"));
        reporte.setIdMesa((Integer) mapMesa.get("n_mesa"));
        reporte.setMesa(String.format("%06d", reporte.getIdMesa()));
        reporte.setIdEstadoActa((Integer) mapMesa.get(ID_ESTADO_ACTA));

        return reporte;
    }

    private List<ReporteMesasEstadoDigitacionDto> getListaDigitacion(ReporteMesasEstadoMesaRequestDto filtro) {
    	List<ReporteMesasEstadoDigitacionDto> listaDigitacion = this.reporteMesasEstadoMesaRepository
        		.listarReporteMesasEstadoDigitacion(filtro.getEsquema(),
        				filtro.getIdEleccion(),
        				filtro.getAmbitoElectoral(),
        				filtro.getCentroComputo(),
        				filtro.getUbigeoNivelUno(),
        				filtro.getUbigeoNivelDos(),
        				filtro.getUbigeoNivelTres(),
        				filtro.getIdEstadoMesa() == null ? null : Integer.parseInt(filtro.getIdEstadoMesa()))
        		.stream()
        		.map(this::llenarDatosMesasEstadoDigitacion).toList();
    	
    	Map<String, Long> sumMesasByGroupLocal = listaDigitacion.stream().collect(
                Collectors.groupingBy(ReporteMesasEstadoDigitacionDto::getCodigoLocal, Collectors.summingLong(ReporteMesasEstadoDigitacionDto::getTotalMesasMesa))
            );
        
        Map<String, Long> sumElectoresByGroupLocal = listaDigitacion.stream().collect(
                Collectors.groupingBy(ReporteMesasEstadoDigitacionDto::getCodigoLocal, Collectors.summingLong(ReporteMesasEstadoDigitacionDto::getTotalElectoresMesa))
            );
        
        return listaDigitacion.stream()
					        .map(dig -> {
					        	dig.setTotalMesasLocal(sumMesasByGroupLocal.get(dig.getCodigoLocal()));
					        	dig.setTotalElectoresLocal(sumElectoresByGroupLocal.get(dig.getCodigoLocal()));
					        	return dig;
					        }).toList();
    }
    
    private ReporteMesasEstadoDigitacionDto llenarDatosMesasEstadoDigitacion(Map<String, Object> mapMesa) {
        ReporteMesasEstadoDigitacionDto reporte = new ReporteMesasEstadoDigitacionDto();
        reporte.setNombreProcesoElectoral((String) mapMesa.get(NOMBRE_PROCESO_ELECTORAL));
        reporte.setIdEleccion((Integer) mapMesa.get(ID_ELECCION));

        reporte.setNombreTipoEleccion((String) mapMesa.get(NOMBRE_TIPO_ELECCION));
        reporte.setCodigoCentroComputo((String) mapMesa.get(CODIGO_CENTRO_COMPUTO));
        reporte.setNombreCentroComputo((String) mapMesa.get(NOMBRE_CENTRO_COMPUTO));

        reporte.setCodigoAmbitoElectoral((String) mapMesa.get(CODIGO_AMBITO_ELECTORAL));
        reporte.setNombreAmbitoElectoral((String) mapMesa.get(NOMBRE_AMBITO_ELECTORAL));

        reporte.setNombreUbigeoNivelUno((String) mapMesa.get(NOMBRE_DEPARTAMENTO));
        reporte.setTotalMesasUbigeoNivelUno((Long) mapMesa.get(MESAS_DEP));
        reporte.setTotalElectoresUbigeoNivelUno((Long) mapMesa.get(ELECTORES_DEP));

        reporte.setNombreUbigeoNivelDos((String) mapMesa.get(NOMBRE_PROVINCIA));
        reporte.setTotalMesasUbigeoNivelDos((Long) mapMesa.get(MESAS_PRO));
        reporte.setTotalElectoresUbigeoNivelDos((Long) mapMesa.get(ELECTORES_PRO));

        reporte.setNombreUbigeoNivelTres((String) mapMesa.get(NOMBRE_DISTRITO));
        reporte.setTotalMesasUbigeoNivelTres((Long) mapMesa.get(MESAS_DIS));
        reporte.setTotalElectoresUbigeoNivelTres((Long) mapMesa.get(ELECTORES_DIS));

        reporte.setCodigoLocal((String) mapMesa.get(CODIGO_LOCAL));
        reporte.setNombreLocal((String) mapMesa.get(NOMBRE_LOCAL));

        reporte.setDireccion((String) mapMesa.get(DIRECCION));
        reporte.setMesa((String) mapMesa.get(MESA));
        reporte.setTotalMesasMesa((Long) mapMesa.get(MESAS_LOCAL));
        reporte.setTotalElectoresMesa((Long) mapMesa.get(ELECTORES_LOCAL));

        reporte.setIdEstadoActa((Integer) mapMesa.get(ID_ESTADO_ACTA));
        reporte.setNombreEstadoActa((String) mapMesa.get("c_estado_digi"));
        return reporte;
    }

    

    private void mapearCamposCabeceraReporteMesa(
            ReporteMesasEstadoMesaRequestDto filtro, Map<String, Object> parametrosReporte,List<? extends ReporteMesasEstadoMesaDto> lista) {

        if(filtro.getAmbitoElectoral() == null) {
            parametrosReporte.put("descOdpe", filtro.getCodigoAmbitoElectoral() + " - " + ConstantesComunes.CC_NACION_DESCRIPCION);
        } else {
            parametrosReporte.put("descOdpe", lista.get(0).getCodigoAmbitoElectoral() + " - " + lista.get(0).getNombreAmbitoElectoral() );
        }
        if(filtro.getCentroComputo() == null) {
            parametrosReporte.put("descCompu", filtro.getCodigoCentroComputo() + " - " + ConstantesComunes.CC_NACION_DESCRIPCION);
        } else {
            parametrosReporte.put("descCompu", lista.get(0).getCodigoCentroComputo() + " - " + lista.get(0).getNombreCentroComputo() );
        }

        parametrosReporte.put("tituloPrincipal", lista.get(0).getNombreProcesoElectoral());
        parametrosReporte.put("tituloEleccionCompleto", lista.get(0).getNombreTipoEleccion());
        parametrosReporte.put("reporte", ConstantesReportes.NAME_REPORTE_MESA_ESTADO_MESA);
    }

    private void mapearCamposCabeceraReporteActa(
            ReporteMesasEstadoMesaRequestDto filtro, Map<String, Object> parametrosReporte,List<? extends ReporteMesasEstadoActaDto> lista) {

        if(filtro.getAmbitoElectoral() == null) {
            parametrosReporte.put("descOdpe", filtro.getCodigoAmbitoElectoral() + " - " + ConstantesComunes.CC_NACION_DESCRIPCION);
        } else {
            parametrosReporte.put("descOdpe", lista.get(0).getCodigoAmbitoElectoral() + " - " + lista.get(0).getNombreAmbitoElectoral() );
        }
        if(filtro.getCentroComputo() == null) {
            parametrosReporte.put("descCompu", filtro.getCodigoCentroComputo() + " - " + ConstantesComunes.CC_NACION_DESCRIPCION);
        } else {
            parametrosReporte.put("descCompu", lista.get(0).getCodigoCentroComputo() + " - " + lista.get(0).getNombreCentroComputo() );
        }

        parametrosReporte.put("tituloPrincipal", lista.get(0).getNombreProcesoElectoral());
        parametrosReporte.put("tituloSecundario", "LISTADO DE MESAS POR ESTADO DE MESA");
        parametrosReporte.put("tituloEleccionCompleto", lista.get(0).getNombreTipoEleccion());

        parametrosReporte.put("servidor", ConstantesReportes.NOMBRE_SERVIDOR_BD);
        parametrosReporte.put("estacion", "FALTA PONER DATOS");
        parametrosReporte.put("reporte", ConstantesReportes.NAME_REPORTE_MESA_ESTADO_ACTA);

    }

    private void mapearCamposCabeceraReporteDigitacion(
            ReporteMesasEstadoMesaRequestDto filtro, Map<String, Object> parametrosReporte,List<? extends ReporteMesasEstadoDigitacionDto> lista) {

        if(filtro.getAmbitoElectoral() == null) {
            parametrosReporte.put("descOdpe", filtro.getCodigoAmbitoElectoral() + " - " + ConstantesComunes.CC_NACION_DESCRIPCION);
        } else {
            parametrosReporte.put("descOdpe", lista.get(0).getCodigoAmbitoElectoral() + " - " + lista.get(0).getNombreAmbitoElectoral() );
        }
        if(filtro.getCentroComputo() == null) {
            parametrosReporte.put("descCompu", filtro.getCodigoCentroComputo() + " - " + ConstantesComunes.CC_NACION_DESCRIPCION);
        } else {
            parametrosReporte.put("descCompu", lista.get(0).getCodigoCentroComputo() + " - " + lista.get(0).getNombreCentroComputo() );
        }

        parametrosReporte.put("tituloPrincipal", lista.get(0).getNombreProcesoElectoral());

        parametrosReporte.put("tituloSecundario", "LISTADO DE MESAS POR ESTADO DE MESA");
        parametrosReporte.put("tituloEleccionCompleto", lista.get(0).getNombreTipoEleccion());
        parametrosReporte.put("servidor", "FALTA PONER DATOS");
        parametrosReporte.put("estacion", "FALTA PONER DATOS");
        parametrosReporte.put("reporte", ConstantesReportes.NAME_REPORTE_MESA_ESTADO_DIGITACION);

    }

}
