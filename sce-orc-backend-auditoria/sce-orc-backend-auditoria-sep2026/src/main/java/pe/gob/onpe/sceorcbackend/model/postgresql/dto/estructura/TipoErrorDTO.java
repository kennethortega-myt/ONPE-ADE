package pe.gob.onpe.sceorcbackend.model.postgresql.dto.estructura;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class TipoErrorDTO implements Serializable {
	private static final long serialVersionUID = -2363853955165035630L;
	private String nombre;
    private String codigo;
}
