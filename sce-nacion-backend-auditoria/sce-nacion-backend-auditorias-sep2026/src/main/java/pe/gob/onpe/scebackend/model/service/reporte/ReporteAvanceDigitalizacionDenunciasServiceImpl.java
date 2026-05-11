package pe.gob.onpe.scebackend.model.service.reporte;

import lombok.extern.java.Log;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteMesasObservacionesRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteAvanceDigitalizacionJasperDto;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.IReporteAvanceDigitalizacionDenunciasRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.model.service.mapper.ReporteAvanceDigitalizacionDenunciasMapper;
import pe.gob.onpe.scebackend.utils.TransactionalLogUtil;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesReportes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;


import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
@Log
public class ReporteAvanceDigitalizacionDenunciasServiceImpl implements IReporteAvanceDigitalizacionDenunciasService {

    private final ITabLogTransaccionalService logService;
    private final IReporteAvanceDigitalizacionDenunciasRepository reporteAvanceDigitalizacionDenunciasRepository;
    private final UtilSceService utilSceService;
    private final ReporteAvanceDigitalizacionDenunciasMapper reporteAvanceDigitalizacionDenunciasMapper;

    public ReporteAvanceDigitalizacionDenunciasServiceImpl(
            ITabLogTransaccionalService logService, IReporteAvanceDigitalizacionDenunciasRepository reporteAvanceDigitalizacionDenunciasRepository,
            UtilSceService utilSceService, ReporteAvanceDigitalizacionDenunciasMapper reporteAvanceDigitalizacionDenunciasMapper) {
        this.logService = logService;
        this.reporteAvanceDigitalizacionDenunciasRepository = reporteAvanceDigitalizacionDenunciasRepository;
        this.utilSceService = utilSceService;
        this.reporteAvanceDigitalizacionDenunciasMapper = reporteAvanceDigitalizacionDenunciasMapper;
    }

    @Override
    @Transactional("locationTransactionManager")
    public byte[] reporteAvanceDigitalizacionDenuncias(ReporteMesasObservacionesRequestDto filtro) throws JRException {
        Map<String, Object> parametrosReporte = new java.util.HashMap<>();
        String nombreReporte = "";
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON +  ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe
        parametrosReporte.put("url_imagen", imagen);
        parametrosReporte.put("sinValorOficial", this.utilSceService.getSinValorOficial());

        List<ReporteAvanceDigitalizacionJasperDto> lista = reporteAvanceDigitalizacionDenunciasMapper.toReporteAvanceDigitalizacionJasperDtoList(
                reporteAvanceDigitalizacionDenunciasRepository.listarReporteAvanceDigitalizacionDenuncia(filtro));
        nombreReporte = ConstantesComunes.REPORTE_AVANCE_DIGITALIZACION_LE_DEN;
        parametrosReporte.put("tituloReporte", ConstantesReportes.TITULO_REPORTE_AVANCE_DIGITALIZACION_DENUNCIAS);
        parametrosReporte.put("reporte", ConstantesReportes.nameReporteAvanceDigitalizacionDenuncias);
        parametrosReporte.put("departamento", filtro.getDepartamento());
        parametrosReporte.put("provincia", filtro.getProvincia());
        parametrosReporte.put("distrito", filtro.getDistrito());
        parametrosReporte.put("totalDesc", "Total de Denuncias");
        mapearCamposCabeceraReporte(filtro,parametrosReporte);

        this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE,
                this.getClass().getSimpleName(), "Se consultó el Reporte de Avance de Digitalización de Denuncias.", "",
                filtro.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);

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
