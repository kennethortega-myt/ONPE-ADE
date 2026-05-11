package pe.gob.onpe.scebackend.model.service.impl.reporte;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.dto.request.reporte.AutoridadesRevocadasRequestDto;
import pe.gob.onpe.scebackend.model.dto.response.reporte.AutoridadesRevocadasResponseDto;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.IAutoridadesRevocadasRepository;
import pe.gob.onpe.scebackend.model.service.AutoridadesRevocadasService;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

@Service
public class AutoridadesRevocadasServiceImpl implements AutoridadesRevocadasService {

    Logger logger = LoggerFactory.getLogger(AutoridadesRevocadasServiceImpl.class);

    private static final int CARGO_REGIDOR = 18;
    private static final String IMG_ONPE_JPG = "onpe.jpg";

    private final IAutoridadesRevocadasRepository autoridadesRevocadasRepository;
    private final ITabLogTransaccionalService logService;
    private final UtilSceService utilSceService;

    public AutoridadesRevocadasServiceImpl(IAutoridadesRevocadasRepository autoridadesRevocadasRepository,
            ITabLogTransaccionalService logService, UtilSceService utilSceService) {
        this.autoridadesRevocadasRepository = autoridadesRevocadasRepository;
        this.logService = logService;
        this.utilSceService = utilSceService;
    }

    @Override
    @Transactional("locationTransactionManager")
    public byte[] getReporteAutoridadesRevocadas(AutoridadesRevocadasRequestDto filtro) {
        try {
            List<AutoridadesRevocadasResponseDto> listaActas = this.autoridadesRevocadasRepository
                    .listaAutoridadesRevocadas(filtro);

            return generarReporteAutoridadesRevocadas(filtro, listaActas);
        } catch (Exception e) {
            logger.error("excepcion", e);
            return new byte[0];
        }
    }

    private byte[] generarReporteAutoridadesRevocadas(AutoridadesRevocadasRequestDto filtro,
            List<AutoridadesRevocadasResponseDto> listaActas) {
        String nombreReporte = ConstantesComunes.PATH_REPORT_JRXML + File.separator
                + ConstantesComunes.REPORTE_AUTORIDADES_REVOCADAS;

        Map<String, Object> parametros = new java.util.HashMap<>();

        try (InputStream imagen = this.getClass().getClassLoader()
                .getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + IMG_ONPE_JPG)) {

            parametros.put("url_imagen", imagen);
            parametros.put("sinvaloroficial", this.utilSceService.getSinValorOficial(filtro.getIdProceso()));
            parametros.put("version", utilSceService.getVersionSistema());
            parametros.put("usuario", filtro.getUsuario());
            parametros.put("tituloPrincipal", filtro.getProceso());
            parametros.put("centroComputo", filtro.getCentroComputo());
            parametros.put("odpe", filtro.getOdpe());
            parametros.put("tipoAutoridad", resolverTipoAutoridad(filtro.getIdCargo()));
            parametros.put("eleccion", filtro.getEleccion());

            this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE,
                    this.getClass().getSimpleName(), "Se consultó el Reporte de Autoridades Revocadas",
                    ConstantesComunes.CC_NACION_DESCRIPCION, filtro.getCentroComputo(),
                    ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);

            return Funciones.generarReporte(this.getClass(), listaActas, nombreReporte, parametros);
        } catch (Exception e) {
            logger.error("excepcion", e);
            return new byte[0];
        }
    }

    private static String resolverTipoAutoridad(Integer idCargo) {
        if (idCargo == null) {
            return "ALCALDES / REGIDORES";
        }
        if (idCargo == CARGO_REGIDOR) {
            return "REGIDORES";
        }
        return "ALCALDES";
    }

}
