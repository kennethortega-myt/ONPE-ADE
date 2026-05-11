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
@Table(name = "tab_mesa")
public class Mesa implements Serializable {

	private static final long serialVersionUID = 2405172041950251807L;

	@Id
	@Column(name = "n_mesa_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_mesa")
    @SequenceGenerator(name = "generator_mesa", sequenceName = "seq_tab_mesa_pk", allocationSize = 1)
	private Long	id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_local_votacion", referencedColumnName = "n_local_votacion_pk", nullable = false)
	private LocalVotacion localVotacion;
	
	@Column(name = "c_mesa")
	private String  codigo; // mesa
	
	@Column(name = "n_cantidad_electores_habiles")
	private Integer cantidadElectoresHabiles;
	
	@Column(name = "n_cantidad_electores_habiles_extranjeros")
	private Integer cantidadElectoresHabilesExtranjeros;
	
	@Column(name = "n_discapacidad")
	private Integer discapacidad;
	
	@Column(name = "n_solucion_tecnologica")
	private Long	solucionTecnologica;
	
	@Column(name = "c_estado_mesa")
	private String 	estadoMesa;
	
	@Column(name = "c_estado_digitalizacion_le")
	private String 	estadoDigitalizacionLe;
	
	@Column(name = "c_estado_digitalizacion_mm")
	private String 	estadoDigitalizacionMm;
	
	@Column(name = "c_estado_digitalizacion_pr")
	private String 	estadoDigitalizacionPr;
	
	@Column(name = "c_estado_digitalizacion_me")
	private String 	estadoDigitalizacionMe;
	
	@Column(name = "n_activo")
	private Integer activo;
	
	@Column(name = "c_usuario_asignado_le")
	private String  usuarioAsignadoLe;

	@Column(name = "d_aud_fecha_usuario_asignado_le")
	private Date	fechaAsignadoLe;

	@Column(name = "c_usuario_asignado_mm")
	private String  usuarioAsignadoMm;

	@Column(name = "d_aud_fecha_usuario_asignado_mm")
	private Date	fechaAsignadoMm;
	
	@Column(name = "c_usuario_asignado_pr")
	private String  usuarioAsignadoPr;

	@Column(name = "d_aud_fecha_usuario_asignado_pr")
	private Date	fechaAsignadoPr;

	@Column(name = "c_usuario_asignado_me")
	private String  usuarioAsignadoMe;

	@Column(name = "d_aud_fecha_usuario_asignado_me")
	private Date	fechaAsignadoMe;
	
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
