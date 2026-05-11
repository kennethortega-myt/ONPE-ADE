package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import java.util.Date;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "cab_catalogo")
public class ImportOrcCatalogo {

    @Id
    @Column(name = "n_catalogo_pk")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_catalogo_padre", referencedColumnName = "n_catalogo_pk", nullable = false)
    private ImportOrcCatalogo catalogoPadre;
    
    @Column(name = "c_maestro")
    private String maestro;

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
