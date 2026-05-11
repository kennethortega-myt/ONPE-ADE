package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.dto.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.CentroComputo;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Eleccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ProcesoElectoral;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.CentroComputoRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.EleccionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ProcesoElectoralRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IAvanceEstadoActaService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.DateUtil;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.net.InetAddress;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AvanceEstadoActaServiceImpl implements IAvanceEstadoActaService {

    @Autowired
    private ActaRepository reportesRepository;

    @Autowired
    private CentroComputoRepository centroComputoRepository;

    @Autowired
    private EleccionRepository eleccionRepository;

    @Autowired
    private ProcesoElectoralRepository procesoElectoralRepository;
    @Autowired
    private UtilSceService utilSceService;
    @Autowired
    private ITabLogService logService;

    Logger logger = LoggerFactory.getLogger(AvanceEstadoActaServiceImpl.class);

    @Override
    public AvanceEstadoActaReporteDto getAvanceEstadoActa(FiltroAvanceEstadoActaDto filtro) {
        return this.getAvanceEstadoActaReporte(filtro);
    }

    @Override
    public byte[] getReporteAvanceEstadoActa(FiltroAvanceEstadoActaDto filtro) {
        try{

            AvanceEstadoActaReporteDto reporte = this.getAvanceEstadoActaReporte(filtro);
            List<AvanceEstadoActaDetalleDto> lista = reporte.getDetalleAvanceEstadoMesa();

            Map<String, Object> parametros = new HashMap<>();

            parametros.put("PROCESO", reporte.getEncabezado().getNombreProceso());
            parametros.put("CENTRO_COMPUTO", reporte.getEncabezado().getCodigoCc() + " - " + reporte.getEncabezado().getNombreCc());
            parametros.put("ELECCION",   reporte.getEncabezado().getNombreEleccion());
            parametros.put("ESTADO",   filtro.getEstadoDescripcion());
            
            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON + "onpe.jpg");
            parametros.put("logo_onpe", imagen);
            parametros.put("sinvaloroficial", utilSceService.getSinValorOficial());
            parametros.put("version", utilSceService.getVersionSistema());
            parametros.put("servidor", InetAddress.getLocalHost().getHostName());
            parametros.put("usuario", filtro.getUsuario());

            String nombreReporte = ConstantesComunes.AVANCE_ESTADO_ACTAS_REPORT_JRXML;

            return Funciones.generarReporte(this.getClass(), lista, nombreReporte, parametros);

        }catch(Exception e) {
            logger.error("excepcion", e);
            return null;
        }
    }

    
    private AvanceEstadoActaReporteDto getAvanceEstadoActaReporte(FiltroAvanceEstadoActaDto filtro) {
        AvanceEstadoActaReporteDto val = null;

        EncabezadoFiltroAvanceEstadoActaDto encabezado = this.getEncabezado(filtro);
        List<AvanceEstadoActaDetalleDto> detalles = reportesRepository.avanceEstadoActa(filtro.getSchema(), filtro.getIdEleccion(), filtro.getIdCentroComputo(), filtro.getEstado(), filtro.getUsuario())
                .stream().map(reporte -> {
                    AvanceEstadoActaDetalleDto estadoActa = new AvanceEstadoActaDetalleDto();
                    estadoActa.setIdCentroComputo((Integer) reporte.get("n_centro_computo"));
                    estadoActa.setCodigoCc((String) reporte.get("c_codigo_centro_computo"));
                    estadoActa.setNombreCc((String) reporte.get("c_nombre_centro_computo"));
                    estadoActa.setFechaUltModificacion(DateUtil.getDateString((Date) reporte.get("d_fecha_ultima_modificacion"), SceConstantes.PATTERN_DD_MM_YYYY_HH_MM_SS_DASH));
                    Long mesasHabiles = (Long) reporte.get("n_mesas_habiles");
                    estadoActa.setMesasHabiles(mesasHabiles);
                    Long ingresadas = (Long) reporte.get("n_actas_ingresadas");
                    estadoActa.setActasIngresadas(ingresadas);
                    Long procesadas = (Long) reporte.get("n_actas_procesadas");
                    estadoActa.setActasProcesadas(procesadas);
                    Long contabilizadas = (Long) reporte.get("n_actas_contabilizadas");
                    estadoActa.setActasContabilizadas(contabilizadas);
                    Long observadas = (Long) reporte.get("n_actas_observadas");
                    estadoActa.setActasPendientesResolverJEE(observadas);
                    
                    Double procesadasPor = (procesadas.doubleValue() / mesasHabiles.doubleValue()) * 100;
                    estadoActa.setActasProcesadasPorcen(procesadasPor);
                    
                    Double contabilizadaPor = (contabilizadas.doubleValue() / mesasHabiles.doubleValue()) * 100;
                    estadoActa.setActasContabilizadasPorcen(contabilizadaPor);
                    
                    Double observadaPor = (observadas.doubleValue() / mesasHabiles.doubleValue()) * 100;
                    estadoActa.setActasPendientesResolverJEEPorcen(observadaPor);
                    
                    estadoActa.setFlagProcesadasCompletas((Integer) reporte.get("n_flag_procesadas_completas"));
                    estadoActa.setFlagContabilizadasCompletas((Integer) reporte.get("n_flag_contabilizadas_completas"));
                    estadoActa.setTotalActas(estadoActa.getActasIngresadas()+estadoActa.getActasContabilizadas()+estadoActa.getActasPendientesResolverJEE()+estadoActa.getActasProcesadas());
                    return estadoActa;
                }).sorted(Comparator
                        .comparing(AvanceEstadoActaDetalleDto::getCodigoCc)
                        .thenComparing(AvanceEstadoActaDetalleDto::getNombreCc))
                .collect(Collectors.toList());

        Long sumaMesasHabiles = detalles.parallelStream().map(d -> d.getMesasHabiles()).reduce( 0L, Long::sum);
        Long sumaIngresadas = detalles.parallelStream().map(d -> d.getActasIngresadas()).reduce( 0L, Long::sum);
        Long sumaProcesadas = detalles.parallelStream().map(d -> d.getActasProcesadas()).reduce( 0L, Long::sum);
        Long sumContabilizadas = detalles.parallelStream().map(d -> d.getActasContabilizadas()).reduce( 0L, Long::sum);
        Long sumaObservadas = detalles.parallelStream().map(d -> d.getActasPendientesResolverJEE()).reduce( 0L, Long::sum);

        Double sumaProcesadasPor = 0d;
        Double sumaContabilizadaPor = 0d;
        Double sumaObservadaPor = 0d;
        
        if(sumaMesasHabiles != 0) {
        	sumaProcesadasPor = (sumaProcesadas.doubleValue() / sumaMesasHabiles) * 100 ;
            sumaContabilizadaPor = (sumContabilizadas.doubleValue() / sumaMesasHabiles) * 100 ;
            sumaObservadaPor = (sumaObservadas.doubleValue() / sumaMesasHabiles) * 100 ;
        } 
        
        AvanceEstadoActaDetalleDto total = new AvanceEstadoActaDetalleDto();
        total.setMesasHabiles(sumaMesasHabiles);
        total.setActasIngresadas(sumaIngresadas);
        total.setActasProcesadas(sumaProcesadas);
        total.setActasContabilizadas(sumContabilizadas);
        total.setActasPendientesResolverJEE(sumaObservadas);
        total.setActasProcesadasPorcen(sumaProcesadasPor);
        total.setActasContabilizadasPorcen(sumaContabilizadaPor);        
        total.setActasPendientesResolverJEEPorcen(sumaObservadaPor);
        
        List<AvanceEstadoActaDetalleDto> totales = Arrays.asList(total);
        
        AvanceEstadoActaResumenDto resumen = new AvanceEstadoActaResumenDto();
        
        resumen.setProcesadas(detalles.stream().mapToLong(AvanceEstadoActaDetalleDto::getFlagProcesadasCompletas).sum());
        resumen.setContabilizadas(detalles.stream().mapToLong(AvanceEstadoActaDetalleDto::getFlagContabilizadasCompletas).sum());
        resumen.setPorProcesar(detalles.size() - ( resumen.getProcesadas() + resumen.getContabilizadas() ));

        val = new AvanceEstadoActaReporteDto();
        val.setEncabezado(encabezado);
        val.setResumen(resumen);
        val.setDetalleAvanceEstadoMesa(detalles);
        val.setTotalAvanceEstadoMesa(totales);

    	this.logService.registrarLog(filtro.getUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(),
    			this.getClass().getSimpleName(), "Se consultó el Reporte de Avance Estado de Actas.", filtro.getCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);
        
        return val;
    }

    @Transactional
    public EncabezadoFiltroAvanceEstadoActaDto getEncabezado(FiltroAvanceEstadoActaDto filtro) {

        Optional<CentroComputo> cc = this.centroComputoRepository.findById(Long.valueOf(filtro.getIdCentroComputo()));
        Optional<Eleccion> eleccion = this.eleccionRepository.findById(Long.valueOf(filtro.getIdEleccion()));
        Optional<ProcesoElectoral> proceso = this.procesoElectoralRepository.findById(Long.valueOf(filtro.getIdProceso()));

        EncabezadoFiltroAvanceEstadoActaDto encabezado = new EncabezadoFiltroAvanceEstadoActaDto();

        if(cc.isPresent()) {
            encabezado.setCodigoCc(cc.get().getCodigo());
            encabezado.setNombreCc(cc.get().getNombre());
        }

        if(eleccion.isPresent()) {
            encabezado.setCodigoEleccion(String.valueOf(eleccion.get().getId()));
            encabezado.setNombreEleccion(eleccion.get().getNombre());
        }

        if(proceso.isPresent()) {
            encabezado.setNombreProceso(proceso.get().getNombre());
        }


        return encabezado;

    }


}
