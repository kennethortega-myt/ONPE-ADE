package pe.gob.onpe.scebackend.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ParametroDto {

    private Long id;

    private String parametro;

    private Integer activo;

    private List<DetParametroDto> detalles;

    private Object valor;

    private String usuario;
}