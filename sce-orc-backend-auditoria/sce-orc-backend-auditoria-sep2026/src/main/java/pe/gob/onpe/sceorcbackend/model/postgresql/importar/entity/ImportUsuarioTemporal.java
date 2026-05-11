package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "tab_usuario_temporal")
public class ImportUsuarioTemporal implements Serializable {

	private static final long serialVersionUID = 6957396850057381727L;

	@Id
	@Column(name = "n_usuario_temporal_pk")
	private Long id;

	@Column(name = "c_usuario")
	private String usuario;

	@Column(name = "c_perfil")
	private String perfil;

	@Column(name = "n_sesion_activa")
	private Integer sesionActiva;

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
