package pe.gob.onpe.scebackend.model.service.reporte;

import lombok.extern.java.Log;
import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Service;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteProcedePagoRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteProcedePagoDto;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.ReporteProcedePagoRepository;
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
public class ReporteProcedePagoService implements IReporteProcedePagoService {

    private final ITabLogTransaccionalService logService;
    private final ReporteProcedePagoRepository reporteProcedePagoRepository;
    private final UtilSceService utilSceService;

    public ReporteProcedePagoService(ITabLogTransaccionalService logService, ReporteProcedePagoRepository reporteProcedePagoRepository, UtilSceService utilSceService) {
        this.logService = logService;
        this.reporteProcedePagoRepository = reporteProcedePagoRepository;
        this.utilSceService = utilSceService;
    }

    @Override
    public byte[] reporteProcedePago(ReporteProcedePagoRequestDto filtro) throws JRException{
        List<ReporteProcedePagoDto> lista;
        Map<String, Object> parametrosReporte = new java.util.HashMap<>();
        String nombreReporte = "";
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC +  ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe
        parametrosReporte.put("url_imagen", imagen);
        parametrosReporte.put("sinValorOficial", this.utilSceService.getSinValorOficial(filtro.getIdProceso()));

        lista = obtenerReporte(filtro);
        parametrosReporte.put("tituloReporte", ConstantesComunes.TITULO_REPORTE_PROCEDE_PAGO);

        nombreReporte = ConstantesComunes.REPORTE_PROCEDE_PAGO;
        parametrosReporte.put("reporte", ConstantesReportes.NAME_REPORTE_PROCEDE_PAGO);

        mapearCamposCabeceraReporte(filtro, parametrosReporte);

        this.logService.registrarLog(filtro.getUsuario(),
                                        Thread.currentThread().getStackTrace()[1].getMethodName(),
                                        this.getClass().getName(),
                                        TransactionalLogUtil.crearMensajeLog(ConstantesComunes.TITULO_REPORTE_PROCEDE_PAGO),
                                        "",
                                        filtro.getCodigoCentroComputo(),
                                        0, 1);

        return Funciones.generarReporte(this.getClass(), lista, nombreReporte, parametrosReporte);
    }


    public List<ReporteProcedePagoDto> obtenerReporte(ReporteProcedePagoRequestDto filtro) {
        String tipoReporte = filtro.getTipoReporte() == null ? null : filtro.getTipoReporte().equals(1) ? "SI" : "NO";

        return reporteProcedePagoRepository.listarReporteProcedePago(filtro.getEsquema(), tipoReporte)
                .stream()
                .map(p -> {
                    ReporteProcedePagoDto dto = new ReporteProcedePagoDto();
                    dto.setNro(p.getNro());
                    dto.setNumeroMesa(p.getNumeroMesa());
                    dto.setNumeroDocumento(p.getNumeroDocumento());
                    dto.setVotante(p.getVotante());
                    dto.setCargo(p.getCargo());
                    dto.setProcedePago(p.getProcedePago());
                    return dto;
                })
                .toList();
    }


    private void mapearCamposCabeceraReporte(
            ReporteProcedePagoRequestDto filtro, Map<String, Object> parametrosReporte) {

        parametrosReporte.put("proceso", filtro.getProceso());
        parametrosReporte.put("usuario", filtro.getUsuario());
        parametrosReporte.put("version", utilSceService.getVersionSistema());
        parametrosReporte.put("tituloEleccionCompleto", "");
        parametrosReporte.put("tipoReporte", "MIEMBROS DE MESA SEGÚN ACTA DE ESCRUTINIO");


    }
}
