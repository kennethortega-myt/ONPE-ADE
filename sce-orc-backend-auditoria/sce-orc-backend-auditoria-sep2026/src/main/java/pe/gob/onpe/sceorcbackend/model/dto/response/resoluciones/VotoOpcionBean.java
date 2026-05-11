package pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class VotoOpcionBean implements Serializable {
	
	private static final long serialVersionUID = 6439276668659919711L;
	
	private Integer  idDetActaOpcion;
    private Long    posicion;
    private String  votos;
    private String  errorMaterial;
    private String  ilegible;
    private String  activo;
    private Integer  estado;

}
