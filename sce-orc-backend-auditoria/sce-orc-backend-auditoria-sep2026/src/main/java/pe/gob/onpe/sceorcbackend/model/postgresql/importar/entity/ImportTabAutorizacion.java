package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;



import lombok.*;
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
@Table(name = "tab_autorizacion")
public class ImportTabAutorizacion implements Serializable {

	private static final long serialVersionUID = 3739084144320042064L;

    @Id
    @Column(name = "n_tab_autorizacion_pk")
    private Long    id;

    @Column(name ="n_numero_autorizacion")
    private Long numeroAutorizacion;

    @Column(name ="c_estado_aprobacion")
    private String estadoAprobacion;

    @Column(name ="n_autorizacion")
    private Integer autorizacion;

    @Column(name ="c_tipo_autorizacion")
    private String tipoAutorizacion;

    @Column(name ="c_detalle")
    private String detalle;

    @Column(name ="n_activo")
    private Integer activo;

    @Column(name ="c_aud_usuario_creacion")
    private String usuarioCreacion;

    @Column(name ="d_aud_fecha_creacion")
    private Date fechaCreacion;

    @Column(name ="c_aud_usuario_modificacion")
    private String usuarioModificacion;

    @Column(name ="d_aud_fecha_modificacion")
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
