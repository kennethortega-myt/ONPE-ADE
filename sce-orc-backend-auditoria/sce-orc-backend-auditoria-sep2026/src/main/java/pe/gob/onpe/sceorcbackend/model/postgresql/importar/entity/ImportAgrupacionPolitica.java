package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;



import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "mae_agrupacion_politica")
public class ImportAgrupacionPolitica implements Serializable {

	private static final long serialVersionUID = -7795049763220045310L;

	@Id
    @Column(name = "n_agrupacion_politica_pk")
	private Long id;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "agrupacionPolitica", cascade = CascadeType.ALL)
	private List<ImportDetUbigeoEleccionAgrupacionPolitica> detalle;
	
	@Column(name = "c_codigo")
	private String  codigo;
	
	@Column(name = "c_descripcion")
	private String  descripcion;
	
	@Column(name = "n_tipo_agrupacion_politica")
	private Long	tipoAgrupacionPolitica;
	
	@Column(name = "n_estado")
	private Integer estado;
	
	@Column(name = "c_ubigeo_maximo")
	private String  ubigeoMaximo;
	
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
