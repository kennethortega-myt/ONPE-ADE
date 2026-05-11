package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tab_miembro_mesa_sorteado")
public class ImportMiembroMesaSorteado implements Serializable {

	private static final long serialVersionUID = 8816564833667278361L;

	@Id
	@Column(name = "n_miembro_mesa_sorteado_pk")
	private Long	id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_mesa", referencedColumnName = "n_mesa_pk", nullable = false)
	private ImportMesa mesa;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_padron_electoral", referencedColumnName = "n_padron_electoral_pk", nullable = false)
	private ImportPadronElectoral padronElectoral;
	
	@Column(name = "n_cargo")
    private Integer cargo;
    
	@Column(name = "n_bolo")
    private Integer bolo;
	
	@Column(name = "n_turno")
    private Integer turno;
    
	@Column(name = "c_direccion")
    private String direccion;
    
	@Column(name = "n_estado")
    private Integer estado;
    
	@Column(name = "n_asistencia_automatico")
    private Integer asistenciaAutomatico;
    
    @Column(name = "n_asistencia_manual")
    private Integer asistenciaManual;
    
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
