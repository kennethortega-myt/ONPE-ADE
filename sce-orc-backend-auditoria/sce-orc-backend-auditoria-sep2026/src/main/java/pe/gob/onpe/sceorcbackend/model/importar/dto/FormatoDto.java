package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class FormatoDto {

    private Long id;
    private Long idArchivoActa;
    private Integer correlativo;
    private Integer tipoFormato;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;


}
