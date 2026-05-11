package pe.gob.onpe.scebackend.model.service.reporte;

import lombok.extern.java.Log;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Service;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteInformacionOficialRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteInformacionOficialDto;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.IReporteInformacionOficialRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.TransactionalLogUtil;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesReportes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Service
@Log
public class ReporteInformacionOficialService implements IReporteInformacionOficialService {

    public static final String DEFAULT_UBIGEO_VALUE = "TODOS";
    private final IReporteInformacionOficialRepository reporteInformacionOficialRepository;
    private final UtilSceService utilSceService;
    private final ITabLogTransaccionalService logService;

    public ReporteInformacionOficialService(IReporteInformacionOficialRepository reporteInformacionOficialRepository, UtilSceService utilSceService, ITabLogTransaccionalService logService) {
        this.reporteInformacionOficialRepository = reporteInformacionOficialRepository;
        this.utilSceService = utilSceService;
        this.logService = logService;
    }

    @Override
    public byte[] reporteInformacionOficial(ReporteInformacionOficialRequestDto filtro) throws JRException {
        List<ReporteInformacionOficialDto> lista = null;
        Map<String, Object> parametrosReporte = new java.util.HashMap<>();
        String nombreReporte = "";
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC +  ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe
        parametrosReporte.put("url_imagen", imagen);
        parametrosReporte.put("sinValorOficial", utilSceService.getSinValorOficial(filtro.getIdProceso()));

        lista = reporteInformacionOficialRepository.listarReporteInformacionOficial(filtro);
        nombreReporte = ConstantesComunes.REPORTE_INFORMACION_OFICIAL;
        parametrosReporte.put("tituloReporte", ConstantesComunes.TITULO_REPORTE_INFORMACION_OFICIAL);
        mapearCamposCabeceraReporte(filtro,parametrosReporte);

        this.logService.registrarLog(filtro.getUsuario(),
                Thread.currentThread().getStackTrace()[1].getMethodName(),
                this.getClass().getName(),
                TransactionalLogUtil.crearMensajeLog(ConstantesComunes.TITULO_REPORTE_INFORMACION_OFICIAL),
                "",
                filtro.getCodigoCentroComputo(),
                0, 1);

        return Funciones.generarReporte(this.getClass(),lista,nombreReporte,parametrosReporte);
    }

    @Override
    public List<ReporteInformacionOficialDto> consultarInformacionOficial(ReporteInformacionOficialRequestDto filtro) {
        List<ReporteInformacionOficialDto> resultado = reporteInformacionOficialRepository.listarReporteInformacionOficial(filtro);
        this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE,
                this.getClass().getSimpleName(), "Se consultó el Reporte de Información Oficial.", "",
                filtro.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);
        return resultado;
    }

    private void mapearCamposCabeceraReporte(
            ReporteInformacionOficialRequestDto filtro, Map<String, Object> parametrosReporte) {


        parametrosReporte.put("p_ambitoElectoral", filtro.getCodigoAmbitoElectoral() + " - " + filtro.getAmbitoElectoral() );
        parametrosReporte.put("p_centroComputo", filtro.getCodigoCentroComputo() + " - " + filtro.getCentroComputo() );

        parametrosReporte.put("proceso", filtro.getProceso());
        parametrosReporte.put("p_nivelUbigeoUno", filtro.getUbigeoNivelUno()==null? DEFAULT_UBIGEO_VALUE :filtro.getUbigeoNivelUno());
        parametrosReporte.put("p_nivelUbigeoDos", filtro.getUbigeoNivelDos()==null? DEFAULT_UBIGEO_VALUE :filtro.getUbigeoNivelDos());
        parametrosReporte.put("p_nivelUbigeoTres", filtro.getUbigeoNivelTres()==null? DEFAULT_UBIGEO_VALUE :filtro.getUbigeoNivelTres());
        parametrosReporte.put("usuario", filtro.getUsuario());
        parametrosReporte.put("reporte", ConstantesReportes.NAME_REPORTE_INFORMACION_OFICIAL);
        parametrosReporte.put("version", utilSceService.getVersionSistema());
        parametrosReporte.put("tituloEleccionCompleto", filtro.getEleccion());
        parametrosReporte.put("nombreUbigeoUno", filtro.getNombreUbigeoUno());
        parametrosReporte.put("nombreUbigeoDos", filtro.getNombreUbigeoDos());
        parametrosReporte.put("nombreUbigeoTres", filtro.getNombreUbigeoTres());


    }
}
