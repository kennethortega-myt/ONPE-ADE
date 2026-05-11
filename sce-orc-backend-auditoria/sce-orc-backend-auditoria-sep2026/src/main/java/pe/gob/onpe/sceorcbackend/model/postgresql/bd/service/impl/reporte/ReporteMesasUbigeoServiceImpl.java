package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import lombok.extern.java.Log;
import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.*;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.MesaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IReporteMesasUbigeoService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesReportes;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

import java.io.File;
import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log
public class ReporteMesasUbigeoServiceImpl implements IReporteMesasUbigeoService {
    
    Logger logger = LoggerFactory.getLogger(ReporteMesasUbigeoServiceImpl.class);

    @Autowired
    private MesaRepository mesaRepository;
    @Autowired
    private UtilSceService utilSceService;
    @Autowired
    private ITabLogService logService;
    
    @Override
    public byte[] reporteMesasUbigeo(ReporteMesasUbigeoRequestDto filtro) {
        
        try {
            ReporteMesasUbigeoDto reporte = this.getListaMesaUbigeoReporte(filtro);
            List<ReporteMesaUbigeoDetalleDto> detalle = reporte.getDetalleMesaUbigeo();
       
            Map<String, Object> parametros = new java.util.HashMap<>();
            parametros.put("tituloGeneral", filtro.getProceso());
            parametros.put("tituloRep", ConstantesReportes.TITULO_REPORTE_LISTADO_MESAS_UBIGEO);
            parametros.put("tipoReporte", "1");
            parametros.put("sinValorOficial", utilSceService.getSinValorOficial());
            parametros.put("version", utilSceService.getVersionSistema());            
            parametros.put("usuario", filtro.getUsuario());

            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON  + ConstantesComunes.REPORT_PARAM_IMAGEN_ONPE);//logo onpe
            parametros.put("imagen", imagen);

            InputStream file = this.getClass().getClassLoader().getResourceAsStream(
                    ConstantesComunes.PATH_REPORT_JRXML + File.separator + ConstantesComunes.LISTA_MESAS_UBIGEO_REPORT_JRXML);
            String nombreReporte = "";
            byte[] pdf = null;
            
            try {
            	this.logService.registrarLog(filtro.getUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(),
            			this.getClass().getSimpleName(), "Se consultó el Reporte de mesas por ubigeo.", filtro.getCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);
            } catch (Exception e) {
            	logger.error("Error al registra log de Reporte de  mesas por ubigeo: ", e);
            }
            
            if (reporte != null && reporte.getDetalleMesaUbigeo() != null && !reporte.getDetalleMesaUbigeo().isEmpty()) {
                nombreReporte = ConstantesComunes.LISTA_MESAS_UBIGEO_REPORT_JRXML;
                return Funciones.generarReporte(this.getClass(),detalle,nombreReporte,parametros);
            } else {
                return pdf;
            }
        } catch (Exception e) {
            logger.error("excepcion", e);
            return null;
        }
    }
    
    public ReporteMesasUbigeoDto getListaMesaUbigeoReporte(ReporteMesasUbigeoRequestDto filtro) {
        
        ReporteMesasUbigeoDto val=null;
        ReporteMesaUbigeoEncabezadoDto encabezado = this.getEncabezado(filtro);
        
        String departamento=parseaCodigoUbigeo(filtro.getDepartamento());
        String provincia=parseaCodigoUbigeo(filtro.getProvincia());
        String distrito=parseaCodigoUbigeo(filtro.getDistrito());
        
        TypedParameterValue centroComputo = new TypedParameterValue(StandardBasicTypes.STRING, filtro.getCentroComputo());
     
        List<ReporteMesaUbigeoDetalleDto> listaMesas = mesaRepository.getReporteMesaPorUbigeo(filtro.getEsquema(), centroComputo, departamento, provincia, distrito)        
                .stream().map(reporte -> {
                    ReporteMesaUbigeoDetalleDto mesaUbigeoDetalleDto = new ReporteMesaUbigeoDetalleDto();
                    mesaUbigeoDetalleDto.setCodAmbito((String) reporte.get("c_codigo_ambito"));
                    mesaUbigeoDetalleDto.setcCodiCompu((String) reporte.get("c_codigo_centro_computo"));
                    mesaUbigeoDetalleDto.settDescCompu((String) reporte.get("c_descripcion_centro_computo"));
                    mesaUbigeoDetalleDto.setcCodiUbigeo((String) reporte.get("c_ubigeo"));
                    mesaUbigeoDetalleDto.setDepartamento((String) reporte.get("c_departamento"));
                    mesaUbigeoDetalleDto.setProvincia((String) reporte.get("c_provincia"));
                    mesaUbigeoDetalleDto.setDistrito((String) reporte.get("c_distrito"));
                    mesaUbigeoDetalleDto.setcCodiLocal((String) reporte.get("c_codigo_local"));
                    mesaUbigeoDetalleDto.settNombLocal((String) reporte.get("c_nombre_local"));
                    mesaUbigeoDetalleDto.settDireLocal((String) reporte.get("c_direccion_local"));
                    mesaUbigeoDetalleDto.setcNumeMesa((String) reporte.get("c_numero_mesa"));
                    mesaUbigeoDetalleDto.setNumElectores((Integer) reporte.get("n_numero_electores"));
                    return mesaUbigeoDetalleDto;

                }).sorted(Comparator
                .comparing(ReporteMesaUbigeoDetalleDto::getDepartamento)
                .thenComparing(ReporteMesaUbigeoDetalleDto::getProvincia))
                .collect(Collectors.toList());

        ReporteMesaUbigeoResumenDto resumen = new ReporteMesaUbigeoResumenDto();
        resumen.setNumElectores(listaMesas.stream().mapToLong(ReporteMesaUbigeoDetalleDto::getNumElectores).sum());
     
        val = new ReporteMesasUbigeoDto();
        val.setDetalleMesaUbigeo(listaMesas);
        val.setEncabezado(encabezado);
        val.setResumen(resumen);
        return val;
    }
    
    public ReporteMesaUbigeoEncabezadoDto getEncabezado(ReporteMesasUbigeoRequestDto filtro) {         
        ReporteMesaUbigeoEncabezadoDto encabezado = new ReporteMesaUbigeoEncabezadoDto();
        return encabezado;
    }
    
    private String parseaCodigoUbigeo(String idUbigeo){
    
        if(idUbigeo.length()==5){
            idUbigeo = "0"+idUbigeo;
        }  
        return idUbigeo;
    }
    
}
