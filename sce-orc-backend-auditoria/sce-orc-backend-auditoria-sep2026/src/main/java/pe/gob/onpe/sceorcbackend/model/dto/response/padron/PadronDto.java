package pe.gob.onpe.sceorcbackend.model.dto.response.padron;

import lombok.Data;

import java.io.Serializable;


@Data
public class PadronDto implements Serializable {

	private static final long serialVersionUID = 4780870164954155935L;
	private String nombres;
    private String apellidoMaterno;
    private String apellidoPaterno;
    private Long idPadron;

    public PadronDto() {
        super();
    }
}
