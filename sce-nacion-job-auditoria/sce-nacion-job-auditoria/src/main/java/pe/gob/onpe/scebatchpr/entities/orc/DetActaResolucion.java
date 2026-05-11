package pe.gob.onpe.scebatchpr.entities.orc;


import lombok.*;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

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

}
