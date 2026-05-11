package pe.gob.onpe.scebackend.model.orc.entities;


import lombok.*;

import jakarta.persistence.*;
import pe.gob.onpe.scebackend.model.entities.DatosAuditoria;

import java.io.Serializable;

@Getter
@Setter
@ToString
@Entity
@Table(name = "det_acta_resolucion")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetActaResolucion extends DatosAuditoria implements Serializable {
	
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

}
