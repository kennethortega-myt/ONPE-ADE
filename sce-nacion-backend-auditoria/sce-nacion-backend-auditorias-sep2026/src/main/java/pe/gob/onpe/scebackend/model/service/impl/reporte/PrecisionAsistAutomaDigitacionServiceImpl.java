package pe.gob.onpe.scebackend.model.service.impl.reporte;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroPrecisionAsistAutomaDigitacionDto;
import pe.gob.onpe.scebackend.model.dto.reportes.PrecisionAsistAutomaDigitacionDetalleDto;
import pe.gob.onpe.scebackend.model.dto.reportes.PrecisionAsistAutomaDigitacionResumenDto;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.IPrecisionAsistAutomaDigitacionRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.model.service.reporte.IPrecisionAsistAutomaDigitacionService;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes.*;

@Log4j2
@RequiredArgsConstructor
@Service
public class PrecisionAsistAutomaDigitacionServiceImpl implements IPrecisionAsistAutomaDigitacionService {

    private final IPrecisionAsistAutomaDigitacionRepository precisionAsistAutomaDigitacionRepository;
    private final ITabLogTransaccionalService logService;
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

            this.logService.registrarLog(filtro.getUsuario(), LOG_TRANSACCIONES_TIPO_REPORTE,
                    this.getClass().getSimpleName(), "Se consultó el Reporte de de precisión asistente automatizado en la digitación en resumen",
                    "", filtro.getCentroComputoCod(), LOG_TRANSACCIONES_AUTORIZACION_NO, LOG_TRANSACCIONES_ACCION);

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
                listaDetalle.sort(Comparator.comparing(PrecisionAsistAutomaDigitacionDetalleDto::getCentroComputoId));
                listaDetalle.forEach(obj -> obj.calculaPrecision(true));
            } else {
                listaDetalle = this.precisionAsistAutomaDigitacionRepository.listaPrecisionAsistAutomaDetallePref(filtro);
                listaDetalle.sort(Comparator.comparing(PrecisionAsistAutomaDigitacionDetalleDto::getCentroComputoId));
                listaDetalle.forEach(obj -> obj.calculaPrecision(false));
            }

            parametros.put(REPORT_PARAM_URL_IMAGE, imagen);
            parametros.put(REPORT_PARAM_PIXEL_TRANSPARENTE, pixelTransparente);
            parametros.put(REPORT_PARAM_SIN_VALOR_OFICIAL, utilSceService.getSinValorOficial(filtro.getIdProceso()));
            parametros.put(REPORT_PARAM_VERSION, utilSceService.getVersionSistema());
            parametros.put(REPORT_PARAM_USUARIO, filtro.getUsuario());
            parametros.put("tituloPrincipal", filtro.getProceso());
            parametros.put("centroComputo", filtro.getCentroComputo());
            parametros.put("eleccion", filtro.getEleccion());

            this.logService.registrarLog(filtro.getUsuario(), LOG_TRANSACCIONES_TIPO_REPORTE,
                    this.getClass().getSimpleName(), "Se consultó el Reporte de de precisión asistente automatizado en la digitación en detalle",
                    "", filtro.getCentroComputoCod(), LOG_TRANSACCIONES_AUTORIZACION_NO, LOG_TRANSACCIONES_ACCION);

            return Funciones.generarReporte(this.getClass(), listaDetalle, nombreReporte, parametros);
        } catch (Exception e) {
            log.error("excepcion", e);
            return null;
        }
    }
}
