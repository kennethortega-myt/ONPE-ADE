package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tab_omiso_miembro_mesa")
public class ImportOmisoMiembroMesa implements Serializable {

	private static final long serialVersionUID = -7235094147761638791L;

    @Id
    @Column(name = "n_omiso_miembro_mesa_pk")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_mesa", referencedColumnName = "n_mesa_pk")
    private ImportMesa mesa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_miembro_mesa_sorteado", referencedColumnName = "n_miembro_mesa_sorteado_pk")
    private ImportMiembroMesaSorteado miembroMesaSorteado;

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
