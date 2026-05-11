package pe.gob.onpe.scebackend.model.dto.reportes;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DetalleResumenTotal {

	private String codigoCc;
    private String centroComputo;
    private String habilitado;    
    private double mesasAInstalar;
    private double mesasInstal;
    private double mesasNoInstal;
    private double actasSiniestradas;
    private double actasExtraviadas;
    private double actasProcesadas;
    private double actasContabilizadas;
    
    private double digActas;
    private double digListaElectores;
    private double digHojasAsistencia;
    private double digResoluciones;
    
    private double txActas;
    private double txResolucion;
    
    private double omisosMm;
    private double omisosVotantes;
    private double omisosMmActaEscrutinio;
    private double omisosPersoneros;
    
    private double digActasPorcentaje;
    private double digListaElectoresPorcentaje;
    private double digHojasAsistenciaPorcentaje;
    private double digResolucionesPorcentaje;
    
    private double txActasPorcentaje;
    private double txResolucionPorcentaje;
    
    private double omisosMmPorcentaje;
    private double omisosVotantesPorcentaje;
    private double omisosMmActaEscrutinioPorcentaje;
    private double omisosPersonerosPorcentaje;
    
    private double totalMesas;
    private double resolucionesProcesadas;
}
