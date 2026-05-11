package pe.gob.onpe.scebackend.model.dto.transmision;

import java.io.Serializable;

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
public class DetOtroDocumentoPorTransmitirDto implements Serializable {

	private static final long serialVersionUID = -5918809424342805415L;
	
	private Long id;
	private String idCc;
	private String codigoMesa;
	private String codTipoDocumento;
	private String codTipoPerdida;
	private Integer activo;
	private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;
    private OtroDocumentoPorTransmitirDto cabOtroDocumento;
	
}
