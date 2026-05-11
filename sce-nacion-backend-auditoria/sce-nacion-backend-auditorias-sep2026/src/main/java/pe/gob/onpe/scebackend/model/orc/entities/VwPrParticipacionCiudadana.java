package pe.gob.onpe.scebackend.model.orc.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "vw_pr_participacion_ciudadana")
public class VwPrParticipacionCiudadana implements Serializable {

	private static final long serialVersionUID = 5313044592036862874L;

	@Id
	@Column(name = "n_id_fila")
	@JsonProperty("n_id_fila")
	private Integer idFila;
	
	@Column(name = "c_tipo_filtro")
	@JsonProperty("c_tipo_filtro")
	private String  tipoFiltro;
	
	@Column(name = "n_ambito_geografico")
	@JsonProperty("n_ambito_geografico")
	private Integer ambitoGeografico;
	
	@Column(name = "n_ubigeo_nivel_01")
	@JsonProperty("n_ubigeo_nivel_01")
	private Integer ubigeoNivel01;
	
	@Column(name = "n_ubigeo_nivel_02")
	@JsonProperty("n_ubigeo_nivel_02")
	private Integer ubigeoNivel02;
	
	@Column(name = "n_ubigeo_nivel_03")
	@JsonProperty("n_ubigeo_nivel_03")
	private Integer ubigeoNivel03;
	
	@Column(name = "n_local_votacion")
	@JsonProperty("n_local_votacion")
	private Long    idLocalVotacion;
	
	@Column(name = "n_total_electores_habiles")
	@JsonProperty("n_total_electores_habiles")
	private Integer totalElectoresHabiles;
	
	@Column(name = "n_total_asistentes")
	@JsonProperty("n_total_asistentes")
	private Integer totalAsistentes;
	
	@Column(name = "n_total_ausentes")
	@JsonProperty("n_total_ausentes")
	private Integer totalAusentes;
	
	@Column(name = "n_porcentaje_asistentes")
	@JsonProperty("n_porcentaje_asistentes")
	private Double  porcentajeAsistentes;
	
	@Column(name = "n_porcentaje_ausentes")
	@JsonProperty("n_porcentaje_ausentes")
	private Double  porcentajeAusentes;
	
}
