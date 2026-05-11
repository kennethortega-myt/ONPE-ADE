package pe.gob.onpe.scebackend.model.dto;

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
    private Long actasContabilizadas;
    private Long actasPendientesResolverJEE;
    private Double actasProcesadasPorcen;
    private Double actasContabilizadasPorcen;
    private Double actasPendientesResolverJEEPorcen;
    private Integer flagProcesadasCompletas;
    private Integer flagContabilizadasCompletas;
    
}
