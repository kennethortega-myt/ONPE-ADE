package pe.gob.onpe.scebatchpr.dto;

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
public class VwPrActaExportDto {


	private Long idActa;
	private Long idMesa;
	private Long  idSolucionTecnologica;
	private String  descripcionSolucionTecnologica;
	private String codigoMesa;
	private String numeroCopia;
	private String digitoChequeoEscrutinio;
	private String digitoChequeoInstalacion;
	private String digitoChequeoSufragio;
	private Long idUbigeoEleccion;
	private Long idEleccion;
	private Integer idAmbitoGeografico;
	private String  ubigeoNivel01;
	private String  ubigeoNivel02;
	private Long    idUbigeo;
	private String  ubigeoNombreNivel01;
	private String  ubigeoNombreNivel02;
	private String  ubigeoNombreNivel03;
	private String  centroPoblado;
	private Long    idLocalVotacion;
	private String  nombreLocalVotacion;
	private String  codigoLocalVotacion;
	private Integer totalElectoresHabiles;
	private Integer totalVotosEmitidos;
	private Integer totalVotosValidos;
	private Integer totalAsistentes;
	private Double  porcentajeParticipacionCiudadana;
	private String  estadoActa;
	private String  estadoComputo;
	private String  codigoEstadoActa;
	private String  descripcionEstadoActa;
	private String  descripcionSubEstadoActa;
	private String  estadoActaResolucion;
	private String  estadoDescripcionActaResolucion;
	private String  detalle;
	private String  lineaTiempo;
	
	
}
