package pe.gob.onpe.sceorcbackend.model.dto.response.reporte;

import lombok.Data;

@Data
public class DatosGeneralesResponseDto {
    
    private Integer id;
    private String nombre;
    private Integer principal;
    private String codigo;
}
