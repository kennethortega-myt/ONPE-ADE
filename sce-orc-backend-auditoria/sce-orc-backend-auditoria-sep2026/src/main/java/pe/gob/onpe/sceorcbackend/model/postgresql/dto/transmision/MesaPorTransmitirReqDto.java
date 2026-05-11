package pe.gob.onpe.sceorcbackend.model.postgresql.dto.transmision;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.DetOtroDocumentoPorTransmitirDto;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MesaPorTransmitirReqDto implements Serializable { 
	
	private static final long serialVersionUID = -8402100960787688860L;

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
	private List<OmisoVotantePorTransmitirReqDto> omisosVotantes;
	private List<MiembroMesaSorteadoPorTransmitirReqDto> miembrosMesaSorteado;
	private List<MiembroMesaColaPorTransmitirReqDto> miembrosMesaCola;
	private List<PersoneroPorTransmitirReqDto> personeros;
	private List<MiembroMesaEscrutinioPorTransmitirReqDto> miembrosMesaEscrutinio;
	private List<MesaArchivoPorTransmitirReqDto> detalleArchivo;
	private List<DetOtroDocumentoPorTransmitirDto> detOtroDocumento;
	
}
