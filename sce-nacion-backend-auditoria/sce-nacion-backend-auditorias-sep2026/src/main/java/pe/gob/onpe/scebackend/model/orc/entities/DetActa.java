package pe.gob.onpe.scebackend.model.orc.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
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
@Table(name = "det_acta")
public class DetActa implements Serializable {

	private static final long serialVersionUID = 7997885618925098957L;

	@Id
	@Column(name = "n_det_acta_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_acta")
    @SequenceGenerator(name = "generator_det_acta", sequenceName = "seq_det_acta_pk", allocationSize = 1)
	private Long	id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_acta", referencedColumnName = "n_acta_pk", nullable = false)
	private Acta acta;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_agrupacion_politica", referencedColumnName = "n_agrupacion_politica_pk", nullable = false, insertable = false, updatable = false)
	private AgrupacionPolitica agrupacionPolitica;
	
	@Column(name = "n_agrupacion_politica", insertable = true, updatable = true)
	private Long	idAgrupacionPolitica;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "detActa", cascade = CascadeType.ALL)
	@EqualsAndHashCode.Exclude private Set<DetActaPreferencial> preferenciales;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "detActa", cascade = CascadeType.ALL)
	@EqualsAndHashCode.Exclude private Set<DetActaOpcion> opciones;
	
	@Column(name = "n_posicion")
	private Long	posicion;
	
	@Column(name = "n_votos")
	private Long	votos;
	
	@Column(name = "n_estado")
	private Integer	estado;
	
	@Column(name = "n_votos_automatico")
	private Long votosAutomatico;
	
	@Column(name = "n_votos_manual_1")
	private Long votosManual1;
	
	@Column(name = "n_votos_manual_2")
	private Long votosManual2;
	
	@Column(name = "c_estado_error_material")
	private String  estadoErrorMaterial;
	
	@Column(name = "c_ilegible")
	private String 	ilegible;
	
	@Column(name = "c_ilegible_v1")
	private String 	ilegiblev1;
	
	@Column(name = "c_ilegible_v2")
	private String 	ilegiblev2;
	
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
