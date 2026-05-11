package pe.gob.onpe.scebackend.model.orc.entities;

import java.io.Serializable;
import java.util.Date;
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
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "mae_distrito_electoral")
public class DistritoElectoral implements Serializable {
	
	private static final long serialVersionUID = 7649212470645125089L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_distrito_electoral")
	@SequenceGenerator(name = "generator_distrito_electoral", sequenceName = "seq_tab_distrito_electoral_pk", allocationSize = 1)
	@Column(name = "n_distrito_electoral_pk")
	private Integer id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_distrito_electoral_padre", referencedColumnName = "n_distrito_electoral_pk", nullable = false)
	private DistritoElectoral distritoElectoralPadre;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "distritoElectoral", cascade = CascadeType.ALL)
	private Set<Ubigeo> ubigeos;
	
	@Column(name = "c_codigo")
	private String codigo;
	
	@Column(name = "c_nombre")
	private String nombre;
	
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
