package pe.gob.onpe.sceorcbackend.utils.reimpresion;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReimpresionCargoDto implements Serializable {

    private String mesa;
    private String eleccion;
    private String acta;
    private Integer tipoCargo;
    private Integer correlativo;
    private String descripcionCargo;
    private Long idArchivo;
    private String nombreArchivo;

}
