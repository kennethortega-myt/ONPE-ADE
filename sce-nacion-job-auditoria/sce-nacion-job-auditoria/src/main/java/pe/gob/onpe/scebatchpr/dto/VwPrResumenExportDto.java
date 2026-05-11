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
public class VwPrResumenExportDto {

	private Integer idFila;
	private Integer tipoEleccion;
	private String  tipoFiltro;
	private Integer ambitoGeografico;
	private Integer ubigeoNivel01;
	private Integer ubigeoNivel02;
	private Integer ubigeoNivel03;
	private Integer totalElectoresHabiles;
	private Integer participacionCiudadana;
	private Double  porcentParticipacionCiudadana;
	private Integer totalActas;
	private Integer totalVotosEmitidos;
	private Integer actasContabilizadas;
	private Double  porcentajeActasContabilizadas;
	private Integer actasObservadasEnviadas;
	private Double  porcentajeActasObservadasEnviadas;
	private Integer actasPendientes;
	private Double  porcentajeActasPendientes;
	private String  detalle;
	
}
