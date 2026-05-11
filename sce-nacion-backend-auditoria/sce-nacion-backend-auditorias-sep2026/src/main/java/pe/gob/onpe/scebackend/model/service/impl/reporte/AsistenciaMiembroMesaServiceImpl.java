package pe.gob.onpe.scebackend.model.service.impl.reporte;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.dto.request.reporte.AsistenciaMiembroMesaRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.AsistenciaMiembroMesaResponseDto;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.IAsistenciaMiembrosMesaRepository;
import pe.gob.onpe.scebackend.model.service.AsistenciaMiembroMesaService;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

@Service
public class AsistenciaMiembroMesaServiceImpl implements AsistenciaMiembroMesaService {

    Logger logger = LoggerFactory.getLogger(AsistenciaMiembroMesaServiceImpl.class);

    private final IAsistenciaMiembrosMesaRepository asistenciaMiembrosMesaRepository;
    private final ITabLogTransaccionalService logService;
    private final UtilSceService utilSceService;

    public AsistenciaMiembroMesaServiceImpl(IAsistenciaMiembrosMesaRepository asistenciaMiembrosMesaRepository,
            ITabLogTransaccionalService logService, UtilSceService utilSceService) {
        this.asistenciaMiembrosMesaRepository = asistenciaMiembrosMesaRepository;
        this.logService = logService;
        this.utilSceService = utilSceService;
    }

    @Override
    @Transactional("locationTransactionManager")
    public byte[] getReporteAsistenciaMiembroMesa(AsistenciaMiembroMesaRequestDto filtro) {
        try {
            List<AsistenciaMiembroMesaResponseDto> listaActas = this.asistenciaMiembrosMesaRepository
                    .listaAsistenciaMM(filtro);

            return generarReporteAsistenciaMiembroMesa(filtro, listaActas);
        } catch (Exception e) {
            logger.error("excepcion", e);
            return new byte[0];
        }
    }

    private byte[] generarReporteAsistenciaMiembroMesa(AsistenciaMiembroMesaRequestDto filtro,
            List<AsistenciaMiembroMesaResponseDto> listaActas) {
        String nombreReporte = ConstantesComunes.REPORTE_ASISTENCIA_MIEMBROS_MESA;

        Map<String, Object> parametros = new java.util.HashMap<>();

        try (InputStream imagen = this.getClass().getClassLoader()
                .getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + "onpe.jpg")) {
            parametros.put("imagen", imagen);
            parametros.put("sinvaloroficial", utilSceService.getSinValorOficial(filtro.getIdProceso()));
            parametros.put("versionSuite", utilSceService.getVersionSistema());
            parametros.put("viewUsuario", filtro.getUsuario());
            parametros.put("tituloGeneral", filtro.getProceso());
            parametros.put("tituloRep", "LISTA DE ASISTENCIA DE MIEMBROS DE MESA");
            parametros.put("prefUbigeoExtranjero", ConstantesComunes.PREFIJO_UBIGEO_EXTRANJEROS);

            this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE,
                    this.getClass().getSimpleName(), "Se consultó el Reporte de Asistencia de Miembros de Mesa", "",
                    filtro.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO,
                    ConstantesComunes.LOG_TRANSACCIONES_ACCION);

            return Funciones.generarReporte(this.getClass(), listaActas, nombreReporte, parametros);
        } catch (Exception e) {
            logger.error("excepcion", e);
            return new byte[0];
        }
    }
}
