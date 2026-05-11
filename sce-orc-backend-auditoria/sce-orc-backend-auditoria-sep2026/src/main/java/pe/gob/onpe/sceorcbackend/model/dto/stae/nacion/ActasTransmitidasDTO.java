package pe.gob.onpe.sceorcbackend.model.dto.stae.nacion;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

@Data
public class ActasTransmitidasDTO implements Serializable{

  private Integer idTransmision;
  private String centroComputo;
  private String acronimoProceso;
  private Integer estadoTransmitidoNacion;
  private String accion;
  private String fechaTransmision;
  private String fechaRegistro;
  private List<ActaTransmitidaDTO> actaTransmitida;
}
