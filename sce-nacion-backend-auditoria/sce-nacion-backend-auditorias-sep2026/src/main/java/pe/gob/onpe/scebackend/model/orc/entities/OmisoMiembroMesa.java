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
import pe.gob.onpe.scebackend.utils.SceConstantes;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tab_omiso_miembro_mesa")
public class OmisoMiembroMesa implements Serializable {

	private static final long serialVersionUID = -4594831534566322759L;

	@Id
	@Column(name = "n_omiso_miembro_mesa_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_omiso_miembro_mesa")
    @SequenceGenerator(name = "generator_omiso_miembro_mesa", sequenceName = "seq_tab_omiso_miembro_mesa_pk", allocationSize = 1)
	private Long	id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_mesa", referencedColumnName = "n_mesa_pk", nullable = false)
	private Mesa mesa;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_miembro_mesa_sorteado", referencedColumnName = "n_miembro_mesa_sorteado_pk", nullable = false)
	private MiembroMesaSorteado miembroMesaSorteado;
	
	@Column(name = "c_id_tab_omiso_miembro_mesa_cc")
	private String idCc;
	
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
