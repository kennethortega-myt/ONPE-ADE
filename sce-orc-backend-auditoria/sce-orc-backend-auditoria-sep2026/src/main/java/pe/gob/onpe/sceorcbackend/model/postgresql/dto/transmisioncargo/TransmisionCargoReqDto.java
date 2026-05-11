package pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmisioncargo;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransmisionCargoReqDto implements Serializable {
	
	private static final long serialVersionUID = 2945481234969562328L;

	private Long idTransmision;
    private String centroComputo;
    private String acronimoProceso;
    private CargoPorTransmitirReqDto cargo;
    private String accion;
    private String fechaTransmision;
    private String fechaRegistro;
    private String usuarioTransmision;

}
