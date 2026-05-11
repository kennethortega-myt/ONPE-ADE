package pe.gob.onpe.scebackend.model.service.reporte;

import lombok.extern.java.Log;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasObservacionesRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteAvanceDigitalizacionJasperDto;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.IReporteAvanceDigitalizacionLeRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.model.service.mapper.ReporteAvanceDigitalizacionMapper;
import pe.gob.onpe.scebackend.utils.TransactionalLogUtil;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesReportes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

import static pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes.LOG_TRANSACCIONES_ACCION;
import static pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO;
import static pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
@Log
public class ReporteAvanceDigitalizacionLeServiceImpl implements IReporteAvanceDigitalizacionLeService {

    private final ITabLogTransaccionalService logService;
    private final IReporteAvanceDigitalizacionLeRepository reporteAvanceDigitalizacionLeRepository;
    private final UtilSceService utilSceService;
    private final ReporteAvanceDigitalizacionMapper reporteAvanceDigitalizacionMapper;

    public ReporteAvanceDigitalizacionLeServiceImpl(
            ITabLogTransaccionalService logService, IReporteAvanceDigitalizacionLeRepository reporteAvanceDigitalizacionLeRepository,
            UtilSceService utilSceService, ReporteAvanceDigitalizacionMapper reporteAvanceDigitalizacionMapper) {
        this.logService = logService;
        this.reporteAvanceDigitalizacionLeRepository = reporteAvanceDigitalizacionLeRepository;
        this.utilSceService = utilSceService;
        this.reporteAvanceDigitalizacionMapper = reporteAvanceDigitalizacionMapper;
    }

    @Override
    @Transactional("locationTransactionManager")
    public byte[] reporteAvanceDigitalizacionLe(ReporteMesasObservacionesRequestDto filtro) throws JRException {
        Map<String, Object> parametrosReporte = new java.util.HashMap<>();
        String nombreReporte = "";
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON +  ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe
        parametrosReporte.put("url_imagen", imagen);
        parametrosReporte.put("sinValorOficial", this.utilSceService.getSinValorOficial());

        List<ReporteAvanceDigitalizacionJasperDto> lista = reporteAvanceDigitalizacionMapper.toReporteAvanceDigitalizacionJasperDtoList(
                this.reporteAvanceDigitalizacionLeRepository.listarReporteAvanceDigitalizacionLe(filtro));
        nombreReporte = ConstantesComunes.REPORTE_AVANCE_DIGITALIZACION_LE_HA;
        parametrosReporte.put("tituloReporte", ConstantesReportes.TITULO_REPORTE_AVANCE_DIGITALIZACION_LE);
        parametrosReporte.put("reporte", ConstantesReportes.nameReporteAvanceDigitalizacionLe);
        parametrosReporte.put("departamento", filtro.getDepartamento());
        parametrosReporte.put("provincia", filtro.getProvincia());
        parametrosReporte.put("distrito", filtro.getDistrito());
        parametrosReporte.put("totalDesc", "Total de Lista de Electores");
        mapearCamposCabeceraReporte(filtro,parametrosReporte);

        this.logService.registrarLog(filtro.getUsuario(), 
        		LOG_TRANSACCIONES_TIPO_REPORTE,
				this.getClass().getSimpleName(), "Se consultó el Reporte de Avance de Digitalización de Lista de Electores.", "",
				filtro.getCodigoCentroComputo(), LOG_TRANSACCIONES_AUTORIZACION_NO, LOG_TRANSACCIONES_ACCION);

        return Funciones.generarReporte(this.getClass(), lista, nombreReporte, parametrosReporte);
    }


    private void mapearCamposCabeceraReporte(
            ReporteMesasObservacionesRequestDto filtro, Map<String, Object> parametrosReporte) {

        parametrosReporte.put("proceso", filtro.getProceso());
        parametrosReporte.put("usuario", filtro.getUsuario());
        parametrosReporte.put("version", utilSceService.getVersionSistema());


    }
}
