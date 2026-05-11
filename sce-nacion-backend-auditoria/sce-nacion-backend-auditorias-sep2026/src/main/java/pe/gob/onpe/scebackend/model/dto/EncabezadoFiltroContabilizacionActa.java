package pe.gob.onpe.scebackend.model.dto;

import lombok.Data;

@Data
public class EncabezadoFiltroContabilizacionActa {

    private String proceso;
    private String eleccion;
    private String ambito;
    private String etiquetaAmbito;
    private String centroComputo;
    private String departamento;
    private String provincia;
    private String distrito;
}
