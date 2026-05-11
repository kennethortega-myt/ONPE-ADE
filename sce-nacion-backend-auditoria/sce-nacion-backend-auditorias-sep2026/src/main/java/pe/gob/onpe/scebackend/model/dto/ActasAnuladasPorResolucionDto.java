package pe.gob.onpe.scebackend.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class ActasAnuladasPorResolucionDto {

    private String titulo;
    private List<ActaContabilizadaDetalleDto> detalle;

}
