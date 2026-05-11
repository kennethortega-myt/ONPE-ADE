package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "cab_importador_progreso")
public class ImportImportadorProgreso implements Serializable {

	private static final long serialVersionUID = 6837435469883725316L;

	@Id
	@Column(name = "n_importador_progreso_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_cab_importador_progreso")
	@SequenceGenerator(name = "generator_cab_importador_progreso", sequenceName = "seq_cab_importador_progreso_pk", allocationSize = 1)
	private Long id;
	
	@Column(name = "n_estado")
	private Integer estado;
	
	@Column(name = "n_porcentaje")
	private Double porcentaje;
	
	@Column(name = "c_aud_usuario_creacion")
    private String 	usuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    private Date fechaCreacion;

    @Column(name = "c_aud_usuario_modificacion")
    private String	usuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    private Date	fechaModificacion;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "importador", cascade = CascadeType.ALL)
	private List<ImportDetImportadorProgreso> detalle;
    
    @PrePersist
	public void prePersist() {
		this.fechaCreacion = new Date();
	}

	@PreUpdate
	public void preUpdate() {
		this.fechaModificacion = new Date();
	}
	
}
