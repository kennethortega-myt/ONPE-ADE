package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "mae_ambito_electoral")
public class ImportAmbitoElectoral implements Serializable {

	private static final long serialVersionUID = 2388545460286237285L;

	@Id
	@Column(name = "n_ambito_electoral_pk")
	private Long id;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ambitoElectoral", cascade = CascadeType.ALL)
	private List<ImportUbigeo> ubigeos;
	
	@Column(name = "c_nombre")
	private String	nombre;
	
	@Column(name = "c_codigo")
	private String  codigo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_ambito_electoral_padre", referencedColumnName = "n_ambito_electoral_pk", nullable = false)
	private ImportAmbitoElectoral ambitoElectoralPadre;
	
	@Column(name = "n_tipo_ambito_electoral")
	private Integer tipoAmbitoElectoral;
	
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
	
	@PrePersist
    public void prePersist() {
    	this.fechaCreacion = new Date();
    }
    
    
    @PreUpdate
    public void preUpdate() {
    	this.fechaModificacion = new Date();
    }
}
