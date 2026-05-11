package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class ActasPorResolverJEEDto {

    private String titulo;
    private List<ActaContabilizadaDetalleDto> detalle;
}
