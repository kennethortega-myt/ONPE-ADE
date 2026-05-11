package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;

import lombok.*;
import pe.gob.onpe.sceorcbackend.utils.RedUtils;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

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

    @Column(name ="c_accion")
    private String accion; //es el proceso que se realiza

    @Column(name ="c_tiempo")
    private String tiempo; //INICIO o FIN

    @Column(name ="n_iteracion")
    private Integer iteracion;

    @Column(name ="n_orden")
    private Integer orden;

    @Column(name ="c_usuario_accion")
    private String usuarioAccion;

    @Column(name ="d_fecha_accion")
    private Date fechaAccion;

    @Column(name = "c_codigo_centro_computo")
    private String 	codigoCentroComputo;

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
	
	@Column(name = "c_ip_servidor_cliente_transmision")
	private String ipServer;

	@Column(name = "c_nombre_servidor_cliente_transmision")
	private String hostName;
	
	@PrePersist
    public void setDefaultValues() {
        if (Objects.isNull(activo)) {
            activo = SceConstantes.ACTIVO;
        }

        if (Objects.isNull(fechaCreacion)) {
            fechaCreacion = new Date();
        }
        
        if (Objects.isNull(this.ipServer)) {
    		this.ipServer = RedUtils.obtenerIpLocal();
    	}

    	if (Objects.isNull(this.hostName)) {
    		this.hostName = RedUtils.obtenerNombreHost();
    	}
    }

}
