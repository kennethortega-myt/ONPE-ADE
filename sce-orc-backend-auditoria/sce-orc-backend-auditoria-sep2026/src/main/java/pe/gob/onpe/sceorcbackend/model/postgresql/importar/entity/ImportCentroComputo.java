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
@Table(name = "mae_centro_computo")
public class ImportCentroComputo implements Serializable {

	private static final long serialVersionUID = 2752019017176283778L;

	@Id
	@Column(name = "n_centro_computo_pk")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_centro_computo_padre", referencedColumnName = "n_centro_computo_pk", nullable = false)
	private ImportCentroComputo centroComputoPadre;

	@Column(name = "c_codigo")
	private String codigo;

	@Column(name = "c_nombre")
	private String nombre;

	@Column(name = "c_ip_app_backend_cc")
	private String ipBackendCc;

	@Column(name = "n_puerto_app_backend_cc")
	private Integer puertoBackedCc;

	@Column(name = "c_apitoken_app_backend_cc")
	private String apiTokenBackedCc;

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
