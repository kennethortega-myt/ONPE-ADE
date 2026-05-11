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
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "mae_candidato")
public class Candidato implements Serializable {
	
	private static final long serialVersionUID = -6087409352500716915L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_candidato")
	@SequenceGenerator(name = "generator_candidato", sequenceName = "seq_tab_candidato_pk", allocationSize = 1)
	@Column(name = "n_candidato_pk")
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_distrito_electoral", referencedColumnName = "n_distrito_electoral_pk", nullable = false)
	private DistritoElectoral distritoElectoral;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_eleccion", referencedColumnName = "n_eleccion_pk", nullable = false)
	private Eleccion eleccion;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_agrupacion_politica", referencedColumnName = "n_agrupacion_politica_pk", nullable = false)
	private AgrupacionPolitica agrupacionPolitica;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_ubigeo", referencedColumnName = "n_ubigeo_pk", nullable = false)
	private Ubigeo ubigeo;
	
	@Column(name = "n_cargo")
	private Integer cargo;
	
	@Column(name = "c_documento_identidad")
	private String  documentoIdentidad;
	
	@Column(name = "c_apellido_paterno")
	private String  apellidoPaterno;
	
	@Column(name = "c_apellido_materno")
	private String  apellidoMaterno;
	
	@Column(name = "c_nombres")
	private String  nombres;
	
	@Column(name = "n_sexo")
	private Integer sexo;
	
	@Column(name = "n_estado")
	private Integer estado;
	
	@Column(name = "n_lista")
	private Integer lista;
	
	@Column(name = "n_activo")
    private Integer activo;

    @Column(name = "c_aud_usuario_creacion")
    private String usuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    private Date fechaCreacion;

    @Column(name = "c_aud_usuario_modificacion")
    private String usuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    private Date fechaModificacion;
	

}
