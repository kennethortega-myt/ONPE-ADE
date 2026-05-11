package pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.impl.reporte;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import pe.gob.onpe.sceorcbackend.model.dto.reporte.AsistenciaMiembroMesaRequestDto;
import pe.gob.onpe.sceorcbackend.model.dto.response.reporte.AsistenciaMiembroMesaResponseDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.reportes.IAsistenciaMiembrosMesaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.AsistenciaMiembroMesaService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.UtilSceService;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;
import pe.gob.onpe.sceorcbackend.utils.funciones.Funciones;

@Service
public class AsistenciaMiembroMesaServiceImpl implements AsistenciaMiembroMesaService{

	Logger logger = LoggerFactory.getLogger(AutoridadesRevocadasServiceImpl.class);
	
	private final IAsistenciaMiembrosMesaRepository asistenciaMiembrosMesaRepository;
	private final UtilSceService utilSceService;
	private final ITabLogService logService;
	
	public AsistenciaMiembroMesaServiceImpl(IAsistenciaMiembrosMesaRepository asistenciaMiembrosMesaRepository,
			UtilSceService utilSceService, ITabLogService logService) {
		this.asistenciaMiembrosMesaRepository = asistenciaMiembrosMesaRepository;
		this.utilSceService = utilSceService;
		this.logService = logService;
	}

	public byte[] getReporteAsistenciaMiembroMesa(AsistenciaMiembroMesaRequestDto filtro) {
		try{
			String nombreReporte = "";

			List<AsistenciaMiembroMesaResponseDto> listaActas = this.asistenciaMiembrosMesaRepository.listaAsistenciaMM(filtro);

			nombreReporte = ConstantesComunes.REPORTE_ASISTENCIA_MIEMBROS_MESA;

			Map<String, Object> parametros = new java.util.HashMap<>();

			InputStream imagen = this.getClass().getClassLoader().getResourceAsStream(ConstantesComunes.PATH_IMAGE_COMMON_NAC + "onpe.jpg");
			parametros.put("imagen", imagen);
			parametros.put("sinvaloroficial", utilSceService.getSinValorOficial(filtro.getIdProceso()));
			parametros.put("versionSuite", utilSceService.getVersionSistema());
			parametros.put("viewUsuario", filtro.getUsuario());
			parametros.put("tituloGeneral", filtro.getProceso());
			parametros.put("tituloRep", "LISTA DE ASISTENCIA DE MIEMBROS DE MESA");

			this.logService.registrarLog(filtro.getUsuario(),
					Thread.currentThread().getStackTrace()[1].getMethodName(),
					this.getClass().getSimpleName(),
					"Se consultó el Reporte de Avance Asistencia miembros de mesa.",
					filtro.getCodigoCentroComputo(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO,
					ConstantesComunes.LOG_TRANSACCIONES_ACCION);

			return Funciones.generarReporte(this.getClass(), listaActas, nombreReporte, parametros);

		}catch(Exception e) {
			logger.error("excepcion", e);
			return null;
		}
	}

}
