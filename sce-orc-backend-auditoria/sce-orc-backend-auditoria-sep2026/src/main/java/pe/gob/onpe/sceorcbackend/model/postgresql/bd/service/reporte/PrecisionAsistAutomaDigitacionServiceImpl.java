package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroPrecisionAsistAutomaDigitacionDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.PrecisionAsistAutomaDigitacionDetalleDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.PrecisionAsistAutomaDigitacionResumenDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes.IPrecisionAsistAutomaDigitacionRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.utils.TransactionalLogUtil;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pe.gob.onpe.sceorcbackend.utils.ConstantesComunes.COD_ELEC_PRE;
import static pe.gob.onpe.sceorcbackend.utils.ConstantesReportes.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class PrecisionAsistAutomaDigitacionServiceImpl implements IPrecisionAsistAutomaDigitacionService {

    private final IPrecisionAsistAutomaDigitacionRepository precisionAsistAutomaDigitacionRepository;
    private final ITabLogService logService;
    private final UtilSceService utilSceService;

    @Override
    public byte[] getPrecisionAsistAutomaDigitacionResumen(FiltroPrecisionAsistAutomaDigitacionDto filtro) {
        try {
            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(PATH_IMAGE_COMMON_NAC + "onpe.jpg");
            InputStream pixelTransparente = this.getClass().getClassLoader().getResourceAsStream(PATH_IMAGE_COMMON_NAC + "pixel_transparente.png");
            Map<String, Object> parametros = new HashMap<>();

            List<PrecisionAsistAutomaDigitacionResumenDto> listaResumen = this.precisionAsistAutomaDigitacionRepository.listaPrecisionAsistAutomaResumen(filtro);
            parametros.put(REPORT_PARAM_URL_IMAGE, imagen);
            parametros.put(REPORT_PARAM_PIXEL_TRANSPARENTE, pixelTransparente);
            parametros.put(REPORT_PARAM_SIN_VALOR_OFICIAL, utilSceService.getSinValorOficial(filtro.getIdProceso()));
            parametros.put(REPORT_PARAM_VERSION, utilSceService.getVersionSistema());
            parametros.put(REPORT_PARAM_USUARIO, filtro.getUsuario());
            parametros.put("tituloPrincipal", filtro.getProceso());
            parametros.put("centroComputo", filtro.getCentroComputo());
            parametros.put("eleccion", filtro.getEleccion());
            String cc = filtro.getIdCentroComputo()!=null?filtro.getIdCentroComputo().toString():"";

            this.logService.registrarLog(filtro.getUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    this.getClass().getName(), TransactionalLogUtil.crearMensajeLog("precisión asistente automatizado en la digitación en resumen"),
                    cc, LOG_TRANSACCIONES_AUTORIZACION_NO, LOG_TRANSACCIONES_ACCION);

            return Funciones.generarReporte(this.getClass(), listaResumen, REPORTE_PRECISION_ASISTENTE_AUTOMATIZADO_DIGITACION_RESUMEN, parametros);
        } catch (Exception e) {
            log.error("excepcion", e);
            return null;
        }
    }

    @Override
    public byte[] getPrecisionAsistAutomaDigitacionDetalle(FiltroPrecisionAsistAutomaDigitacionDto filtro) {
        try {
            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(PATH_IMAGE_COMMON_NAC + "onpe.jpg");
            InputStream pixelTransparente = this.getClass().getClassLoader().getResourceAsStream(PATH_IMAGE_COMMON_NAC + "pixel_transparente.png");
            String nombreReporte = REPORTE_PRECISION_ASISTENTE_AUTOMATIZADO_DIGITACION_DETALLE_PRESIDENCIAL;
            if(!filtro.getIdEleccion().toString().equals(COD_ELEC_PRE)){nombreReporte = REPORTE_PRECISION_ASISTENTE_AUTOMATIZADO_DIGITACION_DETALLE_NO_PRESIDENCIAL;}
            Map<String, Object> parametros = new HashMap<>();

            List<PrecisionAsistAutomaDigitacionDetalleDto> listaDetalle;

            if(filtro.getIdEleccion().toString().equals(COD_ELEC_PRE)){
                listaDetalle = this.precisionAsistAutomaDigitacionRepository.listaPrecisionAsistAutomaDetalle(filtro);
                listaDetalle.forEach(obj -> {
                    double precision = ((double)(obj.getCoincideVotos()+obj.getCoincideCvas()+1)/(obj.getTotalVotos()+1+1))*100;
                    obj.setPresicionActa(BigDecimal.valueOf(precision).setScale(3, RoundingMode.HALF_UP).doubleValue());
                });
            } else {
                listaDetalle = this.precisionAsistAutomaDigitacionRepository.listaPrecisionAsistAutomaDetallePref(filtro);
                listaDetalle.forEach(obj -> {
                    double precision = ((double)(obj.getCoincideVotos()+obj.getCoincidePreferencial()+obj.getCoincideCvas()+1)/(obj.getTotalVotos()+obj.getTotalPreferencial()+1+1))*100;
                    obj.setPresicionActa(BigDecimal.valueOf(precision).setScale(3, RoundingMode.HALF_UP).doubleValue());
                });
            }

            parametros.put(REPORT_PARAM_URL_IMAGE, imagen);
            parametros.put(REPORT_PARAM_PIXEL_TRANSPARENTE, pixelTransparente);
            parametros.put(REPORT_PARAM_SIN_VALOR_OFICIAL, utilSceService.getSinValorOficial(filtro.getIdProceso()));
            parametros.put(REPORT_PARAM_VERSION, utilSceService.getVersionSistema());
            parametros.put(REPORT_PARAM_USUARIO, filtro.getUsuario());
            parametros.put("tituloPrincipal", filtro.getProceso());
            parametros.put("centroComputo", filtro.getCentroComputo());
            parametros.put("eleccion", filtro.getEleccion());

            String cc = filtro.getIdCentroComputo()!=null?filtro.getIdCentroComputo().toString():"";

            this.logService.registrarLog(filtro.getUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(),
                    this.getClass().getName(), TransactionalLogUtil.crearMensajeLog("precisión asistente automatizado en la digitación en detalle"),
                    cc, LOG_TRANSACCIONES_AUTORIZACION_NO, LOG_TRANSACCIONES_ACCION);

            return Funciones.generarReporte(this.getClass(), listaDetalle, nombreReporte, parametros);
        } catch (Exception e) {
            log.error("excepcion", e);
            return null;
        }
    }
}
