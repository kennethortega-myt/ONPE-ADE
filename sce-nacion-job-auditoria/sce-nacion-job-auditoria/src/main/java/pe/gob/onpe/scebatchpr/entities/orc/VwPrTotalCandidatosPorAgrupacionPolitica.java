package pe.gob.onpe.scebatchpr.entities.orc;


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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@Entity
@Table(name = "vw_total_candidatos_por_agrupacion_politica")
public class VwPrTotalCandidatosPorAgrupacionPolitica {

	@Id
	@Column(name = "n_id_fila")
	@JsonProperty("n_id_fila")
	private Integer idFila;
	
	@Column(name = "n_ambito_geografico")
	@JsonProperty("n_ambito_geografico")
	private Integer ambitoGeografico;
	
	@Column(name = "n_distrito_electoral")
	@JsonProperty("n_distrito_electoral")
    private Integer distritoElectoral;
	
	@Column(name = "n_ubigeo_nivel_01")
	@JsonProperty("n_ubigeo_nivel_01")
    private Integer ubigeoNivel01;
	
	@Column(name = "n_ubigeo_nivel_02")
	@JsonProperty("n_ubigeo_nivel_02")
    private Integer ubigeoNivel02;
	
	@Column(name = "n_ubigeo")
	@JsonProperty("n_ubigeo")
    private Integer ubigeo;
	
	@Column(name = "n_det_ubigeo_eleccion")
	@JsonProperty("n_det_ubigeo_eleccion")
    private Integer detUbigeoEleccion;
	
	@Column(name = "n_agrupacion_politica")
	@JsonProperty("n_agrupacion_politica")
    private Integer agrupacionPolitica;
	
	@Column(name = "n_posicion")
	@JsonProperty("n_posicion")
    private Integer posicion;
	
	@Column(name = "c_codigo")
	@JsonProperty("c_codigo")
    private String codigo;
	
	@Column(name = "c_descripcion")
	@JsonProperty("c_descripcion")
    private String descripcion;
	
}
