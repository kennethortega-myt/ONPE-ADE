package pe.gob.onpe.sceorcbackend.model.dto.stae.nacion;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
public class DetActaTransmitidaDTO implements Serializable {

  @Serial
  private static final long serialVersionUID = 2887587181721290368L;

  private Long idActaDetalle;
  private Long idActa;
  private Long idAgrupacionPolitica;
  private Long posicion;
  private Long votos;
  private Long votosAutomatico;
  private Long votosManual1;
  private Long votosManual2;
  private String estadoErrorMaterial;
  private String ilegible;
  private Integer activo;
  private String audUsuarioCreacion;
  private String audFechaCreacion;
  private String audUsuarioModificacion;
  private String audFechaModificacion;
  private Integer estado;
  private List<DetActaPreferencialTransmitidaDTO> detActaPreferencial;

}
