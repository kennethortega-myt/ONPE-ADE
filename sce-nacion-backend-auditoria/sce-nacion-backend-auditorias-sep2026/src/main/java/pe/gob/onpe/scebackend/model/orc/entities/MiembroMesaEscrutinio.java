package pe.gob.onpe.scebackend.model.orc.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "tab_miembro_mesa_escrutinio")
public class MiembroMesaEscrutinio implements Serializable {
	 
	private static final long serialVersionUID = 1429795327813767652L;
	
	@Id
	@Column(name = "n_miembro_mesa_escrutinio_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_tab_miembro_mesa_escrutinio_pk")
    @SequenceGenerator(name = "generator_tab_miembro_mesa_escrutinio_pk", sequenceName = "seq_tab_miembro_mesa_escrutinio_pk", allocationSize = 1)
	private Long	id;
	
	@Column(name = "c_id_tab_miembro_mesa_escrutinio_cc")
	private String idCc;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_mesa", referencedColumnName = "n_mesa_pk", nullable = false)
	private Mesa mesa;
    
	@Column(name = "c_documento_identidad_presidente")
    private String documentoIdentidadPresidente;
	
	@Column(name = "c_documento_identidad_secretario")
    private String documentoIdentidadSecretario;
	
	@Column(name = "c_documento_identidad_tercer_miembro")
    private String documentoIdentidadTercerMiembro;
    
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

	public MiembroMesaEscrutinio(Long id) {
		this.id = id;
	}

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