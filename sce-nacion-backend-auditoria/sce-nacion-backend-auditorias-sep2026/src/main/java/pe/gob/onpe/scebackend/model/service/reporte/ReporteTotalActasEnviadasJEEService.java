package pe.gob.onpe.scebackend.model.service.reporte;

import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteTotalEnviadaJEERequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteTotalActasEnviadasJEEDto;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.IReporteTotalActasEnviadasJEERepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class ReporteTotalActasEnviadasJEEService implements IReporteTotalActasEnviadasJEEService {

    @Autowired
    IReporteTotalActasEnviadasJEERepository reporteTotalActasEnviadasJEERepository;

    @Autowired
    private UtilSceService utilSceService;

    @Autowired
    private TokenDecoder tokenDecoder;

    @Autowired
    private ITabLogTransaccionalService logService;

    @Override
    @Transactional("locationTransactionManager")
    public byte[] reporte(ReporteTotalEnviadaJEERequestDto filtro, String authorization) throws JRException, SQLException {
        List<ReporteTotalActasEnviadasJEEDto> lista = reporteTotalActasEnviadasJEERepository.listarReporteTotalActasEnviadasJEE(filtro);

        Map<String, Object> parametros = new java.util.HashMap<>();
        parametros.put("tituloGeneral", filtro.getProceso());
        parametros.put("tituloRep", "TOTAL DE ACTAS ENVIADAS AL JEE POR CENTRO DE CÓMPUTO");
        parametros.put("sinValorOficial", utilSceService.getSinValorOficial(filtro.getIdProceso()));
        parametros.put("versionSuite", utilSceService.getVersionSistema());
        parametros.put("usuario", filtro.getUsuario());
        parametros.put("codigoAmbitoElectoral", filtro.getCodigoAmbitoElectoral() + " - " + filtro.getNombreAmbitoElectoral());
        parametros.put("reporte", "ReporteTotalActasEnviadasJEE");
        parametros.put("nombreCentroComputo", filtro.getCodigoCentroComputo() + " - " + filtro.getNombreCentroComputo());
        parametros.put("nombreCortoEleccion", filtro.getNombreEleccion());
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe
        parametros.put("logo_onpe", imagen);

        String token = authorization.substring(ConstantesComunes.LENGTH_BEARER);
        Claims claims = this.tokenDecoder.decodeToken(token);
        String usuarioLogin = claims.get("usr", String.class);

        try {
            this.logService.registrarLog(usuarioLogin,ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE, this.getClass().getSimpleName(), "Se consultó el Reporte Total de Actas enviadas al JEE",
                    ConstantesComunes.CC_NACION_DESCRIPCION, filtro.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);
        } catch (Exception e) {
            log.error("Error al registra log de Reporte de avance de registros por ubigeo: ", e);
        }

        if(lista.isEmpty()) {
            return null;
        }

        String nombreReporteFisico = ConstantesComunes.REPORTE_TOTAL_ACTAS_ENVIADAS_JEE_CENTROCOMPUTO;

        return Funciones.generarReporte(this.getClass(),lista,nombreReporteFisico,parametros);
    }
}
