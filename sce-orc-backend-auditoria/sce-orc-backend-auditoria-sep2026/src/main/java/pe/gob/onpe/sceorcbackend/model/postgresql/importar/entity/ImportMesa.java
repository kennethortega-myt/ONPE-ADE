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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tab_mesa")
public class ImportMesa implements Serializable {

	private static final long serialVersionUID = -6670025641653069615L;

	@Id
	@Column(name = "n_mesa_pk")
	private Long	id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_local_votacion", referencedColumnName = "n_local_votacion_pk", nullable = false)
	private ImportLocalVotacion localVotacion;
	
	@Column(name = "c_mesa")
	private String  codigo;
	
	@Column(name = "n_cantidad_electores_habiles")
	private Integer cantidadElectoresHabiles;
	
	@Column(name = "n_cantidad_electores_habiles_extranjeros")
	private Integer cantidadElectoresHabilesExtranjeros;
	
	@Column(name = "n_discapacidad")
	private Integer discapacidad;
	
	@Column(name = "n_solucion_tecnologica")
	private Long	solucionTecnologica;
	
	@Column(name = "c_estado_mesa")
	private String 	estadoMesa;
	
	@Column(name = "c_estado_digitalizacion_le")
	private String 	estadoDigitalizacionLe;
	
	@Column(name = "c_estado_digitalizacion_mm")
	private String 	estadoDigitalizacionMm;

	@Column(name = "c_usuario_asignado_le")
	private String  usuarioAsignadoLe;

	@Column(name = "d_aud_fecha_usuario_asignado_le")
	private Date	fechaAsignadoLe;

	@Column(name = "c_usuario_asignado_mm")
	private String  usuarioAsignadoMm;

	@Column(name = "d_aud_fecha_usuario_asignado_mm")
	private Date	fechaAsignadoMm;
	
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
