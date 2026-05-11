package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Data;

@Data
public class AvanceEstadoActaDetalleDto {

    private Integer idCentroComputo;
    private String codigoCc;
    private String nombreCc;
    private String fechaUltModificacion;
    private Long mesasHabiles;
    private Long totalActas;
    private Long actasIngresadas;
    private Long actasProcesadas;
    private Double actasProcesadasPorcen;
    private Long actasContabilizadas;
    private Double actasContabilizadasPorcen;
    private Long actasPendientesResolverJEE;
    private Double actasPendientesResolverJEEPorcen;
    private Integer flagProcesadasCompletas;
    private Integer flagContabilizadasCompletas;

}
