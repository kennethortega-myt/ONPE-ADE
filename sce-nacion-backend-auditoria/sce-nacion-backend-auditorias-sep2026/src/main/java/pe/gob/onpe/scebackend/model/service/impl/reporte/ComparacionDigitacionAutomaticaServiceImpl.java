package pe.gob.onpe.scebackend.model.service.impl.reporte;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import pe.gob.onpe.scebackend.model.dto.reportes.ComparacionDigitacionAutomaticaDto;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroComparacionDigitacionAutomaticaDto;
import pe.gob.onpe.scebackend.model.dto.request.comun.UbiEleccionAgrupolRequestDto;
import pe.gob.onpe.scebackend.model.orc.repository.comun.IUbiEleccionAgrupolRepositoryCustom;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.IComparacionDigitacionAutomaticaRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.model.service.reporte.IComparacionDigitacionAutomaticaService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes.*;
import static pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes.LOG_TRANSACCIONES_ACCION;

@Log4j2
@RequiredArgsConstructor
@Service
public class ComparacionDigitacionAutomaticaServiceImpl implements IComparacionDigitacionAutomaticaService {
    private final IUbiEleccionAgrupolRepositoryCustom ubiEleccionAgrupolRepositoryCustom;
    private final IComparacionDigitacionAutomaticaRepository comparacionDigitacionAutomaticaRepository;
    private final ITabLogTransaccionalService logService;
    private final UtilSceService utilSceService;

    @Override
    public byte[] getComparacionDigiAutoma(FiltroComparacionDigitacionAutomaticaDto filtro) {
        try {
            filtro.setMesa(filtro.getMesa() != null && filtro.getMesa().trim().isEmpty() ? null : filtro.getMesa());
            filtro.setDepartamento(filtro.getDepartamento() != null && filtro.getDepartamento().trim().isEmpty() ? null : filtro.getDepartamento());
            filtro.setProvincia(filtro.getProvincia() != null && filtro.getProvincia().trim().isEmpty() ? null : filtro.getProvincia());
            filtro.setDistrito(filtro.getDistrito() != null && filtro.getDistrito().trim().isEmpty() ? null : filtro.getDistrito());

            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(PATH_IMAGE_COMMON_NAC + "onpe.jpg");
            InputStream pixelTransparente = this.getClass().getClassLoader().getResourceAsStream(PATH_IMAGE_COMMON_NAC + "pixel_transparente.png");
            String nombreReporte = REPORTE_COMPARACION_DIGITACION_AUTOMATICA;
            Map<String, Object> parametros = new HashMap<>();

            List<ComparacionDigitacionAutomaticaDto> listaComparacion;

            parametros.put(REPORT_PARAM_URL_IMAGE, imagen);
            parametros.put(REPORT_PARAM_PIXEL_TRANSPARENTE, pixelTransparente);
            parametros.put(REPORT_PARAM_SIN_VALOR_OFICIAL, utilSceService.getSinValorOficial(filtro.getIdProceso()));
            parametros.put(REPORT_PARAM_VERSION, utilSceService.getVersionSistema());
            parametros.put(REPORT_PARAM_USUARIO, filtro.getUsuario());
            parametros.put("tituloPrincipal", filtro.getProceso());
            parametros.put("centroComputo", filtro.getCentroComputo());
            parametros.put("eleccion", filtro.getEleccion());

            if ( filtro.getIdEleccion().equals(ELECCION_PRESIDENCIAL) ) {
                listaComparacion= this.comparacionDigitacionAutomaticaRepository.listaComparacionDigitacionAutomatica(filtro);
            } else {
                listaComparacion= this.comparacionDigitacionAutomaticaRepository.listaComparacionDigitacionAutomaticaPref(filtro);

                Integer cantidadAgrupacionPreferencial;
                if (ELECCION_PARLAMENTO_ANDINO.equals(filtro.getIdEleccion())) {
                    cantidadAgrupacionPreferencial = CANTIDAD_VOTOS_PREFERENCIALES_PARLAMENTO;
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
                    nombreReporte = ConstantesComunes.REPORTE_COMPARACION_DIGITACION_AUTOMATICA_PREF_9;
                } else if (cantidadAgrupacionPreferencial <= 16) {
                    nombreReporte = ConstantesComunes.REPORTE_COMPARACION_DIGITACION_AUTOMATICA_PREF_16;
                } else {
                    nombreReporte = ConstantesComunes.REPORTE_COMPARACION_DIGITACION_AUTOMATICA_PREF_32;
                }
            }

            this.logService.registrarLog(filtro.getUsuario(), LOG_TRANSACCIONES_TIPO_REPORTE,
                        this.getClass().getSimpleName(), "Se consultó el Reporte de comparción digitacion automatica",
                        filtro.getAmbito(), filtro.getCentroComputo(), LOG_TRANSACCIONES_AUTORIZACION_NO, LOG_TRANSACCIONES_ACCION);

            return Funciones.generarReporte(this.getClass(), listaComparacion, nombreReporte, parametros);
        } catch (Exception e) {
            log.error("excepcion", e);
            return new byte[0];
        }
    }
}
