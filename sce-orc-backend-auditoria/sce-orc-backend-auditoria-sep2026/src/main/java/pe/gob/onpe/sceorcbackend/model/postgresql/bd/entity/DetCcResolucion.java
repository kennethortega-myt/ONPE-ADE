package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.PrePersist;
import lombok.Data;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;
import java.util.Date;
import java.util.Objects;

@Data
@Entity
@Table(name = "det_cc_resolucion")
public class DetCcResolucion {

	@Id
	@Column(name = "n_det_cc_resolucion_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_cc_resolucion")
	@SequenceGenerator(name = "generator_det_cc_resolucion", sequenceName = "seq_det_cc_resolucion_pk", allocationSize = 1)
	private Long	id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_cc_resolucion", referencedColumnName = "n_cc_resolucion_pk", nullable = false)
	private CabCcResolucion cabCcResolucion;

	@Column(name = "n_det_acta")
	private Long	idDetActa;

	@Column(name = "n_agrupacion_politica")
	private Integer	idAgrupacionPolitica;
	
	@Column(name = "n_posicion")
	private Integer	posicion;

	@Column(name = "n_estado")
	private Integer	estado;
	
	@Column(name = "n_votos")
	private Integer	votos;

	@Column(name = "c_estado_error_material")
	private String  estadoErrorMaterial;
	
	@Column(name = "c_ilegible")
	private String 	ilegible;
	
	@Column(name = "n_activo")
	private Integer activo;
	
	@Column(name = "c_aud_usuario_creacion")
	private String 	usuarioCreacion;
	
	@Column(name = "d_aud_fecha_creacion")
	private Date  	fechaCreacion;
	
	@Column(name = "c_aud_usuario_modificacion")
	private String	usuarioModificacion;
	
	@Column(name = "d_aud_fecha_modificacion")
	private Date	fechaModificacion;

	@Override
	public String toString() {
		return "DetActa{" +
				"id=" + id +
				", posicion=" + posicion +
				", votos=" + votos +
				'}';
	}

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
