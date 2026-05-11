package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Entity
@Table(name = "det_distrito_electoral_eleccion")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportDetDistritoElectoralEleccion implements Serializable {

	private static final long serialVersionUID = 415110700747606015L;
	
	@Id
	@Column(name = "n_det_distrito_electoral_eleccion_pk")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_eleccion", referencedColumnName = "n_eleccion_pk", nullable = false)
	private ImportEleccion eleccion;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_distrito_electoral", referencedColumnName = "n_distrito_electoral_pk", nullable = false)
	private ImportDistritoElectoral distritoElectoral;
	
	@Column(name = "n_cantidad_curules")
	private Integer cantidadCurules;
	
	@Column(name = "n_cantidad_candidatos")
	private Integer cantidadCandidatos;
	
	@Column(name = "n_activo")
	private Integer activo;
	
	@Column(name = "c_aud_usuario_creacion")
	private String 	usuarioCreacion;
	
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
