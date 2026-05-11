package pe.gob.onpe.scebackend.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class MesasAInstalarDto {

    private String titulo;
    private Long   cantidad;
    private List<MesasAInstalarDetalleDto> detalle;
}
