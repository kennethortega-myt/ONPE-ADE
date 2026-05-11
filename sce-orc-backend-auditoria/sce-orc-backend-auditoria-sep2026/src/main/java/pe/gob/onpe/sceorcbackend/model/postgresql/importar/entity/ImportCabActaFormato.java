package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;

import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "cab_acta_formato")
public class ImportCabActaFormato implements Serializable  {

	private static final long serialVersionUID = -3666944596425716256L;

	@Id
	@Column(name = "n_cab_acta_formato_pk")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_formato", referencedColumnName = "n_formato_pk")
    private ImportFormato formato;
	
	@Column(name = "n_correlativo")
	private Integer correlativo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo", referencedColumnName = "n_archivo_pk", nullable = true)
	private ImportArchivo archivoFormatoPdf;
	
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
