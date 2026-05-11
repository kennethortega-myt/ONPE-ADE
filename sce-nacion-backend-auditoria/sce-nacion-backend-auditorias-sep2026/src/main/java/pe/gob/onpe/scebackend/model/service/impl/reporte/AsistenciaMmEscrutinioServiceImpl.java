package pe.gob.onpe.scebackend.model.service.impl.reporte;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pe.gob.onpe.scebackend.model.dto.request.reporte.AsistenciaMiembroMesaRequestDto;
import pe.gob.onpe.scebackend.model.orc.projections.reporte.AsistenciaMmEscrutinioProjection;
import pe.gob.onpe.scebackend.model.orc.repository.reportes.AsistenciaMmEscrutinioRepository;
import pe.gob.onpe.scebackend.model.service.AsistenciaMmEscrutinioService;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.model.service.UtilSceService;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;
import pe.gob.onpe.scebackend.utils.funciones.Funciones;

@Service
public class AsistenciaMmEscrutinioServiceImpl implements AsistenciaMmEscrutinioService{

	Logger logger = LoggerFactory.getLogger(AsistenciaMmEscrutinioServiceImpl.class);
	
	private final AsistenciaMmEscrutinioRepository asistenciaMmEscrutinioRepository;
	private final ITabLogTransaccionalService logService;
	private final UtilSceService utilSceService;

	public AsistenciaMmEscrutinioServiceImpl(AsistenciaMmEscrutinioRepository asistenciaMmEscrutinioRepository,
											 ITabLogTransaccionalService logService,
											 UtilSceService utilSceService) {
		this.asistenciaMmEscrutinioRepository = asistenciaMmEscrutinioRepository;
		this.logService = logService;
		this.utilSceService = utilSceService;
	}
	
	@Override
	public byte[] getReporteAsistenciaMmEscrutinio(AsistenciaMiembroMesaRequestDto filtro) {
		try{
        	String nombreReporte = "";

			Integer mesa = null;
			if (filtro.getMesa() != null && !filtro.getMesa().trim().isEmpty()) {
				mesa = Integer.parseInt(filtro.getMesa());
			}

			List<AsistenciaMmEscrutinioProjection> listaActas = this.asistenciaMmEscrutinioRepository.listaAsistenciaMmEscrutinio(
					filtro.getEsquema(), filtro.getIdEleccion(), filtro.getIdCentroComputo(), filtro.getUbigeo(), mesa);

			nombreReporte = ConstantesComunes.REPORTE_ASISTENCIA_MM_ESCRUTINIO;
			
            Map<String, Object> parametros = new java.util.HashMap<>();

            InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + "onpe.jpg");
            parametros.put("imagen", imagen);
            parametros.put("sinvaloroficial", utilSceService.getSinValorOficial(filtro.getIdProceso()));
            parametros.put("versionSuite", utilSceService.getVersionSistema());            
            parametros.put("viewUsuario", filtro.getUsuario());            
            parametros.put("tituloGeneral", filtro.getProceso());
            parametros.put("tituloRep", "LISTA DE MIEMBROS DE MESA SEGÚN ACTA DE ESCRUTINIO");
            parametros.put("prefUbigeoExtranjero", ConstantesComunes.PREFIJO_UBIGEO_EXTRANJEROS);

			this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE, this.getClass().getSimpleName(),
            			"Se consultó el Reporte de Asistencia de los Miembros de Mesa de Escrutinio.",
            			ConstantesComunes.CC_NACION_DESCRIPCION, "C56000", ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO,
            			ConstantesComunes.LOG_TRANSACCIONES_ACCION);

            return Funciones.generarReporte(this.getClass(), listaActas, nombreReporte, parametros);

        }catch(Exception e) {
            logger.error("excepcion", e);
            return new byte[0];
        }
	}
}
