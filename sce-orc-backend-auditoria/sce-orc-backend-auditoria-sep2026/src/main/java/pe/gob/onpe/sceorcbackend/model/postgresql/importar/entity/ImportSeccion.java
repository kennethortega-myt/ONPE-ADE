package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;



import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "tab_seccion")
public class ImportSeccion implements Serializable {

	private static final long serialVersionUID = 6387838507596496395L;

	@Id
    @Column(name = "n_seccion_pk")
    private Integer id;

    @Column(name = "c_nombre")
    private String nombre;

    @Column(name = "c_abreviatura")
    private String abreviatura;

    @Column(name = "n_activo")
    private Integer activo;
    
    @Column(name = "n_orientacion")
    private Integer orientacion;

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
