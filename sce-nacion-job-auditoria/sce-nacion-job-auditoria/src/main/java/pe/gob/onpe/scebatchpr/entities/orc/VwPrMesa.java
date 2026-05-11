package pe.gob.onpe.scebatchpr.entities.orc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "vw_pr_mesa")
public class VwPrMesa {

	@Id
	@Column(name = "n_id_fila")
	@JsonProperty("n_id_fila")
	private Long id;
	
	@Column(name = "c_tipo_filtro")
	@JsonProperty("c_tipo_filtro")
	private String tipoFiltro;
	
	@Column(name = "n_ambito_geografico")
	@JsonProperty("n_ambito_geografico")
	private Long ambitoGeografico;
	
	@Column(name = "n_ubigeo_nivel_01")
	@JsonProperty("n_ubigeo_nivel_01")
	private Long ubigeoNivel01;
	
	@Column(name = "n_ubigeo_nivel_02")
	@JsonProperty("n_ubigeo_nivel_02")
	private Long ubigeoNivel02;
	
	@Column(name = "n_ubigeo_nivel_03")
	@JsonProperty("n_ubigeo_nivel_03")
	private Long ubigeoNivel03;
	
	@Column(name = "n_total_mesas")
	@JsonProperty("n_total_mesas")
	private Long totalMesas;
	
	@Column(name = "n_mesas_instaladas")
	@JsonProperty("n_mesas_instaladas")
	private Long mesasInstaladas;
	
	@Column(name = "n_mesas_no_instaladas")
	@JsonProperty("n_mesas_no_instaladas")
	private Long mesasNoInstaladas;
	
	@Column(name = "n_mesas_por_informar")
	@JsonProperty("n_mesas_por_informar")
	private Long mesasPorInformar;
	
}
