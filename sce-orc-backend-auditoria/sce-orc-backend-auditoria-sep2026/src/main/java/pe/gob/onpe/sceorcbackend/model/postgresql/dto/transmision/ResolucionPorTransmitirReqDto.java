package pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision;

import lombok.*;
import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResolucionPorTransmitirReqDto implements Serializable {

  @Serial
  private static final long serialVersionUID = -7012681363492793182L;

  private Long id;
  private String idCc;
  private Long idArchivoResolucion;
  private ArchivoTransmisionReqDto archivoResolucion;
  private Integer procedencia;
  private String fechaResolucion;
  private String numeroExpediente;
  private String numeroResolucion;
  private Integer tipoResolucion;
  private String estadoResolucion;
  private String estadoDigitalizacion;
  private String observacionDigitalizacion;
  private Integer numeroPaginas;
  private Integer activo;
  private String audUsuarioCreacion;
  private String audFechaCreacion;
  private String audUsuarioModificacion;
  private String audFechaModificacion;

}
