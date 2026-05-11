package pe.gob.onpe.scebackend.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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
