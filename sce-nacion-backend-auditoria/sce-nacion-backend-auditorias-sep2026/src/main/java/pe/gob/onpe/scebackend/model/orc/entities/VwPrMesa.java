package pe.gob.onpe.scebackend.model.orc.entities;

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

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Entity
@Table(name = "vw_pr_mesa")
public class VwPrMesa implements Serializable {

	private static final long serialVersionUID = 5313044592036862874L;

	@Id
	@Column(name = "n_id_fila")
	private Long id;
	
	@Column(name = "c_tipo_filtro")
	private String tipoFiltro;
	
	@Column(name = "n_distrito_electoral")
	private Long idDistritoElectoral;
	
	@Column(name = "n_ambito_geografico")
	private Long ambitoGeografico;
	
	@Column(name = "n_ubigeo_nivel_01")
	private Long ubigeoNivel01;
	
	@Column(name = "n_ubigeo_nivel_02")
	private Long ubigeoNivel02;
	
	@Column(name = "n_ubigeo_nivel_03")
	private Long ubigeoNivel03;
	
	@Column(name = "n_total_mesas")
	private Long totalMesas;
	
	@Column(name = "n_mesas_instaladas")
	private Long mesasInstaladas;
	
	@Column(name = "n_mesas_no_instaladas")
	private Long mesasNoInstaladas;
	
	@Column(name = "n_mesas_por_informar")
	private Long mesasPorInformar;
	
}
