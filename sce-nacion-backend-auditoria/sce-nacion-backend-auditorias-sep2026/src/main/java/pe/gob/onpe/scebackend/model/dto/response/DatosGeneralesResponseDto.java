package pe.gob.onpe.scebackend.model.dto.response;

import lombok.Data;

@Data
public class DatosGeneralesResponseDto {
    
    private Integer id;
    
    private String nombre;

    private Integer principal;

    private String codigo;
}
