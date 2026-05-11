package pe.gob.onpe.scebackend.model.service.reporte;

import lombok.extern.java.Log;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Service;
import pe.gob.onpe.scebackend.model.dto.reportes.ReporteMesasSinOmisosJasperDto;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasSinOmisosRequestDto;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.model.service.mapper.ReporteMesasSinOmisosMapper;
import pe.gob.onpe.scebackend.utils.TransactionalLogUtil;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
@Log
public class ReporteMesasSinOmisosService implements IReporteMesasSinOmisosService {

    private final ReporteMesasSinOmisosMapper reporteMesasSinOmisosMapper;
    private final ITabLogTransaccionalService logService;
    private final ReporteMesasSinOmisosHandlerFactory reporteMesasSinOmisosHandlerFactory;
    private final UtilSceService utilSceService;

    public ReporteMesasSinOmisosService(ReporteMesasSinOmisosMapper reporteMesasSinOmisosMapper, ITabLogTransaccionalService logService, ReporteMesasSinOmisosHandlerFactory reporteMesasSinOmisosHandlerFactory, UtilSceService utilSceService) {
        this.reporteMesasSinOmisosMapper = reporteMesasSinOmisosMapper;
        this.logService = logService;
        this.reporteMesasSinOmisosHandlerFactory = reporteMesasSinOmisosHandlerFactory;
        this.utilSceService = utilSceService;
    }

    @Override
    public byte[] reporteMesasSinOmisos(ReporteMesasSinOmisosRequestDto filtro) throws JRException {
        List<ReporteMesasSinOmisosJasperDto> lista;
        Map<String, Object> parametrosReporte = new java.util.HashMap<>();
        String nombreReporte = "";
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC +  ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe
        parametrosReporte.put("url_imagen", imagen);
        parametrosReporte.put("sinValorOficial", this.utilSceService.getSinValorOficial(filtro.getIdProceso()));

        ReporteMesasSinOmisosHandler handler = reporteMesasSinOmisosHandlerFactory.getHandler(filtro);

        lista = reporteMesasSinOmisosMapper.toMesasSinOmisosJasperDtoList(handler.obtenerReporte(filtro));
        parametrosReporte.put("tipoReporteActorElectoral", handler.getTipoReporteActorElectoral());
        parametrosReporte.put("tituloReporte", handler.getTituloReporte());
        parametrosReporte.put("prefUbigeoExtranjero", ConstantesComunes.PREFIJO_UBIGEO_EXTRANJEROS);
        
        nombreReporte = ConstantesComunes.REPORTE_MESAS_SIN_OMISOS;
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
            ReporteMesasSinOmisosRequestDto filtro, Map<String, Object> parametrosReporte) {

        parametrosReporte.put("proceso", filtro.getProceso());
        parametrosReporte.put("usuario", filtro.getUsuario());
        parametrosReporte.put("version", utilSceService.getVersionSistema());
        parametrosReporte.put("tituloEleccionCompleto", "");


    }

}
