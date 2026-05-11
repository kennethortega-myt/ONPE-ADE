package pe.gob.onpe.scebackend.model.dto;

import lombok.Data;

@Data
public class PaginacionDetalleDto {
    private Integer paginas;
    private Integer cantidadPagina;
    private Integer totalRegistro;
}
