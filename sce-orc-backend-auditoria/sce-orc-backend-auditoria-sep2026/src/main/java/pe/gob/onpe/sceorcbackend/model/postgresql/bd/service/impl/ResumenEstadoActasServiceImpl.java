package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.dto.DetalleEstadoActasOdpe;
import pe.gob.onpe.sceorcbackend.model.dto.EstadoActasOdpeReporteDto;
import pe.gob.onpe.sceorcbackend.model.dto.FiltroEstadoActasOdpeDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.ResumenEstadoActasService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class ResumenEstadoActasServiceImpl implements ResumenEstadoActasService {

    Logger logger = LoggerFactory.getLogger(ResumenEstadoActasServiceImpl.class);

    private final ActaRepository actasRepository;

    private final UtilSceService utilSceService;
    private final ITabLogService logService;

    public ResumenEstadoActasServiceImpl(ActaRepository actasRepository, UtilSceService utilSceService, ITabLogService logService) {
        this.actasRepository = actasRepository;
        this.utilSceService = utilSceService;
        this.logService = logService;
    }

    @Override
    public byte[] reporteResumenEstadoActas(FiltroEstadoActasOdpeDto filtro) {
        byte[] pdf = null;

        try {
            List<DetalleEstadoActasOdpe> actasLista = getListaEstadoActas(filtro);

            Map<String, Object> parametros = new java.util.HashMap<>();

            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON + "onpe.jpg");
            parametros.put("logo_onpe", imagen);
            parametros.put("sinvaloroficial", utilSceService.getSinValorOficial());
            parametros.put("version", utilSceService.getVersionSistema());
            parametros.put("usuario", filtro.getUsuario());
            parametros.put("tituloGeneral", filtro.getProceso());
            parametros.put("centroComputo", filtro.getCentroComputo());
            parametros.put("odpe", filtro.getOdpe());

            return Funciones.generarReporte(this.getClass(), actasLista, ConstantesComunes.REPORTE_RESUMEN_ESTADO_ACTAS, parametros);

        } catch (Exception e) {
            logger.error(e.getMessage());
            return pdf;
        }
    }

    @Override
    public EstadoActasOdpeReporteDto getResumenListaEstadoActas(FiltroEstadoActasOdpeDto filtro) {

        List<DetalleEstadoActasOdpe> detalle = getListaEstadoActas(filtro);
        this.logService.registrarLog(filtro.getUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                this.getClass().getSimpleName(), "Se consultó el Reporte de Resumen del Estado de Actas.", filtro.getCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);
        return EstadoActasOdpeReporteDto
                .builder()
                .detalleEstadoActasOdpe(detalle)
                .totalEstadoActasOdpe(getTotalEstadoActas(detalle))
                .build();

    }

    private List<DetalleEstadoActasOdpe> getListaEstadoActas(FiltroEstadoActasOdpeDto filtro) {
        TypedParameterValue idCentroComputo = new TypedParameterValue(StandardBasicTypes.INTEGER, filtro.getIdCentroComputo());

        List<Map<String, Object>> actasMap = actasRepository.resumenEstadoActas(filtro.getEsquema(), idCentroComputo);
        return actasMap
                .stream()
                .map( reporte -> {
                    Integer aProcesar = Integer.parseInt( reporte.get("n_actas_a_procesar").toString() );
                    Integer procesadas = Integer.parseInt( reporte.get("n_actas_procesadas").toString() );

                    return DetalleEstadoActasOdpe
                            .builder()
                            .num(Integer.parseInt(reporte.get("n_eleccion").toString()))
                            .descEleccion( reporte.get("c_nombre_eleccion").toString() )
                            .ahProcesar(aProcesar)
                            .porProcesar(aProcesar - procesadas)
                            .procesadas(procesadas)
                            .observadas(Integer.parseInt( reporte.get("n_actas_observadas").toString() ))
                            .resueltas(Integer.parseInt( reporte.get("n_actas_resueltas").toString() ))
                            .pendienteResol(Integer.parseInt( reporte.get("n_actas_pendiente_resolucion").toString() ))
                            .build();
                }).toList();
    }

    private List<DetalleEstadoActasOdpe> getTotalEstadoActas(List<DetalleEstadoActasOdpe> detalle) {
        DetalleEstadoActasOdpe totalEstadoActas = DetalleEstadoActasOdpe
                .builder()
                .descEleccion( "TOTAL" )
                .ahProcesar(detalle.stream().mapToInt(DetalleEstadoActasOdpe::getAhProcesar).sum())
                .porProcesar(detalle.stream().mapToInt(DetalleEstadoActasOdpe::getPorProcesar).sum())
                .procesadas(detalle.stream().mapToInt(DetalleEstadoActasOdpe::getProcesadas).sum())
                .observadas(detalle.stream().mapToInt(DetalleEstadoActasOdpe::getObservadas).sum())
                .resueltas(detalle.stream().mapToInt(DetalleEstadoActasOdpe::getResueltas).sum())
                .pendienteResol(detalle.stream().mapToInt(DetalleEstadoActasOdpe::getPendienteResol).sum())
                .build();

        List<DetalleEstadoActasOdpe> listaTotal = new ArrayList<>();
        listaTotal.add(totalEstadoActas);

        return listaTotal;
    }
}
