package pe.gob.onpe.scebackend.model.service.reporte;

import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.dto.request.reporte.ReporteDetalleAvanceRegistroUbigeoRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.ReporteDetalleAvanceRegistroUbigeoDto;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.IReporteDetalleAvanceRegistroUbigeoRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.security.jwt.TokenDecoder;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesReportes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class ReporteDetalleAvanceRegistroUbigeoService implements IReporteDetalleAvanceRegistroUbigeoService {

    @Autowired
    IReporteDetalleAvanceRegistroUbigeoRepository reporteDetalleAvanceRegistroUbigeoRepository;

    @Autowired
    private ITabLogTransaccionalService logService;

    @Autowired
    private TokenDecoder tokenDecoder;

    @Autowired
    private UtilSceService utilSceService;

    @Override
    @Transactional("locationTransactionManager")
    public byte[] reporte(ReporteDetalleAvanceRegistroUbigeoRequestDto filtro, String authorization) throws JRException, SQLException {

        try {
            String ubigeo = obtenerUbigeo(filtro.getUbigeoNivelUno(), filtro.getUbigeoNivelDos(), filtro.getUbigeoNivelTres());
            filtro.setUbigeo(ubigeo);

            List<ReporteDetalleAvanceRegistroUbigeoDto> lista = reporteDetalleAvanceRegistroUbigeoRepository.listarReporteDetalleAvanceRegistroUbigeoElector(filtro);
            Map<String, Object> parametros = new java.util.HashMap<>();
            parametros.put("tituloGeneral", filtro.getProceso());
            parametros.put("tituloRep", "DETALLE DE AVANCE DE REGISTROS POR UBIGEO (SOLO VOTANTES)");
            parametros.put("sinValorOficial", utilSceService.getSinValorOficial(filtro.getIdProceso()));
            parametros.put("versionSuite", utilSceService.getVersionSistema());
            parametros.put("usuario", filtro.getUsuario());
            parametros.put("nombreReporte", "DetalleAvanceRegistroUbigeo");
            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe
            parametros.put("logo_onpe", imagen);

            String nombreReporteFisico = ConstantesComunes.REPORTE_DETALLE_AVANCE_REGISTRO_UBIGEO;

            String token = authorization.substring(ConstantesComunes.LENGTH_BEARER);
            Claims claims = this.tokenDecoder.decodeToken(token);
            String usuarioLogin = claims.get("usr", String.class);

            this.logService.registrarLog(usuarioLogin,ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE, this.getClass().getSimpleName(), "Se consultó el Reporte Detalle de Avance de Registros por Ubigeo",
                    ConstantesComunes.CC_NACION_DESCRIPCION, filtro.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);

            return Funciones.generarReporte(this.getClass(), lista, nombreReporteFisico, parametros);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    private String obtenerUbigeo(String ubigeoNivelUno,String ubigeoNivelDos,String ubigeoNivelTres) {
        String ubigeRetorno = ConstantesReportes.CODIGO_UBIGEO_NACION;
        if(!ConstantesReportes.CODIGO_UBIGEO_NACION.equals(ubigeoNivelTres)) {
            ubigeRetorno = ubigeoNivelTres;
        }
        else if(!ConstantesReportes.CODIGO_UBIGEO_NACION.equals(ubigeoNivelDos)) {
            ubigeRetorno = ubigeoNivelDos;
        }
        else if(!ConstantesReportes.CODIGO_UBIGEO_NACION.equals(ubigeoNivelUno)) {
            ubigeRetorno = ubigeoNivelUno;
        }
        return ubigeRetorno;
    }
}
