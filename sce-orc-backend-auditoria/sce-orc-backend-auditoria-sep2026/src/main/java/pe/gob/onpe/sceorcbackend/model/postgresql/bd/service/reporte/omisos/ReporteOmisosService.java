package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.omisos;

import lombok.extern.java.Log;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.OmisosJasperDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteOmisosRequestDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.mapper.ReporteOmisosMapper;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.TransactionalLogUtil;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
@Log
public class ReporteOmisosService implements IReporteOmisosService {

    private final ReporteOmisosMapper reporteOmisosMapper;
    private final ITabLogService logService;
    private final ReporteOmisosHandlerFactory reporteOmisosHandlerFactory;
    private final UtilSceService utilSceService;

    public ReporteOmisosService(ReporteOmisosMapper reporteOmisosMapper, ITabLogService logService, ReporteOmisosHandlerFactory reporteOmisosHandlerFactory, UtilSceService utilSceService) {
        this.reporteOmisosMapper = reporteOmisosMapper;
        this.logService = logService;
        this.reporteOmisosHandlerFactory = reporteOmisosHandlerFactory;
        this.utilSceService = utilSceService;
    }

    @Override
    public byte[] reporteOmisos(ReporteOmisosRequestDto filtro) throws JRException {
        List<OmisosJasperDto> lista;
        Map<String, Object> parametrosReporte = new java.util.HashMap<>();
        String nombreReporte = "";
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON +  ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe
        parametrosReporte.put("url_imagen", imagen);
        parametrosReporte.put("sinValorOficial", this.utilSceService.getSinValorOficial());

        ReporteOmisosHandler handler = reporteOmisosHandlerFactory.getHandler(filtro);


        lista = reporteOmisosMapper.toOmisosJasperDtoList(handler.obtenerReporte(filtro));
        parametrosReporte.put("nombreColumna", handler.getNombreColumna());
        parametrosReporte.put("tipoReporteActorElectoral", handler.getTipoReporteActorElectoral());
        parametrosReporte.put("tituloReporte", handler.getTituloReporte());

        if (filtro.getTipoReporte() == 1) {
            nombreReporte = ConstantesComunes.REPORTE_OMISOS_DETALLE;
            parametrosReporte.put("reporte", handler.getNombreReporteDetalle());
        } else {
            nombreReporte = ConstantesComunes.REPORTE_OMISOS_RESUMEN;
            parametrosReporte.put("reporte", handler.getNombreReporteResumen());

        }
        mapearCamposCabeceraReporte(filtro, parametrosReporte);

        this.logService.registrarLog(filtro.getUsuario(),
                                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                                        this.getClass().getName(),
                                        TransactionalLogUtil.crearMensajeLog(handler.getTituloReporte()),
                                        filtro.getCodigoCentroComputo(),
                ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

        return Funciones.generarReporte(this.getClass(), lista, nombreReporte, parametrosReporte);
    }


    private void mapearCamposCabeceraReporte(
            ReporteOmisosRequestDto filtro, Map<String, Object> parametrosReporte) {

        parametrosReporte.put("proceso", filtro.getProceso());
        parametrosReporte.put("usuario", filtro.getUsuario());
        parametrosReporte.put("version", utilSceService.getVersionSistema());
        parametrosReporte.put("tituloEleccionCompleto", "");

    }
}
