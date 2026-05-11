package pe.gob.onpe.sceorcbackend.model.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetParametroDto {

  private Long id;

  private String nombre;

  private String valor;

  private Integer tipoDato;

  private ParametroDto parametro;

  private String descripcion;

  private Integer activo;

  private String usuarioCreacion;

  private Date fechaCreacion;

  private String usuarioModificacion;

  private Date fechaModificacion;

}
