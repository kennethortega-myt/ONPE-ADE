package pe.gob.onpe.scebackend.sasa.dto;

import lombok.Data;

@Data
public class BuscarPorIdOutputDto {
    private Boolean success;
    private String message;
    private String titulo;
    private BuscarPorIdUsuarioOutputDto data;
}
