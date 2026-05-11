package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class DetActaFormatoDto {

    private Long id;
    private Long idActa;
    private Long idCabActaFormato;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;

}
