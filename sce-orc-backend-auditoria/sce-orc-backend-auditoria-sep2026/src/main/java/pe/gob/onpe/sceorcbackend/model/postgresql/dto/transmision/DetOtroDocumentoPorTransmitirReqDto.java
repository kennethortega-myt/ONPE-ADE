package pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DetOtroDocumentoPorTransmitirReqDto implements Serializable  {
	
	private static final long serialVersionUID = 3998335465935527589L;

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
    private OtroDocumentoPorTransmitirReqDto cabOtroDocumento;
	
}
