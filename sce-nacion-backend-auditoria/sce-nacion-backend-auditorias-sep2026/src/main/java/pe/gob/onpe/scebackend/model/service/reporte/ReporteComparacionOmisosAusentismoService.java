package pe.gob.onpe.scebackend.model.service.reporte;

import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Service;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteComparacionOmisosAusentismoRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteComparacionOmisosAusentismoDto;
import pe.gob.onpe.scebackend.model.orc.repository.ReporteComparacionOmisosAusentismoRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesReportes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class ReporteComparacionOmisosAusentismoService implements IReporteComparacionOmisosAusentismoService {

    private final ITabLogTransaccionalService logService;
    private final ReporteComparacionOmisosAusentismoRepository reporteComparacionOmisosAusentismoRepository;
    private final UtilSceService utilSceService;

    public ReporteComparacionOmisosAusentismoService(ITabLogTransaccionalService logService,
                                                     ReporteComparacionOmisosAusentismoRepository reporteComparacionOmisosAusentismoRepository,
                                                     UtilSceService utilSceService){
        this.logService = logService;
        this.reporteComparacionOmisosAusentismoRepository = reporteComparacionOmisosAusentismoRepository;
        this.utilSceService = utilSceService;
    }

    @Override
    public byte[] reporteComparacionOmisosAusentismo(ReporteComparacionOmisosAusentismoRequestDto filtro) throws JRException{
        List<ReporteComparacionOmisosAusentismoDto> lista;
        Map<String, Object> parametrosReporte = new java.util.HashMap<>();
        String nombreReporte = "";
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC +  ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe
        parametrosReporte.put("url_imagen", imagen);
        parametrosReporte.put("sinValorOficial", this.utilSceService.getSinValorOficial(filtro.getIdProceso()));

        lista = this.reporteComparacionOmisosAusentismoRepository
        				.listarReporteComparacionOmisosAusentismo(filtro.getEsquema(),
        															filtro.getIdAmbito(),
        															filtro.getIdCentroComputo())
        				.stream()
        				.map(this::llenarDatos)
                        .sorted(Comparator.comparing(ReporteComparacionOmisosAusentismoDto::getCodigoUbigeo)
                                .thenComparing(ReporteComparacionOmisosAusentismoDto::getCodiLocal)
                                .thenComparing(ReporteComparacionOmisosAusentismoDto::isConDiferencias)
                                .thenComparing(ReporteComparacionOmisosAusentismoDto::getNumeroMesa))
                        .toList();
        
        parametrosReporte.put("tituloReporte", ConstantesComunes.TITULO_REPORTE_COMPARACION_OMISOS_AUSENTISMO);

        nombreReporte = ConstantesComunes.REPORTE_COMPARACION_OMISOS_AUSENTISMO;
        parametrosReporte.put("reporte", ConstantesReportes.NAME_REPORTE_COMPARACION_OMISOS_AUSENTISMO);

        mapearCamposCabeceraReporte(filtro, parametrosReporte);

        this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE,
                this.getClass().getSimpleName(), "Se consultó el Reporte de Omisos vs Ausentismo.", "",
                filtro.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);

        return Funciones.generarReporte(this.getClass(), lista, nombreReporte, parametrosReporte);
    }


    private void mapearCamposCabeceraReporte(
            ReporteComparacionOmisosAusentismoRequestDto filtro, Map<String, Object> parametrosReporte) {

        parametrosReporte.put("proceso", filtro.getProceso());
        parametrosReporte.put(ConstantesComunes.REPORT_PARAM_USUARIO, filtro.getUsuario());
        parametrosReporte.put(ConstantesComunes.REPORT_PARAM_VERSION, utilSceService.getVersionSistema());
        parametrosReporte.put("tituloEleccionCompleto", "");
        parametrosReporte.put("tipoComparacion", "Todos");

    }
    
    private ReporteComparacionOmisosAusentismoDto llenarDatos(Map<String, Object> mapCompara) {
        ReporteComparacionOmisosAusentismoDto reporte = new ReporteComparacionOmisosAusentismoDto();
        Long votOmisos = (Long) mapCompara.get("n_votantes_omisos");
        
        reporte.setCodiDesCompu((String) mapCompara.get("c_codigo_centro_computo"));
        reporte.setCodiDesOdpe((String) mapCompara.get("c_codigo_ambito_electoral"));
        reporte.setCodigoUbigeo((String) mapCompara.get("c_codigo_ubigeo"));
        reporte.setDescDepartamento((String) mapCompara.get("c_departamento"));
        reporte.setDescProvincia((String) mapCompara.get("c_provincia"));
        reporte.setDescDistrito((String) mapCompara.get("c_distrito"));
        reporte.setCodiLocal((String) mapCompara.get("c_codigo_local"));
        reporte.setNombLocal((String) mapCompara.get("c_nombre_local"));
        reporte.setDirLocal((String) mapCompara.get("c_direccion"));
        reporte.setNumeroMesa((String) mapCompara.get("c_mesa"));
        reporte.setNroMesaOmisa((Integer) mapCompara.get("n_mesa_omisa"));
        reporte.setVotantesHabiles((Integer) mapCompara.get("n_votantes_habiles"));
        reporte.setVotantesAusentes((Integer) mapCompara.get("n_votantes_ausentes"));
        reporte.setVotantesOmisos(votOmisos.intValue());
        reporte.setConDiferencias(reporte.getVotantesOmisos().compareTo(reporte.getVotantesAusentes()) == 0);

        return reporte;
    }
}
