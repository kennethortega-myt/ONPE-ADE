package pe.gob.onpe.scebackend.model.service.impl;

import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
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
import pe.gob.onpe.scebackend.model.service.IReporteTotalCentroComputoService;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.DateUtil;
import pe.gob.onpe.scebackend.utils.JasperReportUtil;
import pe.gob.onpe.scebackend.utils.SceConstantes;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReporteTotalCentroComputoService implements IReporteTotalCentroComputoService {
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
    @Transactional
    public AvanceEstadoActaReporteDto getAvanceEstadoActa(FiltroAvanceEstadoActaDto filtro) {
        return this.getAvanceEstadoActaReporte(filtro);
    }

    @Override
    @Transactional
    public byte[] getReporteAvanceEstadoActa(FiltroAvanceEstadoActaDto filtro) {
        try{

            AvanceEstadoActaReporteDto reporte = this.getAvanceEstadoActaReporte(filtro);
            //List<AvanceEstadoActaDetalleDto> detalle = reporte.getDetalleAvanceEstadoMesa();
            List<CatResumenTotalPorCentroComputo> detalle = new ArrayList<>();

            double totPorcActaDig= 0.0,totPorcResolDig= 0.0, totPorcActaSiniAnul= 0.0, totPorcActaExtravAnul= 0.0, totPorcActaProc= 0.0, totPorcActaContab= 0.0,
                    totPorcActaContCal= 0.0,totPorcResolContCal= 0.0,totPorcActaTrans= 0.0,totPorcResolTrans= 0.0,totPorcOmiMiem= 0.0,totPorcOmiVot = 0.0;

            double sumTotalActas = 0,sumActaDig = 0,sumResolDig = 0, sumActaSiniAnul = 0, sumActaExtravAnul = 0, sumActaProc = 0, sumActaContab = 0,
                    sumActaContCal = 0,sumResolContCal = 0,sumActaTrans = 0,sumResolTrans = 0,sumOmiMiem = 0,sumOmiVot = 0, sumResolIng = 0,
                    sumMesaNoInst = 0;

            Map<String, Object> parametros = new java.util.HashMap<>();

            parametros.put("reporte", "parametro reporte");
            parametros.put("totPorcActaDig",totPorcActaDig);
            parametros.put("totPorcResolDig",totPorcResolDig);
            parametros.put("totPorcActaSiniAnul",totPorcActaSiniAnul);
            parametros.put("totPorcActaExtravAnul",totPorcActaExtravAnul);
            parametros.put("totPorcActaProc",totPorcActaProc);
            parametros.put("totPorcActaContab",totPorcActaContab);
            parametros.put("totPorcActaContCal",totPorcActaContCal);
            parametros.put("totPorcResolContCal",totPorcResolContCal);
            parametros.put("totPorcActaTrans",totPorcActaTrans);
            parametros.put("totPorcResolTrans",totPorcResolTrans);
            parametros.put("totPorcOmiMiem",totPorcOmiMiem);
            parametros.put("totPorcOmiVot",totPorcOmiVot);
            parametros.put("TituloGeneral", "Titulo general parametro");
            parametros.put("TituloRep", "parametro2");
            parametros.put("TituloEleccionSimple", "parametro3");
            parametros.put("TituloEleccionCompleto","parametro 4"); //JJSG 2018-05-08
            parametros.put("desOdpe", "parametro 5");
            parametros.put("desComp", "parametro 6");
            parametros.put("estado", "parametro 7");
            parametros.put("tipoconexion", "parametro8");

            parametros.put("SVOF", this.utilSceService.getSinValorOficial(filtro.getIdProceso()));

            parametros.put("servidor", "parametro 10");
            parametros.put("usuario", "parametro 11");
            parametros.put("estacion", "parametro 12");
            parametros.put("version", "paraemtro 13");


            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + "onpe.jpg");//logo onpe
            parametros.put("url_imagen", imagen);


            InputStream file = this.getClass().getClassLoader().getResourceAsStream( ConstantesComunes.PATH_REPORT_TOTAL_CENTRO_COMPUTO_JRXML);

            byte[] pdf = null;

        	this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE, this.getClass().getSimpleName(), 
        	"Se consultó el Reporte Total por Centro de Computo",
        			ConstantesComunes.CC_NACION_DESCRIPCION, "C56000", ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 
        			ConstantesComunes.LOG_TRANSACCIONES_ACCION);
            
            if(true) {
                JasperPrint jasperPrint = JasperReportUtil.getJasperPrint(parametros, detalle, file );
                JRPdfExporter jrPdfExporter = new JRPdfExporter();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                jrPdfExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
                jrPdfExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
                jrPdfExporter.exportReport();
                pdf=byteArrayOutputStream.toByteArray();
                return pdf;
            } else {
                return pdf;
            } // end-if


        }catch(Exception e) {
            logger.error("excepcion", e);
            return null;
        }
    }

    @Transactional("locationTransactionManager")
    public AvanceEstadoActaReporteDto getAvanceEstadoActaReporte(FiltroAvanceEstadoActaDto filtro) {
        AvanceEstadoActaReporteDto val = null;

        EncabezadoFiltroAvanceEstadoActaDto encabezado = this.getEncabezado(filtro);
        List<AvanceEstadoActaDetalleDto> detalles = reportesRepository.avanceEstadoActa(filtro.getSchema(), filtro.getIdEleccion(), filtro.getIdCentroComputo(), 0, filtro.getUsuario())
                .stream().map(reporte -> {
                    AvanceEstadoActaDetalleDto estadoActa = new AvanceEstadoActaDetalleDto();
                    estadoActa.setIdCentroComputo((Integer) reporte.get("n_centro_computo"));
                    estadoActa.setCodigoCc((String) reporte.get("c_codigo_centro_computo"));
                    estadoActa.setNombreCc((String) reporte.get("c_nombre_centro_computo"));
                    estadoActa.setFechaUltModificacion(DateUtil.getDateString((Date) reporte.get("d_fecha_ultima_modificacion"), SceConstantes.FORMATO_FECHA));
                    BigInteger mesasHabiles = (BigInteger) reporte.get("n_mesas_habiles");
                    estadoActa.setMesasHabiles(Long.valueOf(String.valueOf(mesasHabiles)));
                    BigInteger ingresadas = (BigInteger) reporte.get("n_actas_ingresadas");
                    estadoActa.setActasIngresadas(Long.valueOf(String.valueOf(ingresadas)));
                    BigInteger procesadas = (BigInteger) reporte.get("n_actas_procesadas");
                    estadoActa.setActasProcesadas(Long.valueOf(String.valueOf(procesadas)));
                    BigInteger contabilizadas = (BigInteger) reporte.get("n_actas_contabilizadas");
                    estadoActa.setActasContabilizadas(Long.valueOf(String.valueOf(contabilizadas)));
                    BigInteger observadas = (BigInteger) reporte.get("n_actas_observadas");
                    estadoActa.setActasPendientesResolverJEE(Long.valueOf(String.valueOf(observadas)));
                    BigDecimal procesadasPor = (BigDecimal) reporte.get("n_porcentaje_actas_procesadas");
                    estadoActa.setActasProcesadasPorcen(Double.valueOf(String.valueOf(procesadasPor)) );
                    BigDecimal contabilizadaPor = (BigDecimal) reporte.get("n_porcentaje_actas_contabilizadas");
                    estadoActa.setActasContabilizadasPorcen(Double.valueOf(String.valueOf(contabilizadaPor)));
                    BigDecimal observadaPor = (BigDecimal) reporte.get("n_porcentaje_actas_observadas");
                    estadoActa.setActasPendientesResolverJEEPorcen(Double.valueOf(String.valueOf(observadaPor)));
                    estadoActa.setFlagProcesadasCompletas((Integer) reporte.get("n_flag_procesadas_completas"));
                    estadoActa.setFlagContabilizadasCompletas((Integer) reporte.get("n_flag_contabilizadas_completas"));
                    estadoActa.setTotalActas(estadoActa.getActasIngresadas()+estadoActa.getActasContabilizadas()+estadoActa.getActasPendientesResolverJEE()+estadoActa.getActasProcesadas());
                    return estadoActa;
                }).sorted(Comparator
                        .comparing(AvanceEstadoActaDetalleDto::getCodigoCc)
                        .thenComparing(AvanceEstadoActaDetalleDto::getNombreCc))
                .collect(Collectors.toList());


        AvanceEstadoActaResumenDto resumen = new AvanceEstadoActaResumenDto();
        resumen.setProcesadas(detalles.stream().mapToInt(AvanceEstadoActaDetalleDto::getFlagProcesadasCompletas).sum());
        resumen.setContabilizadas(detalles.stream().mapToInt(AvanceEstadoActaDetalleDto::getFlagContabilizadasCompletas).sum());
        resumen.setPorProcesar(detalles.size() - resumen.getProcesadas() - resumen.getContabilizadas());

        val = new AvanceEstadoActaReporteDto();
        val.setEncabezado(encabezado);
        val.setResumen(resumen);
        val.setDetalleAvanceEstadoMesa(detalles);

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
}
