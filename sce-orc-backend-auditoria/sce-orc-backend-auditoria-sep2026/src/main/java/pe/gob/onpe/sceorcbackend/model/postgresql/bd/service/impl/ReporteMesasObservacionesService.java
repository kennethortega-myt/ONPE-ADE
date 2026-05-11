package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl;

import lombok.extern.java.Log;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteMesasObservacionesJasperDto;
import pe.gob.onpe.sceorcbackend.model.dto.request.ReporteMesasObservacionesRequestDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes.IReporteMesasObservacionesRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.mapper.ReporteMesasObservacionesMapper;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IReporteMesasObservacionesService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesReportes;
import pe.gob.onpe.sceorcbackend.utils.TransactionalLogUtil;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
@Log
public class ReporteMesasObservacionesService implements IReporteMesasObservacionesService {

    private final ITabLogService logService;
    private final IReporteMesasObservacionesRepository reporteMesasObservacionesRepository;
    private final UtilSceService utilSceService;
    private final ReporteMesasObservacionesMapper reporteMesasObservacionesMapper;

    public ReporteMesasObservacionesService(IReporteMesasObservacionesRepository reporteMesasObservacionesRepository,
                                            ITabLogService logService,
                                            UtilSceService utilSceService, ReporteMesasObservacionesMapper reporteMesasObservacionesMapper) {
        this.reporteMesasObservacionesRepository = reporteMesasObservacionesRepository;
        this.logService = logService;
        this.utilSceService = utilSceService;
        this.reporteMesasObservacionesMapper = reporteMesasObservacionesMapper;
    }

    @Override
    public byte[] reporteMesasObservaciones(ReporteMesasObservacionesRequestDto filtro) throws JRException {
        Map<String, Object> parametrosReporte = new java.util.HashMap<>();
        String nombreReporte = "";
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON +  ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe
        parametrosReporte.put("url_imagen", imagen);
        parametrosReporte.put("sinValorOficial", this.utilSceService.getSinValorOficial());

        List<ReporteMesasObservacionesJasperDto> lista = reporteMesasObservacionesMapper.toReporteMesasObservacionesJasperDtoList(
                reporteMesasObservacionesRepository.listarReporteMesasObservaciones(filtro));
        nombreReporte = ConstantesComunes.REPORTE_MESAS_OBSERVACIONES;
        parametrosReporte.put("tituloReporte", ConstantesReportes.TITULO_REPORTE_MESAS_OBSERVACIONES);
        parametrosReporte.put("reporte", ConstantesReportes.nameReporteMesasObservaciones);
        mapearCamposCabeceraReporte(filtro,parametrosReporte);

        this.logService.registrarLog(filtro.getUsuario(),
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                this.getClass().getName(),
                TransactionalLogUtil.crearMensajeLog(ConstantesReportes.TITULO_REPORTE_MESAS_OBSERVACIONES),
                filtro.getCodigoCentroComputo(),
                ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, 1);

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
