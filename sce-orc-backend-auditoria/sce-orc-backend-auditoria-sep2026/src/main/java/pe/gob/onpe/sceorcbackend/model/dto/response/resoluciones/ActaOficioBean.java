package pe.gob.onpe.sceorcbackend.model.dto.response.resoluciones;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ActaOficioBean implements Serializable {	

	private static final long serialVersionUID = 3251583881200644985L;
	
	private String mesa;
	private String copia;
	private String eleccion;
	private String digitoChequeo;
	private String sobre;
	
}
