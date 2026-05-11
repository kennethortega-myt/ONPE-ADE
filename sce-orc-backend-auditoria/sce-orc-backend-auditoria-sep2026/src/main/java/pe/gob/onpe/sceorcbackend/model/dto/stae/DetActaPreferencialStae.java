package pe.gob.onpe.sceorcbackend.model.dto.stae;

import lombok.Data;

import java.io.Serializable;

@Data
public class DetActaPreferencialStae implements Serializable {

	private static final long serialVersionUID = -5502312644101153142L;
	private String estadoErrorMaterial;
    private Integer lista;
    private Long votos;

}
