package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.*;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tab_archivo")
public class ImportArchivo implements Serializable {

	private static final long serialVersionUID = -3125270789563926230L;

	@Id
	@Column(name = "n_archivo_pk")
	private Long   	id;
	
	@Column(name = "c_guid")
	private String 	guid;
	
	@Column(name = "c_nombre")
	private String 	nombre;
	
	@Column(name = "c_nombre_original")
	private String 	nombreOriginal;
	
	@Column(name = "c_formato")
	private String 	formato;
	
	@Column(name = "c_peso")
	private String 	peso;
	
	@Column(name = "c_ruta")
	private String 	ruta;
	
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
