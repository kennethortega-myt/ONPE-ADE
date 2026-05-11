package pe.gob.onpe.scebackend.model.service.reporte;

import lombok.extern.java.Log;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.dto.reportes.ReporteMesasObservacionesJasperDto;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasObservacionesRequestDto;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.IReporteMesasObservacionesRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.model.service.mapper.ReporteMesasObservacionesMapper;
import pe.gob.onpe.scebackend.utils.TransactionalLogUtil;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesReportes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
@Log
public class ReporteMesasObservacionesService implements IReporteMesasObservacionesService {

    private final ITabLogTransaccionalService logService;
    private final IReporteMesasObservacionesRepository reporteMesasObservacionesRepository;
    private final UtilSceService utilSceService;
    private final ReporteMesasObservacionesMapper reporteMesasObservacionesMapper;

    public ReporteMesasObservacionesService(IReporteMesasObservacionesRepository reporteMesasObservacionesRepository,
                                            ITabLogTransaccionalService logService,
                                            UtilSceService utilSceService, ReporteMesasObservacionesMapper reporteMesasObservacionesMapper) {
        this.reporteMesasObservacionesRepository = reporteMesasObservacionesRepository;
        this.logService = logService;
        this.utilSceService = utilSceService;
        this.reporteMesasObservacionesMapper = reporteMesasObservacionesMapper;
    }

    @Override
    @Transactional("locationTransactionManager")
    public byte[] reporteMesasObservaciones(ReporteMesasObservacionesRequestDto filtro) throws JRException {
        Map<String, Object> parametrosReporte = new java.util.HashMap<>();
        String nombreReporte = "";
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC +  ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe
        parametrosReporte.put("url_imagen", imagen);
        parametrosReporte.put("sinValorOficial", this.utilSceService.getSinValorOficial(filtro.getIdProceso()));

        List<ReporteMesasObservacionesJasperDto> lista = reporteMesasObservacionesMapper.toReporteMesasObservacionesJasperDtoList(
                reporteMesasObservacionesRepository.listarReporteMesasObservaciones(filtro));
        nombreReporte = ConstantesComunes.REPORTE_MESAS_OBSERVACIONES;
        parametrosReporte.put("tituloReporte", ConstantesReportes.TITULO_REPORTE_MESAS_OBSERVACIONES);
        parametrosReporte.put("reporte", ConstantesReportes.NAME_REPORTE_MESAS_OBSERVACIONES);
        mapearCamposCabeceraReporte(filtro,parametrosReporte);

        this.logService.registrarLog(filtro.getUsuario(),
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                this.getClass().getName(),
                TransactionalLogUtil.crearMensajeLog(ConstantesReportes.TITULO_REPORTE_MESAS_OBSERVACIONES),
                "",
                filtro.getCodigoCentroComputo(),
                0, 1);

        return Funciones.generarReporte(this.getClass(), lista, nombreReporte, parametrosReporte);
    }


    private void mapearCamposCabeceraReporte(
            ReporteMesasObservacionesRequestDto filtro, Map<String, Object> parametrosReporte) {

        parametrosReporte.put("proceso", filtro.getProceso());
        parametrosReporte.put("usuario", filtro.getUsuario());
        parametrosReporte.put("version", utilSceService.getVersionSistema());
        parametrosReporte.put("tituloEleccionCompleto", filtro.getEleccion());


    }
}
