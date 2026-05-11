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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "det_acta_historial")
public class ImportDetActaHistorial implements Serializable {

	private static final long serialVersionUID = -1617971100772925050L;

	@Id
	@Column(name = "n_det_acta_historial_pk")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_acta_historial", referencedColumnName = "n_acta_historial_pk", nullable = false)
	private ImportActaHistorial actaHistorial;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "detActaHistorial", cascade = CascadeType.ALL)
	private Set<ImportDetActaPreferencialHistorial> preferenciales;

	@Column(name = "n_agrupacion_politica")
	private Long idAgrupacionPolitica;

	@Column(name = "n_posicion")
	private Long posicion;

	@Column(name = "n_votos")
	private Long votos;

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
