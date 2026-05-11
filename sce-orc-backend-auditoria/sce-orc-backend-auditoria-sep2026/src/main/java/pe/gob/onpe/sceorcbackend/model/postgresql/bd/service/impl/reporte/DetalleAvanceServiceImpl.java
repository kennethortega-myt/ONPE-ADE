package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.DetalleAvancePersonerosDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroDetalleAvanceDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.DetalleAvanceMiembrosMesaEscrutinioDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes.IDetalleAvanceRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte.IDetalleAvanceService;
import pe.gob.onpe.sceorcbackend.security.TokenDecoder;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesReportes;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class DetalleAvanceServiceImpl implements IDetalleAvanceService {
    @Autowired
    private IDetalleAvanceRepository detalleAvanceRepository;

    @Autowired
    private ITabLogService logService;

    @Autowired
    private TokenDecoder tokenDecoder;

    @Autowired
    private UtilSceService utilSceService;

    @Value("${spring.jpa.properties.hibernate.default_schema}")
    private String schema;

    @Override
    public byte[] reporteMiembrosMesaEscrutinio(FiltroDetalleAvanceDto filtro, String authorization) throws JRException, SQLException {
        try {
            String ubigeo = obtenerUbigeo(filtro.getUbigeoNivelUno(), filtro.getUbigeoNivelDos(), filtro.getUbigeoNivelTres());
            filtro.setUbigeo(ubigeo);
            filtro.setEsquema(schema);

            List<DetalleAvanceMiembrosMesaEscrutinioDto> lista = detalleAvanceRepository.listaDetalleAvanceMiembrosMesaEscrutinio(filtro);
            Map<String, Object> parametros = new java.util.HashMap<>();
            parametros.put("tituloGeneral", filtro.getProceso());
            parametros.put("tituloRep", "DETALLE DE AVANCE MIEMBROS DE MESA ESCRUTINIO");
            parametros.put("sinValorOficial", utilSceService.getSinValorOficial(filtro.getIdProceso()));
            parametros.put("versionSuite", utilSceService.getVersionSistema());
            parametros.put("usuario", filtro.getUsuario());
            parametros.put("nombreReporte", "DetalleAvanceMiembrosMesaEscrutinio");
            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe
            parametros.put("logo_onpe", imagen);

            String nombreReporteFisico = ConstantesComunes.REPORTE_DETALLE_AVANCE_MIEMBROS_MESA_ESCRUTINIO;

            String token = authorization.substring(SceConstantes.LENGTH_BEARER);
            Claims claims = this.tokenDecoder.decodeToken(token);
            String usuarioLoginCentroComputo = claims.get("ccc", String.class);
            String usuarioLogin = claims.get("usr", String.class);

            this.logService.registrarLog(usuarioLogin,Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getSimpleName(), "Se consultó el Reporte Detalle De Avance Miembros De Mesa Escrutinio",
                    usuarioLoginCentroComputo, ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);

            return Funciones.generarReporte(this.getClass(), lista, nombreReporteFisico, parametros);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    @Override
    public byte[] reportePersoneros(FiltroDetalleAvanceDto filtro, String authorization) throws JRException, SQLException {
        try {
            String ubigeo = obtenerUbigeo(filtro.getUbigeoNivelUno(), filtro.getUbigeoNivelDos(), filtro.getUbigeoNivelTres());
            filtro.setUbigeo(ubigeo);
            filtro.setEsquema(schema);

            List<DetalleAvancePersonerosDto> lista = detalleAvanceRepository.listaDetalleAvancePersoneros(filtro);
            Map<String, Object> parametros = new java.util.HashMap<>();
            parametros.put("tituloGeneral", filtro.getProceso());
            parametros.put("tituloRep", "DETALLE DE AVANCE PERSONEROS");
            parametros.put("sinValorOficial", utilSceService.getSinValorOficial(filtro.getIdProceso()));
            parametros.put("versionSuite", utilSceService.getVersionSistema());
            parametros.put("usuario", filtro.getUsuario());
            parametros.put("nombreReporte", "DetalleAvancePersoneros");
            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + ConstantesComunes.NOMBRE_LOGO_ONPE);//logo onpe
            parametros.put("logo_onpe", imagen);

            String nombreReporteFisico = ConstantesComunes.REPORTE_DETALLE_AVANCE_PERSONEROS;

            String token = authorization.substring(SceConstantes.LENGTH_BEARER);
            Claims claims = this.tokenDecoder.decodeToken(token);
            String usuarioLoginCentroComputo = claims.get("ccc", String.class);
            String usuarioLogin = claims.get("usr", String.class);

            this.logService.registrarLog(usuarioLogin,Thread.currentThread().getStackTrace()[1].getMethodName(), this.getClass().getSimpleName(), "Se consultó el Reporte Detalle De Avance Personeros",
                    usuarioLoginCentroComputo, ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);

            return Funciones.generarReporte(this.getClass(), lista, nombreReporteFisico, parametros);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    private String obtenerUbigeo(String ubigeoNivelUno,String ubigeoNivelDos,String ubigeoNivelTres) {
        String ubigeRetorno = SceConstantes.UBIGEO_NACION;
        if(!SceConstantes.UBIGEO_NACION.equals(ubigeoNivelTres)) {
            ubigeRetorno = ubigeoNivelTres;
        }
        else if(!SceConstantes.UBIGEO_NACION.equals(ubigeoNivelDos)) {
            ubigeRetorno = ubigeoNivelDos;
        }
        else if(!SceConstantes.UBIGEO_NACION.equals(ubigeoNivelUno)) {
            ubigeRetorno = ubigeoNivelUno;
        }
        return ubigeRetorno;
    }
}
