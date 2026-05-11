package pe.gob.onpe.scebackend.model.service.impl.reporte;

import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.scebackend.model.dto.reportes.DetalleAvancePersonerosDto;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroDetalleAvanceDto;
import pe.gob.onpe.scebackend.model.dto.reportes.DetalleAvanceMiembrosMesaEscrutinioDto;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.IDetalleAvanceRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.model.service.reporte.IDetalleAvanceService;
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
public class DetalleAvanceServiceImpl implements IDetalleAvanceService {
    @Autowired
    private IDetalleAvanceRepository detalleAvanceRepository;

    @Autowired
    private ITabLogTransaccionalService logService;

    @Autowired
    private TokenDecoder tokenDecoder;

    @Autowired
    private UtilSceService utilSceService;

    @Override
    @Transactional("locationTransactionManager")
    public byte[] reporteMiembrosMesaEscrutinio(FiltroDetalleAvanceDto filtro, String authorization) throws JRException, SQLException {
        try {
            String ubigeo = obtenerUbigeo(filtro.getUbigeoNivelUno(), filtro.getUbigeoNivelDos(), filtro.getUbigeoNivelTres());
            filtro.setUbigeo(ubigeo);

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

            String token = authorization.substring(ConstantesComunes.LENGTH_BEARER);
            Claims claims = this.tokenDecoder.decodeToken(token);
            String usuarioLogin = claims.get("usr", String.class);

            this.logService.registrarLog(usuarioLogin,ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE, this.getClass().getSimpleName(), "Se consultó el Reporte Detalle De Avance Miembros De Mesa Escrutinio",
                    ConstantesComunes.CC_NACION_DESCRIPCION, filtro.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);

            return Funciones.generarReporte(this.getClass(), lista, nombreReporteFisico, parametros);
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        return null;
    }

    @Override
    @Transactional("locationTransactionManager")
    public byte[] reportePersoneros(FiltroDetalleAvanceDto filtro, String authorization) throws JRException, SQLException {
        try {
            String ubigeo = obtenerUbigeo(filtro.getUbigeoNivelUno(), filtro.getUbigeoNivelDos(), filtro.getUbigeoNivelTres());
            filtro.setUbigeo(ubigeo);

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

            String token = authorization.substring(ConstantesComunes.LENGTH_BEARER);
            Claims claims = this.tokenDecoder.decodeToken(token);
            String usuarioLogin = claims.get("usr", String.class);

            this.logService.registrarLog(usuarioLogin,ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE, this.getClass().getSimpleName(), "Se consultó el Reporte Detalle De Avance Personeros",
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
