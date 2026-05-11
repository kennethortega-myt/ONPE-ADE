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
@Entity
@Table(name = "det_acta_resolucion")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetActaResolucion implements Serializable {
	
	private static final long serialVersionUID = 4113195803276814951L;

	@Id
	@Column(name = "n_det_acta_resolucion_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_acta_resolucion")
	@SequenceGenerator(name = "generator_det_acta_resolucion", sequenceName = "seq_det_acta_resolucion_pk", allocationSize = 1)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_acta", referencedColumnName = "n_acta_pk", nullable = false)
	private Acta acta;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_resolucion", referencedColumnName = "n_resolucion_pk", nullable = false)
	private TabResolucion resolucion;

	@Column(name = "c_estado_acta")
	private String estadoActa;

	@Column(name = "n_correlativo")
	private Integer correlativo;
	
	@Column(name = "n_activo")
	private Integer activo;
	
	@Column(name = "c_aud_usuario_creacion")
	private String 	usuarioCreacion;
	
	@Column(name = "d_aud_fecha_creacion")
	private Date fechaCreacion;
	
	@Column(name = "c_aud_usuario_modificacion")
	private String usuarioModificacion;
	
	@Column(name = "d_aud_fecha_modificacion")
	private Date fechaModificacion;

	@Column(name = "c_observacion_jne")
	private String observacionJne;
	
	@Column(name = "c_ip_servidor_cliente_transmision")
	private String ipServer;

	@Column(name = "c_nombre_servidor_cliente_transmision")
	private String hostName;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DetActaResolucion that = (DetActaResolucion) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}
	
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
