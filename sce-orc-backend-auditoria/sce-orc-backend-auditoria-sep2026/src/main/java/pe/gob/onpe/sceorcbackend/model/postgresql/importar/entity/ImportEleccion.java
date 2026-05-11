package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;


import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import jakarta.persistence.CascadeType;
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
@Table(name = "mae_eleccion")
public class ImportEleccion implements Serializable {


	private static final long serialVersionUID = -494597813180929788L;

	@Id
	@Column(name = "n_eleccion_pk")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_proceso_electoral", referencedColumnName = "n_proceso_electoral_pk", nullable = false)
	private ImportProcesoElectoral procesoElectoral;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "eleccion", cascade = CascadeType.ALL)
	private Set<ImportUbigeoEleccion> ubigeosElecciones;
	
	@Column(name = "c_codigo")
	private String  codigo;
	
	@Column(name = "c_nombre")
	private String	nombre;
	
	@Column(name = "c_nombre_vista")
	private String	nombreVista;
	
	@Column(name = "n_activo")
	private Integer activo;
	
	@Column(name = "n_principal")
	private Integer principal;
	
	@Column(name = "n_preferencial")
	private Integer preferencial;
	
	@Column(name = "c_aud_usuario_creacion")
	private String 	usuarioCreacion;
	
	@Column(name = "d_aud_fecha_creacion")
	private Date  	fechaCreacion;
	
	@Column(name = "c_aud_usuario_modificacion")
	private String	usuarioModificacion;
	
	@Column(name = "d_aud_fecha_modificacion")
	@Setter(AccessLevel.NONE)
	private Date	fechaModificacion;


	@PrePersist
	public void prePersist() {
		this.fechaCreacion = new Date();
	}

	@PreUpdate
	public void preUpdate() {
		this.fechaModificacion = new Date();
	}
}
