package pe.gob.onpe.scebackend.model.dto;

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
