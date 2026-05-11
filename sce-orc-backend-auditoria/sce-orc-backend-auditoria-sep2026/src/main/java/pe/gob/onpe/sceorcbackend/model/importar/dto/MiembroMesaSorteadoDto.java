package pe.gob.onpe.sceorcbackend.model.importar.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class MiembroMesaSorteadoDto {

    private Long id;
    private Long idMesa;
    private Long idPadronElectoral;
    private Integer cargo;
    private Integer bolo;
    private String direccion;
    private Integer turno;
    private Integer estado;
    private Integer asistenciaAutomatico;
    private Integer asistenciaManual;
    private Integer activo;
    private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;

}
