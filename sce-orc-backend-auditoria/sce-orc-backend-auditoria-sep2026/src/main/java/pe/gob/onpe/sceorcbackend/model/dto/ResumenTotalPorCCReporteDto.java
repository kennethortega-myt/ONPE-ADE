package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResumenTotalPorCCReporteDto {

    private String proceso;
    private String eleccion;
    private String etiquetaAmbito;
    private String ambito;
    private String codigoCentroComputo;
    private String centroComputo;
    private String habilitadoCierre;
    private Double porcDigitalizacionActas;
    private Double porcDigitalizacionResolucion;
    private Long totalMesasInstaladas;
    private Long totalMesasNoInstaladas;
    private Double porcActasSiniestradasAnuladas;
    private Double porcActasExtravAnuladas;
    private Double porcActasProcesadasRecibidas;
    private Double porcActasContabilizadas;
    private Double porcCcActas;
    private Double porcCcResol;
    private Double porcTransImagenes;
    private Double porcTransResol;
    private Double porcRegOmisosMiembros;
    private Double porcRegOmisosVotantes;


}
