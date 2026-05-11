package pe.gob.onpe.scebackend.model.orc.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import pe.gob.onpe.scebackend.utils.SceConstantes;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "det_acta_accion")
public class DetActaAccion implements Serializable {

	private static final long serialVersionUID = 5011052309918560470L;

	@Id
    @Column(name = "n_det_acta_accion_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_acta_accion")
    @SequenceGenerator(name = "generator_det_acta_accion", sequenceName = "seq_det_acta_accion_pk", allocationSize = 1)
	private Long	id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_acta", referencedColumnName = "n_acta_pk", nullable = false)
    private Acta acta;
    
    @Column(name ="c_id_det_acta_accion_cc")
    private String idCc;

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
	private String 	usuarioCreacion;
	
	@Column(name = "d_aud_fecha_creacion")
	private Date  	fechaCreacion;
	
	@Column(name = "c_aud_usuario_modificacion")
	private String	usuarioModificacion;
	
	@Column(name = "d_aud_fecha_modificacion")
	private Date	fechaModificacion;
	
	@PrePersist
    public void setDefaultValues() {
        if (Objects.isNull(activo)) {
            activo = SceConstantes.ACTIVO;
        }

        if (Objects.isNull(fechaCreacion)) {
            fechaCreacion = new Date();
        }
    }

}