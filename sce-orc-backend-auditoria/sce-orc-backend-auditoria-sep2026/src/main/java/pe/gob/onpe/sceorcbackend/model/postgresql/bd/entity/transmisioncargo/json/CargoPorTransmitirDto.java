package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmisioncargo.json;



import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
@JsonIgnoreProperties(ignoreUnknown = true)
public class CargoPorTransmitirDto implements Serializable {

	private static final long serialVersionUID = -5146418673663077250L;
	
	private Long idCentroComputo;
    private String proceso;
    private Long idActa;
    private List<DetActaFormatoDto> detallesActaFormato;

}
