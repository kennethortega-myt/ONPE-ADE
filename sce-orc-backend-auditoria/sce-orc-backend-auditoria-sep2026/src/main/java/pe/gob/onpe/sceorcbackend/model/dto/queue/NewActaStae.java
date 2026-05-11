package pe.gob.onpe.sceorcbackend.model.dto.queue;

import java.io.Serializable;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NewActaStae implements Serializable {

	private static final long serialVersionUID = -8893681985247253892L;
	
	private Long actaId;
	private String codUsuario;
	private String codCentroComputo;
	
}
