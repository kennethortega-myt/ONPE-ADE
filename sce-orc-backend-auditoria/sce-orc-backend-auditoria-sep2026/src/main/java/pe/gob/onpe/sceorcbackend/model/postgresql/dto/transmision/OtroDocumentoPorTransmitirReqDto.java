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
public class OtroDocumentoPorTransmitirReqDto implements Serializable  {
	
	private static final long serialVersionUID = 3670517629388813107L;
	
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
