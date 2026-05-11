package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;

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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.utils.DateUtil;

@Getter
@Setter
@Entity
@Table(name = "det_importador_progreso")
public class ImportDetImportadorProgreso implements Serializable {

	private static final long serialVersionUID = -2951503446925903564L;
	
	@Id
	@Column(name = "n_det_importador_progreso_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_importador_progreso")
	@SequenceGenerator(name = "generator_det_importador_progreso", sequenceName = "seq_det_importador_progreso_pk", allocationSize = 1)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_importador_progreso", referencedColumnName = "n_importador_progreso_pk", nullable = false)
	private ImportImportadorProgreso importador;
	
	@Column(name = "n_porcentaje")
	private Double porcentaje;
	
	@Column(name = "c_mensaje")
	private String mensaje;
	
	@Column(name = "c_aud_usuario_creacion")
    private String 	usuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    private Date fechaCreacion;

    @Column(name = "c_aud_usuario_modificacion")
    private String	usuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    private Date	fechaModificacion;
    
    @PrePersist
	public void prePersist() {
		this.fechaCreacion = DateUtil.getFechaActualPeruana();
	}

	@PreUpdate
	public void preUpdate() {
		this.fechaModificacion = DateUtil.getFechaActualPeruana();
	}

}
