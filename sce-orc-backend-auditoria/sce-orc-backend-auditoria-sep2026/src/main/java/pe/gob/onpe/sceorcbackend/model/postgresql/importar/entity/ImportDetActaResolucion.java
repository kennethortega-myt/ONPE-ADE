package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;


import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@Entity
@Table(name = "det_acta_resolucion")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportDetActaResolucion implements Serializable {
	
	private static final long serialVersionUID = 4113195803276814951L;

	@Id
	@Column(name = "n_det_acta_resolucion_pk")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_acta", referencedColumnName = "n_acta_pk", nullable = false)
	private ImportActa acta;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_resolucion", referencedColumnName = "n_resolucion_pk", nullable = false)
	private ImportTabResolucion resolucion;

	@Column(name = "c_estado_acta")
	private String estadoActa;

	@Column(name = "n_correlativo")
	private Integer correlativo;
	
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

	@Column(name = "c_observacion_jne")
	private String observacionJne;
	
	@PrePersist
	public void prePersist() {
		this.fechaCreacion = new Date();
	}

	@PreUpdate
	public void preUpdate() {
		this.fechaModificacion = new Date();
	}

}
