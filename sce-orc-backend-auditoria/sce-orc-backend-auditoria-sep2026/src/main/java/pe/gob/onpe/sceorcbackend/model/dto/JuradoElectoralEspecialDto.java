package pe.gob.onpe.sceorcbackend.model.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class JuradoElectoralEspecialDto {

  private Integer id;

  private Integer idCentroComputo;
  
  private String codigoCentroComputo;
  
  private String nombreCentroComputo;
  
  private String idJEE;
  
  private String nombreJEE;
  
  private String direccion;
  
  private String apellidoPaterno;
  
  private String apellidoMaterno;
  
  private String nombreRepresentante;
  
  private String usuarioModificacion;
  
}
