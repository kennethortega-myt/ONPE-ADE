package pe.gob.onpe.sceorcbackend.sasa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginAccionesOutputDto {
    private Integer idAccion;
    private String nombre;
    private String descripcion;
}
