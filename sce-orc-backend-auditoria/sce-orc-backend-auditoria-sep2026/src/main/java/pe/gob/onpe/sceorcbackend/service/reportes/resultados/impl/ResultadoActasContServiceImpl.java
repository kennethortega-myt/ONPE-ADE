package pe.gob.onpe.sceorcbackend.service.reportes.resultados.impl;

import org.springframework.stereotype.Service;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroResultadoContabilizadasDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ResultadoActasContabilizadasDto;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.repository.ActaRepository;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.service.ITabLogService;
import pe.gob.onpe.sceorcbackend.service.reportes.resultados.ResultadoActasContService;
import pe.gob.onpe.sceorcbackend.service.reportes.resultados.impl.factory.EleccionStrategyFactory;
import pe.gob.onpe.sceorcbackend.service.reportes.resultados.impl.strategy.eleccionstrategy.EleccionStrategy;
import pe.gob.onpe.sceorcbackend.utils.ConstantesComunes;


@Service
public class ResultadoActasContServiceImpl implements ResultadoActasContService {

	private final ITabLogService logService;
	private final EleccionStrategyFactory strategyFactory;
	
	public ResultadoActasContServiceImpl(ActaRepository actasRepository,
										 ITabLogService logService,
										 EleccionStrategyFactory strategyFactory) {
		this.logService = logService;
		this.strategyFactory = strategyFactory;
	}
	
	@Override
	public ResultadoActasContabilizadasDto busquedaResultadosActasContabilizadas(FiltroResultadoContabilizadasDto filtros) {
		this.logService.registrarLog(filtros.getUsuario(), Thread.currentThread().getStackTrace()[1].getMethodName(),
				this.getClass().getSimpleName(), "Se consultó el Reporte de Resultados de actas contabilizadas.",
				 filtros.getCc(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);
		EleccionStrategy strategy = strategyFactory.obtenerEstrategia(filtros);
		return strategy.procesarResultados(filtros);

	}

	@Override
    public byte[] getReporteResultadoActasContabilizadas(FiltroResultadoContabilizadasDto filtro) {
		this.logService.registrarLog(
				filtro.getUsuario(),
				Thread.currentThread().getStackTrace()[1].getMethodName(),
				this.getClass().getSimpleName(),
				"Se consultó el Reporte en PDF de Resultados de actas contabilizadas.", filtro.getCc(),
				ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);

		EleccionStrategy strategy = strategyFactory.obtenerEstrategia(filtro);
		return strategy.generarReportePdf(filtro);
    }

}
