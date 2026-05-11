package pe.gob.onpe.sceorcbackend.service.reportes.resultados.impl.strategy.eleccionstrategy;


import pe.gob.onpe.sceorcbackend.model.dto.reporte.FiltroResultadoContabilizadasDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ReporteResultadoActasContDto;
import pe.gob.onpe.sceorcbackend.model.dto.reporte.ResultadoActasContabilizadasDto;

import java.util.List;
import java.util.Map;

/**
 * Strategy interface para manejar diferentes tipos de elecciones
 */
public interface EleccionStrategy {
    
    /**
     * Procesa los resultados de actas contabilizadas según el tipo de elección
     */
    ResultadoActasContabilizadasDto procesarResultados(FiltroResultadoContabilizadasDto filtro);
    
    /**
     * Genera el reporte PDF según el tipo de elección
     */
    byte[] generarReportePdf(FiltroResultadoContabilizadasDto filtro);
    
    /**
     * Mapea los resultados para el PDF según el tipo de elección
     */
    List<ReporteResultadoActasContDto> mapearResultadosParaPdf(List<Map<String, Object>> resultadosMap);
    
    /**
     * Verifica si esta estrategia puede manejar el tipo de elección dado
     */
    boolean puedeManejar(FiltroResultadoContabilizadasDto filtro);
}
