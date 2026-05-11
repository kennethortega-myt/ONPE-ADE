package pe.gob.onpe.scebackend.model.dto.transmision;

import java.io.Serializable;
import java.util.List;

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
public class MesaPorTransmitirDto implements Serializable {

	private static final long serialVersionUID = -4953262659113959684L;
	
	private Long idMesa;
	private String estado;
	private String estadoDigitalizacionLe;
	private String estadoDigitalizacionMm;
	private String estadoDigitalizacionPr;
	private String estadoDigitalizacionMe;
	private String usuarioAsignadoLe;
	private String usuarioAsignadoMm;
	private String usuarioAsignadoPr;
	private String usuarioAsignadoMe;
	private String fechaAsignadoLe;
	private String fechaAsignadoMm;
	private String fechaAsignadoPr;
	private String fechaAsignadoMe;
	private Integer activo;
	private String usuarioCreacion;
	private String usuarioModificacion;
	private String fechaCreacion; 
	private String fechaModificacion;
	private List<OmisoVotantePorTransmitirDto> omisosVotantes;
	private List<MiembroMesaSorteadoPorTransmitirDto> miembrosMesaSorteado;
	private List<MiembroMesaColaPorTransmitirDto> miembrosMesaCola;
	private List<PersoneroPorTransmitirDto> personeros;
	private List<MiembroMesaEscrutinioPorTransmitirDto> miembrosMesaEscrutinio;
	private List<MesaArchivoPorTransmitirDto> detalleArchivo;
	private List<DetOtroDocumentoPorTransmitirDto> detOtroDocumento;
	
}
