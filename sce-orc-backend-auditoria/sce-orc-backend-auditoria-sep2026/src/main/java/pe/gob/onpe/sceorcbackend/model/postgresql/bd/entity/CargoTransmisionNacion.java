package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;


import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmisioncargo.json.TransmisionCargoDto;


@Getter
@Setter
@Entity
@Table(name = "tab_cargo_transmision")
public class CargoTransmisionNacion implements Serializable {

	private static final long serialVersionUID = 2535102507832682269L;

	@Id
	@Column(name = "n_cargo_transmision_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_cargo_transmision")
	@SequenceGenerator(name = "generator_cargo_transmision", sequenceName = "seq_tab_cargo_transmision_pk", allocationSize = 1)
	private Long id;
	
	@Column(name = "n_acta")
	private Long idActa;

	@Column(name = "n_estado_transmitido_nacion")
	private Integer estadoTransmitidoNacion;
	
	@Column(name = "c_tipo_transmision")
	private String tipoTransmision;

	@Column(name = "n_transmite")
	private Integer transmite;

	@Column(name = "d_fecha_transmision")
	private Date fechaTransmision;
	
	@Column(name = "c_accion")
	private String accion;

	@Column(name = "c_usuario_transmision")
	private String usuarioTransmision;
	
	@Column(name = "n_intento")
	private Integer intento;
	
	@Column(name = "n_activo")
    private Integer activo;
	
	@Column(name = "d_aud_fecha_creacion")
	private Date fechaRegistro;

	@Column(name = "c_aud_usuario_creacion")
	private String usuarioRegistro;
	
	@Column(name = "c_aud_usuario_modificacion")
    private String usuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    private Date fechaModificacion;
	
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name="c_peticion_cargo_transmision", columnDefinition = "jsonb")
    private TransmisionCargoDto requestCargoTransmision;
	
}
