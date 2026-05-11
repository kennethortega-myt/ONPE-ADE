package pe.gob.onpe.scebackend.model.orc.entities;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "det_distrito_electoral_eleccion")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetDistritoElectoralEleccion implements Serializable {

	private static final long serialVersionUID = 415110700747606015L;
	
	@Id
	@Column(name = "n_det_distrito_electoral_eleccion_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_distrito_electoral_eleccion")
    @SequenceGenerator(name = "generator_det_distrito_electoral_eleccion", sequenceName = "seq_det_distrito_electoral_eleccion_pk", allocationSize = 1)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_eleccion", referencedColumnName = "n_eleccion_pk", nullable = false)
	private Eleccion eleccion;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_distrito_electoral", referencedColumnName = "n_distrito_electoral_pk", nullable = false)
	private DistritoElectoral distritoElectoral;
	
	@Column(name = "n_cantidad_curules")
	private Integer cantidadCurules;
	
	@Column(name = "n_cantidad_candidatos")
	private Integer cantidadCandidatos;
	
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
