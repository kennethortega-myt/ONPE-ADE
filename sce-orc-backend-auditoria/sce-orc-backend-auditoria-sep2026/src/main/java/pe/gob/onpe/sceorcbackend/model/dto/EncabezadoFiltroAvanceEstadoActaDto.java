package pe.gob.onpe.sceorcbackend.model.dto;


import lombok.Data;

@Data
public class EncabezadoFiltroAvanceEstadoActaDto {

    private String codigoEleccion;
    private String nombreEleccion;
    private String codigoCc;
    private String nombreCc;
    private String nombreProceso;

}
