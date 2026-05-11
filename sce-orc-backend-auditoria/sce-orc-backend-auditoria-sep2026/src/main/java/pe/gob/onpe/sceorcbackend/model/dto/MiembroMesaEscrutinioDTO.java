package pe.gob.onpe.sceorcbackend.model.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MiembroMesaEscrutinioDTO {

  private Long id;
  private MesaDTO mesa;
  private String documentoIdentidadPresidente;
  private String documentoIdentidadSecretario;
  private String documentoIdentidadTercerMiembro;
  private Integer activo;
  private String usuarioCreacion;
  private Date fechaCreacion;
  private String usuarioModificacion;
  private Date fechaModificacion;
  private Integer tipoFiltro;
  private Long actaId;
  private String acronimoProceso;


}
