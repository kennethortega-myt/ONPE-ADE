package pe.gob.onpe.sceorcbackend.model.dto.test;


import lombok.Data;
import java.io.Serializable;

@Data
public class DocumentoDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	private String codigo;
    private String nombreEleccion;
    private String rangoInicial;
    private String rangoFinal;
}
