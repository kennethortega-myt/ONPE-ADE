package pe.gob.onpe.sceorcbackend.model.dto.controlcalidad;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AgrupacionPoliticaResponse {
    private Integer id;
    private Long idActa;
    private Integer posicion;
    private String codigo;
    private String descripcion;
}
