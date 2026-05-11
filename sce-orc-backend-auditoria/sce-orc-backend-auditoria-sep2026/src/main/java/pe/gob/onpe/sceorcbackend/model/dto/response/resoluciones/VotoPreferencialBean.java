package pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class VotoPreferencialBean implements Serializable {

	private static final long serialVersionUID = -1063165033890352751L;
	
	private String idDetActaPreferencial;
    private Integer lista;
    private String votos;
    private String errorMaterial;
    private String ilegible;
    private String activo;
    private Integer estado;

}
