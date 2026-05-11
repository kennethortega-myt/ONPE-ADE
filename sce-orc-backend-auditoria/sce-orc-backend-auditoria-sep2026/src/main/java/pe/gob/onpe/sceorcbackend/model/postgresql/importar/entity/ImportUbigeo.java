package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;



import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "mae_ubigeo")
public class ImportUbigeo implements Serializable {

	private static final long serialVersionUID = -2372706147223116280L;

	@Id
	@Column(name = "n_ubigeo_pk")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_centro_computo", referencedColumnName = "n_centro_computo_pk", nullable = false)
	private ImportCentroComputo centroComputo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_ambito_electoral", referencedColumnName = "n_ambito_electoral_pk", nullable = false)
	private ImportAmbitoElectoral ambitoElectoral;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_distrito_electoral", referencedColumnName = "n_distrito_electoral_pk", nullable = false)
	private ImportDistritoElectoral distritoElectoral;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_ubigeo_padre", referencedColumnName = "n_ubigeo_pk", nullable = false)
	private ImportUbigeo ubigeoPadre;
	
	@Column(name = "n_region")
	private Integer region;
	
	@Column(name = "c_ubigeo")
	private String 	codigo;
	
	@Column(name = "c_nombre")
	private String	nombre;

	@Column(name = "c_provincia")
	private String provincia;

	@Column(name = "c_departamento")
	private String departamento;
	
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
	@Setter(AccessLevel.NONE)
	private Date	fechaModificacion;
	
	@OneToMany(mappedBy="ubigeo")
    private Set<ImportLocalVotacion> localVotacions;


	@PrePersist
	public void prePersist() {
		this.fechaCreacion = new Date();
	}

	@PreUpdate
	public void preUpdate() {
		this.fechaModificacion = new Date();
	}
}
