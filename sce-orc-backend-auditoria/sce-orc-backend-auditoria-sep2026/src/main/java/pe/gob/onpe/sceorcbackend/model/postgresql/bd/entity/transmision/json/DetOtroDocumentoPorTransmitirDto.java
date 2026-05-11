package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json;

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
	
	private static final long serialVersionUID = -1971818125524915052L;
	
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
