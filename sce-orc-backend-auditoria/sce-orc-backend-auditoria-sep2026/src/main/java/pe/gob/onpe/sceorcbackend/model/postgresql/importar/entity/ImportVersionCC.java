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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tab_version_cc")
public class ImportVersionCC implements Serializable {

    private static final long serialVersionUID = -4442038762270754984L;

    @Id
    @Column(name = "n_version_cc_pk")
    private Long id;

    @Column(name = "c_cadena")
    private String cadena;

    @Column(name = "c_version")
    private String codversion;

    @Column(name = "d_fecha_version")
    private Date fechaVersion;

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
