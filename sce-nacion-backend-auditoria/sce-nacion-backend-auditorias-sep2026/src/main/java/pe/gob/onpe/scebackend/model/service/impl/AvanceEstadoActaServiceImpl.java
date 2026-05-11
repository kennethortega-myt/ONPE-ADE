package pe.gob.onpe.scebackend.model.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.scebackend.model.dto.*;
import pe.gob.onpe.scebackend.model.orc.entities.CentroComputo;
import pe.gob.onpe.scebackend.model.orc.entities.Eleccion;
import pe.gob.onpe.scebackend.model.orc.entities.ProcesoElectoral;
import pe.gob.onpe.scebackend.model.orc.repository.ActaRepository;
import pe.gob.onpe.scebackend.model.orc.repository.CentroComputoRepository;
import pe.gob.onpe.scebackend.model.orc.repository.EleccionRepository;
import pe.gob.onpe.scebackend.model.orc.repository.ProcesoElectoralRepository;
import pe.gob.onpe.scebackend.model.service.IAvanceEstadoActaService;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;
import pe.gob.onpe.scebackend.utils.DateUtil;
import pe.gob.onpe.scebackend.utils.SceConstantes;

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
    private ITabLogTransaccionalService logService;
    
    @Autowired
    private UtilSceService utilSceService;

    Logger logger = LoggerFactory.getLogger(AvanceEstadoActaServiceImpl.class);

    @Override
    public AvanceEstadoActaReporteDto getAvanceEstadoActa(FiltroAvanceEstadoActaDto filtro) {
        Optional<CentroComputo> cc = this.centroComputoRepository.findById(Long.valueOf(filtro.getIdCentroComputo()));
        return this.getAvanceEstadoActaReporte(filtro, cc.isPresent() ? cc.get().getCodigo() : "");
    }

    @Override
    public byte[] getReporteAvanceEstadoActa(FiltroAvanceEstadoActaDto filtro) {
        try{

            Optional<CentroComputo> cc = this.centroComputoRepository.findById(Long.valueOf(filtro.getIdCentroComputo()));
            AvanceEstadoActaReporteDto reporte = this.getAvanceEstadoActaReporte(filtro, cc.isPresent() ? cc.get().getCodigo() : "");
            List<AvanceEstadoActaDetalleDto> detalle = reporte.getDetalleAvanceEstadoMesa();


            Optional<Eleccion> eleccion = this.eleccionRepository.findById(Long.valueOf(filtro.getIdEleccion()));
            Optional<ProcesoElectoral> proceso = this.procesoElectoralRepository.findById(Long.valueOf(filtro.getIdProceso()));
            
            Map<String, Object> parametros = Funciones.getParametrosBaseReporte(
            		this.getClass(), 
					utilSceService.getSinValorOficial(filtro.getIdProceso()), 
            		utilSceService.getVersionSistema(), 
            		filtro.getUsuario(), 
            		proceso.isPresent() ? proceso.get().getNombre() : "",
            		null);

            parametros.put("PROCESO", reporte.getEncabezado().getNombreProceso());
            parametros.put("CENTRO_COMPUTO", cc.isPresent() ? cc.get().getCodigo() + " - " + cc.get().getNombre() : "");
            parametros.put("ELECCION",   eleccion.isPresent() ? eleccion.get().getNombre() : "");
            parametros.put("ESTADO",   filtro.getEstadoDescripcion());

            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON + "onpe.jpg");
            parametros.put("logo_onpe", imagen);
            parametros.put("sinvaloroficial", utilSceService.getSinValorOficial());
            parametros.put("version", utilSceService.getVersionSistema());
            parametros.put("servidor", InetAddress.getLocalHost().getHostName());
            parametros.put("usuario", filtro.getUsuario());

        	this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE, this.getClass().getSimpleName(), 
        			"Se consultó el Reporte de Avance de estado de actas",
        			ConstantesComunes.CC_NACION_DESCRIPCION, 
        			cc.isPresent() ? cc.get().getCodigo() : "", 
        			ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 
        			ConstantesComunes.LOG_TRANSACCIONES_ACCION);
            
            return Funciones.generarReporte(this.getClass(), detalle, ConstantesComunes.AVANCE_ESTADO_ACTAS_REPORT_JRXML, parametros);
            
        }catch(Exception e) {
            logger.error("excepcion", e);
            return null;
        }
    }

    
    private AvanceEstadoActaReporteDto getAvanceEstadoActaReporte(FiltroAvanceEstadoActaDto filtro, String codigoCentroComputo) {
        AvanceEstadoActaReporteDto val = null;

        List<AvanceEstadoActaDetalleDto> detalles = getDetalle(filtro);
        
        List<AvanceEstadoActaDetalleDto> totales = Arrays.asList(calcularTotales(detalles));
        
        AvanceEstadoActaResumenDto resumen = new AvanceEstadoActaResumenDto();
        
        resumen.setProcesadas(detalles.stream().mapToInt(AvanceEstadoActaDetalleDto::getFlagProcesadasCompletas).sum());
        resumen.setContabilizadas(detalles.stream().mapToInt(AvanceEstadoActaDetalleDto::getFlagContabilizadasCompletas).sum());
        resumen.setPorProcesar(detalles.size() - ( resumen.getProcesadas() + resumen.getContabilizadas() ));

        val = new AvanceEstadoActaReporteDto();
        val.setResumen(resumen);
        val.setEncabezado(this.getEncabezado(filtro));
        val.setDetalleAvanceEstadoMesa(detalles);
        val.setTotalAvanceEstadoMesa(totales);

        this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE,
                this.getClass().getSimpleName(), "Se consultó el Reporte de Avance de Estado de Actas.", "",
                codigoCentroComputo, ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);

        return val;
    }

    @Transactional("locationTransactionManager")
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
    
    private List<AvanceEstadoActaDetalleDto> getDetalle(FiltroAvanceEstadoActaDto filtro) {

        return reportesRepository.avanceEstadoActa(filtro.getSchema(), filtro.getIdEleccion(), filtro.getIdCentroComputo(), filtro.getEstado(), filtro.getUsuario())
                .stream().map(reporte -> {
                    AvanceEstadoActaDetalleDto acta = new AvanceEstadoActaDetalleDto();
                    acta.setIdCentroComputo((Integer) reporte.get("n_centro_computo"));
                    acta.setCodigoCc((String) reporte.get("c_codigo_centro_computo"));
                    acta.setNombreCc((String) reporte.get("c_nombre_centro_computo"));
                    acta.setFechaUltModificacion(DateUtil.getDateString((Date) reporte.get("d_fecha_ultima_modificacion"), SceConstantes.FORMATO_FECHA));
                    
                    Long mesasHabiles = (Long) reporte.get("n_mesas_habiles");
                    acta.setMesasHabiles(Long.valueOf(String.valueOf(mesasHabiles)));
                    
                    Long ingresadas = (Long) reporte.get("n_actas_ingresadas");
                    acta.setActasIngresadas(Long.valueOf(String.valueOf(ingresadas)));
                    
                    Long procesadas = (Long) reporte.get("n_actas_procesadas");
                    acta.setActasProcesadas(procesadas);
                    
                    Long contabilizadas = (Long) reporte.get("n_actas_contabilizadas");
                    acta.setActasContabilizadas(contabilizadas);
                    
                    Long observadas = (Long) reporte.get("n_actas_observadas");
                    acta.setActasPendientesResolverJEE(observadas);
                    
                    Double procesadasPor = (procesadas.doubleValue() / mesasHabiles.doubleValue()) * 100;
                    acta.setActasProcesadasPorcen(procesadasPor);
                    
                    Double contabilizadaPor = (contabilizadas.doubleValue() / mesasHabiles.doubleValue()) * 100;
                    acta.setActasContabilizadasPorcen(contabilizadaPor);
                    
                    Double observadaPor = (observadas.doubleValue() / mesasHabiles.doubleValue()) * 100;
                    acta.setActasPendientesResolverJEEPorcen(observadaPor);
                    
                    acta.setFlagProcesadasCompletas((Integer) reporte.get("n_flag_procesadas_completas"));
                    acta.setFlagContabilizadasCompletas((Integer) reporte.get("n_flag_contabilizadas_completas"));
                    acta.setTotalActas( acta.getActasIngresadas() + acta.getActasContabilizadas() + acta.getActasPendientesResolverJEE() + acta.getActasProcesadas() );
                    
                    return acta;
                    
                }).sorted(Comparator
                        .comparing(AvanceEstadoActaDetalleDto::getCodigoCc)
                        .thenComparing(AvanceEstadoActaDetalleDto::getNombreCc))
                .collect(Collectors.toList());
        
    }
    
    private AvanceEstadoActaDetalleDto calcularTotales(List<AvanceEstadoActaDetalleDto> detalles) {
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
        
        return total;
    }

}
