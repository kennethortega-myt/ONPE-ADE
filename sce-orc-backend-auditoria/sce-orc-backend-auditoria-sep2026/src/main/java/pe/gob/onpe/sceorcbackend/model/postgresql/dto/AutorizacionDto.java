package pe.gob.onpe.sceorcbackend.model.postgresql.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class AutorizacionDto implements Serializable {

    private static final long serialVersionUID = 1662582442791663580L;
    private Long id;
    private Long numero;
    private String detalle;
    private String estado;
    private String descripcionEstado;
    private String fechaHora;


}
