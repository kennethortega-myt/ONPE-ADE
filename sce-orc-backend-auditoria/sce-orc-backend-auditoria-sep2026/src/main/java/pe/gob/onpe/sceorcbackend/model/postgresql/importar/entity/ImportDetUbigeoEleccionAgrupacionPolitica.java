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



@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "det_ubigeo_eleccion_agrupacion_politica")
public class ImportDetUbigeoEleccionAgrupacionPolitica implements Serializable {

	private static final long serialVersionUID = -3180017922178467490L;

	@Id
	@Column(name = "n_det_ubigeo_eleccion_agrupacion_politica_pk")
	private Long	id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_agrupacion_politica", referencedColumnName = "n_agrupacion_politica_pk", nullable = false)
	private ImportAgrupacionPolitica agrupacionPolitica;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_det_ubigeo_eleccion", referencedColumnName = "n_det_ubigeo_eleccion_pk", nullable = false)
	private ImportUbigeoEleccion ubigeoEleccion;
	
	@Column(name = "n_posicion")
	private Integer posicion;
	
	@Column(name = "n_estado")
	private Integer estado;
	
	@Column(name = "n_activo")
	private Integer activo;
	
	@Column(name = "c_aud_usuario_creacion")
	private String 	usuarioCreacion;
	
	@Column(name = "d_aud_fecha_creacion")
	private Date  	fechaCreacion;
	
	@Column(name = "c_aud_usuario_modificacion")
	private String	usuarioModificacion;
	
	@Column(name = "d_aud_fecha_modificacion")
	@Setter(AccessLevel.NONE)
	private Date	fechaModificacion;


	@PrePersist
	public void prePersist() {
		this.fechaCreacion = new Date();
	}

	@PreUpdate
	public void preUpdate() {
		this.fechaModificacion = new Date();
	}
}
