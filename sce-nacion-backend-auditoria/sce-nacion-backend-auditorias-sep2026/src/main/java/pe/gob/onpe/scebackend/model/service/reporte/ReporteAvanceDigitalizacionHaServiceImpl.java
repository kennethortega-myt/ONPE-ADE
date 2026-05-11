package pe.gob.onpe.scebackend.model.service.reporte;

import lombok.extern.java.Log;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasObservacionesRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteAvanceDigitalizacionJasperDto;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.IReporteAvanceDigitalizacionHaRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.model.service.mapper.ReporteAvanceDigitalizacionMapper;
import pe.gob.onpe.scebackend.utils.TransactionalLogUtil;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesReportes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;


import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes.PATH_IMAGE_COMMON_NAC;

@Service
@Log
public class ReporteAvanceDigitalizacionHaServiceImpl implements IReporteAvanceDigitalizacionHaService {

    private final ITabLogTransaccionalService logService;
    private final IReporteAvanceDigitalizacionHaRepository reporteAvanceDigitalizacionHaRepository;
    private final UtilSceService utilSceService;
    private final ReporteAvanceDigitalizacionMapper reporteAvanceDigitalizacionMapper;

    public ReporteAvanceDigitalizacionHaServiceImpl(
            ITabLogTransaccionalService logService, IReporteAvanceDigitalizacionHaRepository reporteAvanceDigitalizacionHaRepository,
            UtilSceService utilSceService, ReporteAvanceDigitalizacionMapper reporteAvanceDigitalizacionMapper) {
        this.logService = logService;
        this.reporteAvanceDigitalizacionHaRepository = reporteAvanceDigitalizacionHaRepository;
        this.utilSceService = utilSceService;
        this.reporteAvanceDigitalizacionMapper = reporteAvanceDigitalizacionMapper;
    }

    @Override
    @Transactional("locationTransactionManager")
    public byte[] reporteAvanceDigitalizacionHa(ReporteMesasObservacionesRequestDto filtro) throws JRException {
        Map<String, Object> parametrosReporte = new java.util.HashMap<>();
        String nombreReporte = "";
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON +  ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe
        parametrosReporte.put("url_imagen", imagen);
        parametrosReporte.put("sinValorOficial", this.utilSceService.getSinValorOficial());

        List<ReporteAvanceDigitalizacionJasperDto> lista = reporteAvanceDigitalizacionMapper.toReporteAvanceDigitalizacionJasperDtoList(
                reporteAvanceDigitalizacionHaRepository.listarReporteAvanceDigitalizacionHa(filtro));
        nombreReporte = ConstantesComunes.REPORTE_AVANCE_DIGITALIZACION_LE_HA;
        parametrosReporte.put("tituloReporte", ConstantesReportes.TITULO_REPORTE_AVANCE_DIGITALIZACION_HA);
        parametrosReporte.put("reporte", ConstantesReportes.nameReporteAvanceDigitalizacionHa);
        parametrosReporte.put("departamento", filtro.getDepartamento());
        parametrosReporte.put("provincia", filtro.getProvincia());
        parametrosReporte.put("distrito", filtro.getDistrito());
        parametrosReporte.put("totalDesc", "Total de Hojas de Asistencia");
        mapearCamposCabeceraReporte(filtro,parametrosReporte);

        this.logService.registrarLog(filtro.getUsuario(),
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                this.getClass().getName(),
                TransactionalLogUtil.crearMensajeLog(ConstantesReportes.TITULO_REPORTE_AVANCE_DIGITALIZACION_HA),
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
