package pe.gob.onpe.sceorcbackend.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class MesasAInstalarDto {

    private String titulo;
    private Long cantidad;
    private List<MesasAInstalarDetalleDto> detalle;
}
