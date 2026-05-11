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
public class ActaPorTransmitirDto implements Serializable {

	private static final long serialVersionUID = -4337612218162091165L;
	
	private Long idCentroComputo;
	private String proceso;
	private Long idActa;
	private Long idActaCeleste;
	private String idCc;
	private String idCcCeleste;
	private Long idMesa;
	private MesaPorTransmitirDto mesa;
	private Long idDetUbigeoEleccion;
	private Long idArchivoEscrutinio;
	private Long idArchivoInstalacionSufragio;
	private String numeroCopia;
	private String numeroLote;
	private String digitoChequeoEscrutinio;
	private String digitoChequeoInstalacion;
	private String digitoChequeoSufragio;
	private String tipoLote;
	private Long electoresHabiles;
	private Long cvas;
	private Long cvasAutomatico;
	private Long cvasv1;
	private Long cvasv2;
	private String 	ilegibleCvas;
	private String 	ilegibleCvasv1;
	private String 	ilegibleCvasv2;
	private Long votosCalculados;
	private Long totalVotos;
	private String estadoActa;
	private String estadoCc;
	private String estadoActaResolucion;
	private String estadoDigitalizacion;
	private String estadoErrorMaterial;
	private Long digitalizacionEscrutinio;
	private Long digitalizacionInstalacion;
	private Long digitalizacionSufragio;
	private Long digitalizacionInstalacionSufragio;
	private Long controlDigEscrutinio;
	private Long controlDigInstalacionSufragio;
	private String observDigEscrutinio;
	private String observDigInstalacionSufragio;
	private Long digitacionHoras;
	private Long digitacionVotos;
	private Long digitacionObserv;
	private Long digitacionFirmasAutomatico;
	private Long digitacionFirmasManual;
	private Long controlDigitacion;
	private String horaEscrutinioAutomatico;
	private String horaEscrutinioManual;
	private String horaInstalacionAutomatico;
	private String horaInstalacionManual;
	private String descripcionObservAutomatico;
	private String descripcionObservManual;
	private Long escrutinioFirmaMm1Automatico;
	private Long escrutinioFirmaMm1Manual;
	private Long escrutinioFirmaMm2Automatico;
	private Long escrutinioFirmaMm2Manual;
	private Long escrutinioFirmaMm3Automatico;
	private Long escrutinioFirmaMm3Manual;
	private Long instalacionFirmaMm1Automatico;
	private Long instalacionFirmaMm1Manual;
	private Long instalacionFirmaMm2Automatico;
	private Long instalacionFirmaMm2Manual;
	private Long instalacionFirmaMm3Automatico;
	private Long instalacionFirmaMm3Manual;
	private Long sufragioFirmaMm1Automatico;
	private Long sufragioFirmaMm1Manual;
	private Long sufragioFirmaMm2Automatico;
	private Long sufragioFirmaMm2Manual;
	private Long sufragioFirmaMm3Automatico;
	private Long sufragioFirmaMm3Manual;
	private String verificador;
	private String verificador2;
	private Integer activo;
	private String audUsuarioCreacion;
	private String audFechaCreacion;
	private String audUsuarioModificacion;
	private String audFechaModificacion;
	private Integer tipoTransmision;
	private Long solucionTecnologica;
	private Integer asignado;
	private ArchivoTransmisionDto archivoEscrutinio;
	private ArchivoTransmisionDto archivoInstalacionSufragio;
	private ArchivoTransmisionDto archivoEscrutinioPdf;
	private ArchivoTransmisionDto archivoInstalacionSufragioPdf;
	private List<DetActaPorTransmitirDto> detalle;
	private List<DetActaAccionPorTransmitirDto> acciones;
	private List<DetActaResolucionPorTransmitirDto> resoluciones;
	private List<DetOficioPorTransmistirDto> detallesOficio;
	
	private ActaPorTransmitirDto actaCeleste;
	

}
