package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "tab_cierre_centro_computo")
public class ImportTabCierreCentroComputo implements Serializable {
	
	private static final long serialVersionUID = 1781391144197180215L;

	@Id
    @Column(name = "n_cierre_centro_computo_pk")
    private Long id;

    @Column(name = "n_centro_computo", nullable = false)
    private Integer centroComputo;

    @Column(name = "n_correlativo", nullable = false)
    private Integer correlativo;

    @Column(name = "d_fecha_cierre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCierre;

    @Column(name = "c_usuario_cierre", nullable = false, length = 50)
    private String usuarioCierre;

    @Column(name = "c_motivo_cierre", length = 350)
    private String motivoCierre;

    @Column(name = "n_reapertura", columnDefinition = "smallint default 0")
    private Integer reapertura;

    @Column(name = "d_fecha_reapertura")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaReapertura;

    @Column(name = "c_usuario_reapertura", nullable = false, length = 50)
    private String usuarioReapertura;

    @Column(name = "n_activo", columnDefinition = "smallint default 1")
    private Integer activo;

    @Column(name = "c_aud_usuario_creacion", nullable = false, length = 50)
    private String usuarioCreacion;

    @Column(name = "d_aud_fecha_creacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "c_aud_usuario_modificacion", length = 50)
    private String usuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    @Temporal(TemporalType.TIMESTAMP)
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
