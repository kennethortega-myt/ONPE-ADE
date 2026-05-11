package pe.gob.onpe.scebackend.model.service.reporte;

import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;
import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteTransaccionesRealizadasRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ListaUsuariosReporteTransaccionesDTO;
import pe.gob.onpe.scebackend.model.orc.projections.ReporteTransaccionesRealizadasProjection;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.ReporteTransaccionesRealizadasRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;
import java.io.InputStream;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class ReporteTransaccionesRealizadasService implements IReporteTransaccionesRealizadasService{

    private final ReporteTransaccionesRealizadasRepository reporteTransaccionesRealizadasRepository;
    private final ITabLogTransaccionalService logService;
    private final TokenDecoder tokenDecoder;
    private final UtilSceService utilSceService;

    public ReporteTransaccionesRealizadasService(
            ReporteTransaccionesRealizadasRepository reporteTransaccionesRealizadasRepository,
            ITabLogTransaccionalService logService,
            TokenDecoder tokenDecoder,
            UtilSceService utilSceService
    ) {
        this.reporteTransaccionesRealizadasRepository = reporteTransaccionesRealizadasRepository;
        this.logService = logService;
        this.tokenDecoder = tokenDecoder;
        this.utilSceService = utilSceService;
    }



    @Override
    public List<ListaUsuariosReporteTransaccionesDTO> listaUsuarios(ReporteTransaccionesRealizadasRequestDto filtro) {
        try {
            return reporteTransaccionesRealizadasRepository.listarUsuarios(filtro.getEsquema()).stream().map(str->new ListaUsuariosReporteTransaccionesDTO(str, str))
                    .toList();
        }catch(Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return Collections.emptyList();
    }

    @Override
    public byte[] reporte(ReporteTransaccionesRealizadasRequestDto filtro, String authorization) throws JRException, SQLException {
        try {
            String token = authorization.substring(ConstantesComunes.LENGTH_BEARER);
            Claims claims = this.tokenDecoder.decodeToken(token);
            String usuarioLogin = claims.get("usr", String.class);

            int conAutorizacion = ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO;

            if (filtro.getSoloConAutorizacion().equals(Boolean.TRUE)) {
                conAutorizacion = ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_SI;
            }

            LocalDateTime formatFechaDesde = getFechaDesde(filtro);
            LocalDateTime formatFechaHasta = getFechaHasta(filtro);

            List<ReporteTransaccionesRealizadasProjection> lista = reporteTransaccionesRealizadasRepository.listarReporteTransaccionesRealizadas(
                    filtro.getEsquema(), filtro.getIdCentroComputo(), filtro.getUsuario(),formatFechaDesde , formatFechaHasta,conAutorizacion);
            Map<String, Object> parametros = new java.util.HashMap<>();
            parametros.put("tituloGeneral", filtro.getProceso());
            parametros.put("tituloRep", "TRANSACCIONES REALIZADAS");
            parametros.put("sinValorOficial", utilSceService.getSinValorOficial(filtro.getIdProceso()));
            parametros.put("versionSuite", utilSceService.getVersionSistema());
            parametros.put("usuarioVisualizacion", usuarioLogin);
            parametros.put("usuario", filtro.getUsuario()==null?"TODOS":filtro.getUsuario());
            parametros.put("nombreCentroComputo", filtro.getCodigoCentroComputo() + " - " + filtro.getNombreCentroComputo());
            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe
            parametros.put("logo_onpe", imagen);

            String nombreReporteFisico = ConstantesComunes.REPORTE_TRANSACCIONES_REALIZADAS;

            this.logService.registrarLog(usuarioLogin, ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE, this.getClass().getSimpleName(), "Se consultó el Reporte de Transacciones Realizadas.",
                        "", filtro.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);


            return Funciones.generarReporte(this.getClass(), lista, nombreReporteFisico, parametros);
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
        return new byte[0];
    }


    LocalDateTime getFechaDesde(ReporteTransaccionesRealizadasRequestDto filtro){
        if(filtro.getFechaDesde()!=null && filtro.getHoraDesde()!=null && !filtro.getHoraDesde().isEmpty()) {
            LocalDate fechaDesde = filtro.getFechaDesde().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String[] horaDesde = filtro.getHoraDesde().split(ConstantesComunes.DOS_PUNTOS);
            return LocalDateTime.of(fechaDesde.getYear(), fechaDesde.getMonth(), fechaDesde.getDayOfMonth(), Integer.parseInt(horaDesde[0]), Integer.parseInt(horaDesde[1]), 0);
        }else if (filtro.getFechaDesde() != null) {
            LocalDate fechaDesde = filtro.getFechaDesde().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return LocalDateTime.of(fechaDesde.getYear(), fechaDesde.getMonth(), fechaDesde.getDayOfMonth(), 0, 0, 0);
        }
        return null;
    }

    LocalDateTime getFechaHasta(ReporteTransaccionesRealizadasRequestDto filtro){
        if(filtro.getFechaHasta()!=null && filtro.getHoraHasta()!=null && !filtro.getHoraHasta().isEmpty()) {
            LocalDate fechaHasta = filtro.getFechaHasta().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String[] horaHasta = filtro.getHoraHasta().split(ConstantesComunes.DOS_PUNTOS);
            return LocalDateTime.of(fechaHasta.getYear(), fechaHasta.getMonth(), fechaHasta.getDayOfMonth(), Integer.parseInt(horaHasta[0]), Integer.parseInt(horaHasta[1]), 0);
        }else if(filtro.getFechaHasta() != null) {
            LocalDate fechaHasta = filtro.getFechaHasta().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            return LocalDateTime.of(fechaHasta.getYear(), fechaHasta.getMonth(), fechaHasta.getDayOfMonth(), 23, 59, 59);
        }
        return null;
    }



}
