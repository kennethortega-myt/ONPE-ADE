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
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "tab_personero")
public class Personero implements Serializable {

	private static final long serialVersionUID = 5313044592036862874L;

	@Id
	@Column(name = "n_personero_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_personero")
	@SequenceGenerator(name = "generator_personero", sequenceName = "seq_tab_personero_pk", allocationSize = 1)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_mesa", referencedColumnName = "n_mesa_pk", nullable = false)
	private Mesa mesa;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_agrupacion_politica", referencedColumnName = "n_agrupacion_politica_pk", nullable = true)
	private AgrupacionPolitica agrupacionPolitica;

	@Column(name = "c_documento_identidad")
	private String documentoIdentidad;

	@Column(name = "c_nombres")
	private String nombres;

	@Column(name = "c_apellido_paterno")
	private String apellidoPaterno;

	@Column(name = "c_apellido_materno")
	private String apellidoMaterno;

	@Column(name = "n_activo")
	private Integer activo;

	@Column(name = "c_aud_usuario_creacion")
	private String usuarioCreacion;

	@Column(name = "d_aud_fecha_creacion")
	private Date fechaCreacion;

	@Column(name = "c_aud_usuario_modificacion")
	private String usuarioModificacion;

	@Column(name = "d_aud_fecha_modificacion")
	private Date fechaModificacion;

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
