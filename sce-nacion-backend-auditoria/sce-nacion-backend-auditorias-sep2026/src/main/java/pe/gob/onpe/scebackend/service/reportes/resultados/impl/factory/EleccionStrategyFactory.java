package pe.gob.onpe.scebackend.service.reportes.resultados.impl.factory;

import org.springframework.stereotype.Component;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroResultadoContabilizadasDto;
import pe.gob.onpe.scebackend.service.reportes.resultados.impl.strategy.eleccionstrategy.EleccionStrategy;

import java.util.List;

/**
 * Factory para crear las estrategias de reportes de elecciones
 */
@Component
public class EleccionStrategyFactory {
    
    private final List<EleccionStrategy> strategies;
    
    public EleccionStrategyFactory(List<EleccionStrategy> strategies) {
        this.strategies = strategies;
    }
    
    /**
     * Obtiene la estrategia apropiada para el tipo de elección
     * @param filtro Filtro que contiene el tipo de elección
     * @return La estrategia correspondiente
     * @throws IllegalArgumentException Si no se encuentra una estrategia para el tipo de elección
     */
    public EleccionStrategy obtenerEstrategia(FiltroResultadoContabilizadasDto filtro) {
        return strategies.stream()
                .filter(strategy -> strategy.puedeManejar(filtro))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                    "No se encontró una estrategia para el tipo de elección: " + filtro.getCodigoEleccion() +
                            " " + filtro.getEleccion()
                ));
    }
}
