package pe.gob.onpe.sceorcbackend.model.dto.queue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetActaQueue implements Serializable {
  @Serial
  private static final long serialVersionUID = 5915953875498011689L;
  private Long votos;
  private Long idAgrupacionPolitica;
  private String estadoErrorMaterial;
  private String ilegible;
  private Integer activo;
  private String audUsuarioCreacion;
  private Date audFechaCreacion;
  private String audUsuarioModificacion;
  private Date audFechaModificacion;
  private String accion;

}
