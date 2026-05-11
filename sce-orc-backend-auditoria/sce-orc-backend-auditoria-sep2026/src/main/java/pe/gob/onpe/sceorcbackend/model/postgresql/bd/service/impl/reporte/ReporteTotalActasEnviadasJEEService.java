package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.JRException;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteTotalEnviadaJEERequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.ReporteTotalActasEnviadasJEEDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.IReporteTotalActasEnviadasJEERepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IReporteTotalActasEnviadasJEEService;
import pe.gob.onpe.sceorcbackend.security.TokenDecoder;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

@Log4j2
@Service
public class ReporteTotalActasEnviadasJEEService implements IReporteTotalActasEnviadasJEEService {
	
    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;
    
    @Autowired
    IReporteTotalActasEnviadasJEERepository reporteTotalActasEnviadasJEERepository;
    
    @Autowired
    private ITabLogService logService;
    
    @Autowired
    private TokenDecoder tokenDecoder;

    @Autowired
    private UtilSceService utilSceService;

	@Override
	public byte[] reporte(ReporteTotalEnviadaJEERequestDto filtro, String authorization)
			throws JRException, SQLException {
		filtro.setEsquema(schema);
        List<ReporteTotalActasEnviadasJEEDto> lista = reporteTotalActasEnviadasJEERepository.listarReporteTotalActasEnviadasJEE(filtro);

        Map<String, Object> parametros = new java.util.HashMap<>();
        parametros.put("tituloGeneral", filtro.getProceso());
        parametros.put("tituloRep", "TOTAL DE ACTAS ENVIADAS AL JEE POR CENTRO DE CÓMPUTO");
        parametros.put("sinValorOficial", utilSceService.getSinValorOficial());
        parametros.put("versionSuite", utilSceService.getVersionSistema());
        parametros.put("usuario", filtro.getUsuario());
        parametros.put("codigoAmbitoElectoral", filtro.getCodigoAmbitoElectoral() + " - " + filtro.getNombreAmbitoElectoral());
        parametros.put("reporte", "ReporteTotalActasEnviadasJEE");
        parametros.put("nombreCentroComputo", filtro.getCodigoCentroComputo() + " - " + filtro.getNombreCentroComputo());
        parametros.put("nombreCortoEleccion", filtro.getNombreEleccion());
        InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON + ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe
        parametros.put("logo_onpe", imagen);

        String token = authorization.substring(SceConstantes.LENGTH_BEARER);
        Claims claims = this.tokenDecoder.decodeToken(token);
        String usuarioLogin = claims.get("usr", String.class);
        String usuarioLoginCentroComputo = claims.get("ccc", String.class);

        try {
            this.logService.registrarLog(usuarioLogin,Thread.currentThread().getStackTrace()[1].getMethodName(),
            		this.getClass().getSimpleName(), "Se consultó el Reporte Total de Actas enviadas al JEE", usuarioLoginCentroComputo,
                    ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);
        } catch (Exception e) {
            log.error("Error al registra log de Reporte de Actas enviadas al JEE: ", e);
        }

        if(lista.isEmpty()) {
            return null;
        }
        String nombreReporteFisico = ConstantesComunes.REPORTE_TOTAL_ACTAS_ENVIADAS_JEE_CENTROCOMPUTO;
        return Funciones.generarReporte(this.getClass(),lista,nombreReporteFisico,parametros);
    }
}
