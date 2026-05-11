package pe.gob.onpe.scebackend.model.dto.transmision;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PrUpdatedDto implements Serializable {

	private static final long serialVersionUID = -3800704819537488334L;
	private Integer idFila;
	private Integer estado;
	
}
