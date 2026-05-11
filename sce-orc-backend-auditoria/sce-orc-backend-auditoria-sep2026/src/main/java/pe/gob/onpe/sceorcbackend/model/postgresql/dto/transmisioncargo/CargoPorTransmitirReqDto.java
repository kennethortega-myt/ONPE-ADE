package pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmisioncargo;

import java.io.Serializable;
import java.util.List;

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
public class CargoPorTransmitirReqDto implements Serializable {

	private static final long serialVersionUID = -2679892311981800285L;
	
	private Long idCentroComputo;
    private String proceso;
    private Long idActa;
    private List<DetActaFormatoReqDto> detallesActaFormato;

}
