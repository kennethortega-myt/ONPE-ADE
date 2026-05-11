package pe.gob.onpe.scebackend.model.service.reporte;

import lombok.extern.java.Log;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.dto.reportes.OmisosJasperDto;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteOmisosRequestDto;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.model.service.mapper.ReporteOmisosMapper;
import pe.gob.onpe.scebackend.utils.TransactionalLogUtil;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
@Log
public class ReporteOmisosService implements IReporteOmisosService {

    private final ReporteOmisosMapper reporteOmisosMapper;
    private final ITabLogTransaccionalService logService;
    private final ReporteOmisosHandlerFactory reporteOmisosHandlerFactory;
    private final UtilSceService utilSceService;

    public ReporteOmisosService(ReporteOmisosMapper reporteOmisosMapper, ITabLogTransaccionalService logService, ReporteOmisosHandlerFactory reporteOmisosHandlerFactory, UtilSceService utilSceService) {
        this.reporteOmisosMapper = reporteOmisosMapper;
        this.logService = logService;
        this.reporteOmisosHandlerFactory = reporteOmisosHandlerFactory;
        this.utilSceService = utilSceService;
    }

    @Override
    @Transactional("locationTransactionManager")
    public byte[] reporteOmisos(ReporteOmisosRequestDto filtro) throws JRException {
        List<OmisosJasperDto> lista;
        Map<String, Object> parametrosReporte = new java.util.HashMap<>();
        String nombreReporte = "";
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC +  ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe
        parametrosReporte.put("url_imagen", imagen);
        parametrosReporte.put("sinValorOficial", this.utilSceService.getSinValorOficial(filtro.getIdProceso()));

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
        parametrosReporte.put("prefUbigeoExtranjero", ConstantesComunes.PREFIJO_UBIGEO_EXTRANJEROS);
        mapearCamposCabeceraReporte(filtro, parametrosReporte);

        this.logService.registrarLog(filtro.getUsuario(),
                                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                                        this.getClass().getName(),
                                        TransactionalLogUtil.crearMensajeLog(handler.getTituloReporte()),
                                        "",
                                        filtro.getCodigoCentroComputo(),
                                        0, 1);

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
