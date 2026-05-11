package pe.gob.onpe.sceorcbackend.model.dto;

import java.util.Date;

import pe.gob.onpe.sceorcbackend.model.importar.dto.AgrupacionPoliticaDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersoneroDTO {
  private Long id;
  private MesaDTO mesa;
  private AgrupacionPoliticaDto agrupacionPolitica;
  private String documentoIdentidad;
  private String nombres;
  private String apellidoPaterno;
  private String apellidoMaterno;
  private Integer activo;
  private String usuarioCreacion;
  private Date fechaCreacion;
  private String usuarioModificacion;
  private Date fechaModificacion;

}
