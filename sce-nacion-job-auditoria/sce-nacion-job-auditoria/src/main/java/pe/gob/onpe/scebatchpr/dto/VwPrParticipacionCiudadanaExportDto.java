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
public class VwPrParticipacionCiudadanaExportDto {

	private Integer idFila;
	private String  tipoFiltro;
	private Integer ambitoGeografico;
	private Integer ubigeoNivel01;
	private Integer ubigeoNivel02;
	private Integer ubigeoNivel03;
	private Long    idLocalVotacion;
	private Integer totalElectoresHabiles;
	private Integer totalAsistentes;
	private Integer totalAusentes;
	private Double  porcentajeAsistentes;
	private Double  porcentajeAusentes;

}
