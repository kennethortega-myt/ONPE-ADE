package pe.gob.onpe.scebackend.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class CabActaDto {

	private Long   	id;
	private Long   	idMesa; // foranea
	private Long   	idDetUbigeoEleccion; // foranea
	private Long   	idArchivoEscrutinio; // foranea
	private Long   	idArchivoInstalacionSufragio; // foranea
	private String 	numeroCopia;
	private String 	numeroLote;
	private String 	tipoLote;
	private Long   	electoresHabiles;
	private Long   	cvas;
	private Long	votosCalculados;
	private Long	totalVotos;
	private String  estadoActa;
	private String  estadoCc;
	private String  estadoActaResolucion;
	private String  estadoDigitalizacion;
	private String  estadoErrorMaterial;
	private Long	digitalizacionEscrutinio;
	private Long 	digitalizacionInstalacionSufragio;
	private Long	controlDigEscrutinio;
	private Long	controlDigInstalacionSufragio;
	private String	observDigEscrutinio;
	private String	observDigInstalacionSufragio;
	private Long    digitacionHoras;
	private Long	digitacionVotos;
	private Long	digitacionObserv;
	private Long	digitacionFirmasAutomatico;
	private Long	digitacionFirmasManual;
	private Long	controlDigitacion;
	private String  horaEscrutinioAutomatico;
	private String 	horaEscrutinioManual;
	private String	horaInstalacionAutomatico;
	private String  horaInstalacionManual;
	private String 	descripcionObservAutomatico;
	private String	descripcionObservManual;
	private Long	escrutinioFirmaMm1Automatico;
	private Long	escrutinioFirmaMm2Automatico;
	private Long	escrutinioFirmaMm3Automatico;
	private Long	instalacionFirmaMm1Automatico;
	private Long	instalacionFirmaMm2Automatico;
	private Long	instalacionFirmaMm3Automatico;
	private Long	sufragioFirmaMm1Automatico;
	private Long	sufragioFirmaMm2Automatico;
	private Long	sufragioFirmaMm3Automatico;
	private Integer activo;
	private String 	audUsuarioCreacion;
	private String  audFechaCreacion;
	private String	audUsuarioModificacion;
	private String	audFechaModificacion;
	private String  formatoArchivoEscrutinio;
	private String  formatoArchivoInstalacionSufragio;
	private String  contentArchivoEscrutinio;
	private String  contentArchivoInstalacionSufragio;
	private List<DetActaDto> detalleActas;
	
}
