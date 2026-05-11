package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.reporte;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ComparacionDigitacionAutomaticaDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroComparacionDigitacionAutomaticaDto;
import pe.gob.onpe.sceorcbackend.model.dto.request.UbiEleccionAgrupolRequestDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.IUbiEleccionAgrupolRepositoryCustom;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes.IComparacionDigitacionAutomaticaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import static pe.gob.onpe.sceorcbackend.utils.ConstantesComunes.*;

import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.ConstantesReportes;
import pe.gob.onpe.sceorcbackend.utils.TransactionalLogUtil;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

@Log4j2
@RequiredArgsConstructor
@Service
public class ComparacionDigitacionAutomaticaServiceImpl implements IComparacionDigitacionAutomaticaService {
    private final IUbiEleccionAgrupolRepositoryCustom ubiEleccionAgrupolRepositoryCustom;
    private final IComparacionDigitacionAutomaticaRepository comparacionDigitacionAutomaticaRepository;
    private final ITabLogService logService;
    private final UtilSceService utilSceService;

    @Override
    public byte[]
    getComparacionDigiAutoma(FiltroComparacionDigitacionAutomaticaDto filtro) throws JRException {
            filtro.setMesa(filtro.getMesa() != null && filtro.getMesa().trim().isEmpty() ? null : filtro.getMesa());
            filtro.setDepartamento(filtro.getDepartamento() != null && filtro.getDepartamento().trim().isEmpty() ? null : filtro.getDepartamento());
            filtro.setProvincia(filtro.getProvincia() != null && filtro.getProvincia().trim().isEmpty() ? null : filtro.getProvincia());
            filtro.setDistrito(filtro.getDistrito() != null && filtro.getDistrito().trim().isEmpty() ? null : filtro.getDistrito());

            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesReportes.PATH_IMAGE_COMMON_NAC + "onpe.jpg");
            InputStream pixelTransparente = this.getClass().getClassLoader().getResourceAsStream(ConstantesReportes.PATH_IMAGE_COMMON_NAC + "pixel_transparente.png");
            String nombreReporte = ConstantesReportes.REPORTE_COMPARACION_DIGITACION_AUTOMATICA;
            Map<String, Object> parametros = new HashMap<>();
            List<ComparacionDigitacionAutomaticaDto> listaComparacion;

            parametros.put(ConstantesComunes.REPORT_PARAM_URL_IMAGE, imagen);
            parametros.put(ConstantesComunes.REPORT_PARAM_PIXEL_TRANSPARENTE, pixelTransparente);
            parametros.put(ConstantesComunes.REPORT_PARAM_SIN_VALOR_OFICIAL, utilSceService.getSinValorOficial(filtro.getIdProceso()));
            parametros.put(ConstantesComunes.REPORT_PARAM_VERSION, utilSceService.getVersionSistema());
            parametros.put(ConstantesComunes.REPORT_PARAM_USUARIO, filtro.getUsuario());
            parametros.put("tituloPrincipal", filtro.getProceso());
            parametros.put("centroComputo", filtro.getCentroComputo());
            parametros.put("eleccion", filtro.getEleccion());

            if ( filtro.getIdEleccion().equals(ELECCION_PRESIDENCIAL) ) {
                listaComparacion= this.comparacionDigitacionAutomaticaRepository.listaComparacionDigitacionAutomatica(filtro);
            } else {
                listaComparacion= this.comparacionDigitacionAutomaticaRepository.listaComparacionDigitacionAutomaticaPref(filtro);

                Integer cantidadAgrupacionPreferencial;
                if (ELECCION_PARLAMENTO_ANDINO.equals(filtro.getIdEleccion())) {
                    cantidadAgrupacionPreferencial = ConstantesComunes.CANTIDAD_VOTOS_PREFERENCIALES_PARLAMENTO;
                } else {
                    UbiEleccionAgrupolRequestDto ubiEleccionAgrupolRequestDto = new UbiEleccionAgrupolRequestDto();
                    ubiEleccionAgrupolRequestDto.setIdEleccion(filtro.getIdEleccion());
                    ubiEleccionAgrupolRequestDto.setEsquema(filtro.getEsquema());
                    ubiEleccionAgrupolRequestDto.setUbigeo(filtro.getUbigeo());
                    ubiEleccionAgrupolRequestDto.setMesa(filtro.getMesa());

                    cantidadAgrupacionPreferencial = ubiEleccionAgrupolRepositoryCustom.obtenerCantidadAgrupacionPreferencial(ubiEleccionAgrupolRequestDto);
                }

                parametros.put("cantidadColumna", cantidadAgrupacionPreferencial);
                if (cantidadAgrupacionPreferencial <= 9) {
                    nombreReporte = ConstantesReportes.REPORTE_COMPARACION_DIGITACION_AUTOMATICA_PREF_9;
                } else if (cantidadAgrupacionPreferencial <= 16) {
                    nombreReporte = ConstantesReportes.REPORTE_COMPARACION_DIGITACION_AUTOMATICA_PREF_16;
                } else {
                    nombreReporte = ConstantesReportes.REPORTE_COMPARACION_DIGITACION_AUTOMATICA_PREF_32;
                }
            }

            this.logService.registrarLog(filtro.getUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(), 
                    this.getClass().getName(), TransactionalLogUtil.crearMensajeLog("comparción digitacion automatica"),
                    filtro.getCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);

            return Funciones.generarReporte(this.getClass(), listaComparacion, nombreReporte, parametros);
    }
}
