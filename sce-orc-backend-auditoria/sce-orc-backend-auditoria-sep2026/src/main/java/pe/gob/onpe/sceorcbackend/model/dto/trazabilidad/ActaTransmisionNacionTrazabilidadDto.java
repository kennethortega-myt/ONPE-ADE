package pe.gob.onpe.sceorcbackend.model.dto.trazabilidad;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.TransmisionDto;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ActaTransmisionNacionTrazabilidadDto {

  private Long id;
  private Long idActa;
  private Integer estadoTransmitidoNacion;
  private String tipoTransmision;
  private Integer transmite;
  private Date fechaTransmision;
  private Date fechaRegistro;
  private String usuarioRegistro;
  private String accion;
  private String usuarioTransmision;
  private Integer intento;
  private Date fechaInicio;
  private Date fechaFin;
  private TransmisionDto requestActaTransmision;
}
