package pe.gob.onpe.scebackend.model.dto;

import lombok.Data;

import java.util.Objects;

@Data
public class ActaContabilizadaReporte {

    private String descripcion;
    private Long cantidad;
    private Double porcentajeValidos;
    private Double porcentajeEmitidos;
    private Long posicion;

    public ActaContabilizadaReporte() {
        super();
    }

    public ActaContabilizadaReporte(String descripcion, Long cantidad, Double porcentajeEmitidos) {
        this.descripcion = descripcion;
        this.porcentajeEmitidos = porcentajeEmitidos;
        this.cantidad = cantidad;
    }

    public ActaContabilizadaReporte(
            String descripcion,
            Long cantidad,
            Double porcentajeValidos,
            Double porcentajeEmitidos) {
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.porcentajeValidos = porcentajeValidos;
        this.porcentajeEmitidos = porcentajeEmitidos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActaContabilizadaReporte that = (ActaContabilizadaReporte) o;
        return Objects.equals(descripcion, that.descripcion);
    }

    @Override
    public int hashCode() {
        return Objects.hash(descripcion);
    }

}
