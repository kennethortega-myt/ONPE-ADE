package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Data;

@Data
public class ActaContabilizadaDetalleDto {

    private String descripcion;
    private Long cantidad;

    public ActaContabilizadaDetalleDto() {
        super();
    }

    public ActaContabilizadaDetalleDto(String descripcion, Long cantidad) {
        this.descripcion = descripcion;
        this.cantidad = cantidad;
    }
}
