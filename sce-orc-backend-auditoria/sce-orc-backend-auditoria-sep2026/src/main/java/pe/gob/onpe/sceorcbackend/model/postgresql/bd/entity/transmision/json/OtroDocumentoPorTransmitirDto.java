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
public class OtroDocumentoPorTransmitirDto implements Serializable  {

	private static final long serialVersionUID = 1479909062804103021L;
	
	private Long id;
	private String idCc;
	private String codigoCentroComputo;
	private String numeroDocumento;
	private String codTipoDocumento;
	private Integer numeroPaginas;
	private String estadoDigitalizacion;
	private String estadoDocumento;
	private String usuarioControl;
	private String fechaUsuarioControl;
	private Integer activo;
	private String audUsuarioCreacion;
    private String audFechaCreacion;
    private String audUsuarioModificacion;
    private String audFechaModificacion;
	
	
}
