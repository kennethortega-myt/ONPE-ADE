package pe.gob.onpe.scebackend.model.dto;

import lombok.Data;

import java.util.Objects;

@Data
public class AvanceMesaVotoDto {

    private Long idAgrupacionPolitica;
    private String agrupacionPolitica;
    private Long posicion;
    private Long votos;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AvanceMesaVotoDto that = (AvanceMesaVotoDto) o;
        return Objects.equals(idAgrupacionPolitica, that.idAgrupacionPolitica);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAgrupacionPolitica);
    }
}
