package pe.gob.onpe.scebackend.service.reportes.resultados.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.onpe.scebackend.model.dto.reportes.FiltroResultadoContabilizadasDto;
import pe.gob.onpe.scebackend.model.dto.reportes.ResultadoActasContabilizadasDto;
import pe.gob.onpe.scebackend.model.orc.repository.ActaRepository;
import pe.gob.onpe.scebackend.model.service.ITabLogTransaccionalService;
import pe.gob.onpe.scebackend.service.reportes.resultados.ResultadoActasContService;
import pe.gob.onpe.scebackend.service.reportes.resultados.impl.factory.EleccionStrategyFactory;
import pe.gob.onpe.scebackend.service.reportes.resultados.impl.strategy.eleccionstrategy.EleccionStrategy;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;


@Service
public class ResultadoActasContServiceImpl implements ResultadoActasContService {

	private final ITabLogTransaccionalService logService;
	private final EleccionStrategyFactory strategyFactory;
	
	public ResultadoActasContServiceImpl(ITabLogTransaccionalService logService,
										 EleccionStrategyFactory strategyFactory) {
		this.logService = logService;
		this.strategyFactory = strategyFactory;
	}
	
	@Override
	@Transactional("locationTransactionManager")
	public ResultadoActasContabilizadasDto busquedaResultadosActasContabilizadas(FiltroResultadoContabilizadasDto filtros) {
		this.logService.registrarLog(filtros.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE,
				this.getClass().getSimpleName(), "Se consultó el Reporte de Resultados de actas contabilizadas.",
				"", filtros.getCc(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);

		EleccionStrategy strategy = strategyFactory.obtenerEstrategia(filtros);
		return strategy.procesarResultados(filtros);
	}

	@Override
	@Transactional("locationTransactionManager")
    public byte[] getReporteResultadoActasContabilizadas(FiltroResultadoContabilizadasDto filtro) {
		this.logService.registrarLog(filtro.getUsuario(), ConstantesComunes.LOG_TRANSACCIONES_TIPO_REPORTE,
				this.getClass().getSimpleName(), "Se consultó el Reporte en PDF de Resultados de actas contabilizadas.",
				"", filtro.getCc(), ConstantesComunes.LOG_TRANSACCIONES_AUTORIZACION_NO, ConstantesComunes.LOG_TRANSACCIONES_ACCION);

		EleccionStrategy strategy = strategyFactory.obtenerEstrategia(filtro);
		return strategy.generarReportePdf(filtro);
    }
}
