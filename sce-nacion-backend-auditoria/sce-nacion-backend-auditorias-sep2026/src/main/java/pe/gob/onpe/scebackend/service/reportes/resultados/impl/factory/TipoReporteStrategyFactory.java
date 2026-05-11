package pe.gob.onpe.scebackend.service.reportes.resultados.impl.factory;

import org.springframework.stereotype.Component;
import pe.gob.onpe.scebackend.model.dto.reportes.FiltroResultadoContabilizadasDto;
import pe.gob.onpe.scebackend.repository.resultados.ResultadosActaRepository;
import pe.gob.onpe.scebackend.service.reportes.resultados.impl.strategy.tiporeportestrategy.TipoReporteStrategy;
import pe.gob.onpe.scebackend.service.reportes.resultados.impl.strategy.tiporeportestrategy.impl.*;
import pe.gob.onpe.scebackend.utils.constantes.ConstantesComunes;


/**
 * Factory para crear las estrategias de tipo de reporte (Resumido/Detallado)
 */
@Component
public class TipoReporteStrategyFactory {

    private final ResultadosActaRepository resultadosActaRepository;

    public TipoReporteStrategyFactory(ResultadosActaRepository resultadosActaRepository) {
        this.resultadosActaRepository = resultadosActaRepository;
    }

    /**
     * Obtiene la estrategia de tipo de reporte apropiada
     * @param filtro Filtro que contiene el tipo de elección y tipo de reporte
     * @return La estrategia correspondiente
     */
    public TipoReporteStrategy obtenerEstrategia(FiltroResultadoContabilizadasDto filtro) {
        String codigoEleccion = filtro.getCodigoEleccion();
        Integer tipoReporte = filtro.getTipoReporte();

        // Elección Presidencial
        if (codigoEleccion.equals(ConstantesComunes.COD_ELEC_PRE)) {
            if (tipoReporte.equals(ConstantesComunes.COD_TIPO_REPORTE_RESUMIDO)) {
                return new TipoReportePresidencialResumidoStrategyImpl(resultadosActaRepository);
            } else if(tipoReporte.equals(ConstantesComunes.COD_TIPO_REPORTE_DETALLADO)) {
                return new TipoReportePresidencialDetalladoStrategyImpl(resultadosActaRepository);
            }
        }

        // Elección EMC
        if (codigoEleccion.equals(ConstantesComunes.COD_ELEC_DIST)) {
            if (tipoReporte.equals(ConstantesComunes.COD_TIPO_REPORTE_RESUMIDO)) {
                return new TipoReporteEMCResumidoStrategyImpl(resultadosActaRepository);
            } else if(tipoReporte.equals(ConstantesComunes.COD_TIPO_REPORTE_DETALLADO)) {
                return new TipoReporteEMCDetalladoStrategyImpl(resultadosActaRepository);
            }
        }

        // Elección Senado Multiple
        if (codigoEleccion.equals(ConstantesComunes.COD_ELEC_SENADO_MULTIPLE)) {
            if (tipoReporte.equals(ConstantesComunes.COD_TIPO_REPORTE_RESUMIDO)) {
                return new TipoReporteSenadoresDistritoMultipleResumidoStrategyImpl(resultadosActaRepository);
            } else if(tipoReporte.equals(ConstantesComunes.COD_TIPO_REPORTE_DETALLADO)) {
                return new TipoReporteSenadoresDistritoMultpleDetalladoStrategyImpl(resultadosActaRepository);
            }
        }

        // Elección CPR
        if (codigoEleccion.equals(ConstantesComunes.COD_ELEC_REV_DIST)) {
            if (tipoReporte.equals(ConstantesComunes.COD_TIPO_REPORTE_RESUMIDO)) {
                return new TipoReporteCPRResumidoStrategyImpl(resultadosActaRepository);
            } else if(tipoReporte.equals(ConstantesComunes.COD_TIPO_REPORTE_DETALLADO)) {
                return new TipoReporteCPRDetalladoStrategyImpl(resultadosActaRepository);
            }
        }

        // Elecciones Municipales Distritales
        if (codigoEleccion.equals(ConstantesComunes.COD_ELEC_DIST)) {
            if (tipoReporte.equals(ConstantesComunes.COD_TIPO_REPORTE_RESUMIDO)) {
                return new TipoReportePresidencialResumidoStrategyImpl(resultadosActaRepository);
            } else if(tipoReporte.equals(ConstantesComunes.COD_TIPO_REPORTE_DETALLADO)) {
                return new TipoReportePresidencialDetalladoStrategyImpl(resultadosActaRepository);
            }
        }

        // Elecciones Parlamento Andino
        if (codigoEleccion.equals(ConstantesComunes.COD_ELEC_PAR)) {
            if (tipoReporte.equals(ConstantesComunes.COD_TIPO_REPORTE_RESUMIDO)) {
                return new TipoReporteParlamentoAndinoResumidoStrategyImpl(resultadosActaRepository);
            } else if(tipoReporte.equals(ConstantesComunes.COD_TIPO_REPORTE_DETALLADO)) {
                return new TipoReporteParlamentoAndinoDetalladoStrategyImpl(resultadosActaRepository);
            }
        }

        // Elecciones Diputados
        if (codigoEleccion.equals(ConstantesComunes.COD_ELEC_DIPUTADO)) {
            if (tipoReporte.equals(ConstantesComunes.COD_TIPO_REPORTE_RESUMIDO)) {
                return new TipoReporteDiputadosResumidoStrategyImpl(resultadosActaRepository);
            } else if(tipoReporte.equals(ConstantesComunes.COD_TIPO_REPORTE_DETALLADO)) {
                return new TipoReporteDiputadosDetalladoStrategyImpl(resultadosActaRepository);
            }
        }

        // Elecciones Senadores Distrito Unico
        if (codigoEleccion.equals(ConstantesComunes.COD_ELEC_SENADO_UNICO)) {
            if (tipoReporte.equals(ConstantesComunes.COD_TIPO_REPORTE_RESUMIDO)) {
                return new TipoReporteSenadoresDistritoUnicoResumidoStrategyImpl(resultadosActaRepository);
            } else if(tipoReporte.equals(ConstantesComunes.COD_TIPO_REPORTE_DETALLADO)) {
                return new TipoReporteSenadoresDistritoUnicoDetalladoStrategyImpl(resultadosActaRepository);
            }
        }

        throw new IllegalArgumentException(
                "No se encontró una estrategia para el tipo de reporte: " + filtro.getCodigoEleccion() + ": " + filtro.getEleccion()
        );
    }
}
