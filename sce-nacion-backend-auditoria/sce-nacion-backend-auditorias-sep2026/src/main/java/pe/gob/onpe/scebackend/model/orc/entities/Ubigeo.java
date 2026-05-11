package pe.gob.onpe.scebackend.model.orc.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

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
@Table(name = "mae_ubigeo")
public class Ubigeo implements Serializable {

	private static final long serialVersionUID = 2405172041950251807L;

	@Id
	@Column(name = "n_ubigeo_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_ubigeo")
    @SequenceGenerator(name = "generator_ubigeo", sequenceName = "seq_mae_ubigeo_pk", allocationSize = 1)
	private Long 	id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_centro_computo", referencedColumnName = "n_centro_computo_pk", nullable = false)
	private CentroComputo centroComputo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_ambito_electoral", referencedColumnName = "n_ambito_electoral_pk", nullable = false)
	private AmbitoElectoral ambitoElectoral;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_distrito_electoral", referencedColumnName = "n_distrito_electoral_pk", nullable = false)
	private DistritoElectoral distritoElectoral;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_ubigeo_padre", referencedColumnName = "n_ubigeo_pk", nullable = false)
	private Ubigeo ubigeoPadre;
	
	@Column(name = "n_region")
	private Integer region;
	
	@Column(name = "c_ubigeo")
	private String 	codigo;
	
	@Column(name = "c_nombre")
	private String	nombre;

	@Column(name = "c_provincia")
	private String	provincia;

	@Column(name = "c_departamento")
	private String	departamento;
	
	@Column(name = "n_tipo_ambito_geografico")
	private Integer	tipoAmbitoGeografico;
	
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
	
	@OneToMany(mappedBy="ubigeo")
    private Set<LocalVotacion> localVotacions;

	
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
