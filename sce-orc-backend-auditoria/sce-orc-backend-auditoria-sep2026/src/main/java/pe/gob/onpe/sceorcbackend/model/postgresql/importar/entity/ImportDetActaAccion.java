package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;

import lombok.*;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "det_acta_accion")
public class ImportDetActaAccion implements Serializable {

	private static final long serialVersionUID = 5011052309918560470L;

	@Id
    @Column(name = "n_det_acta_accion_pk")
	private Long	id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_acta", referencedColumnName = "n_acta_pk", nullable = false)
    private ImportActa acta;

    @Column(name ="c_accion")
    private String accion; 

    @Column(name ="c_tiempo")
    private String tiempo;

    @Column(name ="n_iteracion")
    private Integer iteracion;

    @Column(name ="n_orden")
    private Integer orden;

    @Column(name ="c_usuario_accion")
    private String usuarioAccion;

    @Column(name ="d_fecha_accion")
    private Date fechaAccion;

    @Column(name ="n_activo")
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
