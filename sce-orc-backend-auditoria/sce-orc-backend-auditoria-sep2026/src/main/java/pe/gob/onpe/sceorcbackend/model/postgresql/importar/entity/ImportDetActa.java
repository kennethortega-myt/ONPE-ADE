package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "det_acta")
public class ImportDetActa implements Serializable {

	private static final long serialVersionUID = 8228511470763268349L;

	@Id
	@Column(name = "n_det_acta_pk")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_acta", referencedColumnName = "n_acta_pk", nullable = false)
	private ImportActa acta;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_agrupacion_politica", referencedColumnName = "n_agrupacion_politica_pk", nullable = false)
	private ImportAgrupacionPolitica agrupacionPolitica;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "detActa", cascade = CascadeType.ALL)
	@EqualsAndHashCode.Exclude
	private Set<ImportDetActaPreferencial> preferenciales;

	@Column(name = "n_posicion")
	private Long posicion;

	@Column(name = "n_votos")
	private Long votos;

	@Column(name = "n_estado")
	private Integer estado;

	@Column(name = "n_votos_automatico")
	private Long votosAutomatico;

	@Column(name = "n_votos_manual_1")
	private Long votosManual1;

	@Column(name = "n_votos_manual_2")
	private Long votosManual2;

	@Column(name = "c_estado_error_material")
	private String estadoErrorMaterial;

	@Column(name = "c_ilegible")
	private String ilegible;

	@Column(name = "c_ilegible_v1")
	private String ilegiblev1;

	@Column(name = "c_ilegible_v2")
	private String ilegiblev2;

	@Column(name = "n_activo")
	private Integer activo;

	@Column(name = "c_aud_usuario_creacion")
	private String usuarioCreacion;

	@Column(name = "d_aud_fecha_creacion")
	private Date fechaCreacion;

	@Column(name = "c_aud_usuario_modificacion")
	private String usuarioModificacion;

	@Column(name = "d_aud_fecha_modificacion")
	@Setter(AccessLevel.NONE)
	private Date fechaModificacion;

	@PrePersist
	public void prePersist() {
		this.fechaCreacion = new Date();
	}

	@PreUpdate
	public void preUpdate() {
		this.fechaModificacion = new Date();
	}

}
