package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.JdbcTypeCode;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.type.SqlTypes;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.transmision.json.TransmisionDto;
import pe.gob.onpe.sceorcbackend.utils.ConstantesTransmision;

@Getter
@Setter
@Entity
@Table(name = "tab_acta_Transmision")
public class ActaTransmisionNacion implements Serializable {

	private static final long serialVersionUID = 2110167724733067657L;

	@Id
	@Column(name = "n_transmision_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_acta_transmision")
	@SequenceGenerator(name = "generator_acta_transmision", sequenceName = "seq_tab_acta_transmision_pk", allocationSize = 1)
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
	
	@Column(name = "c_mensaje_error")
	private String mensajeError;
	
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name="c_peticion_acta_transmision", columnDefinition = "jsonb")
    private TransmisionDto requestActaTransmision;
	
	@Column(name = "n_activo")
	private Integer activo;
	
	@Column(name = "n_orden")
	private Integer orden;

	@Column(name = "c_aud_usuario_creacion")
	private String 	usuarioCreacion;
	
	@Column(name = "d_aud_fecha_creacion")
	private Date  	fechaCreacion;
	
	@Column(name = "c_aud_usuario_modificacion")
	private String	usuarioModificacion;
	
	@Column(name = "d_aud_fecha_modificacion")
	private Date	fechaModificacion;
	
	
	public boolean isAccionEqualNullOrEmptyOrActionActa() {
		return this.isAccionEqualNull() || this.isAccionIsEmtpy() || this.isAccionIsActionActa();
	}

	public boolean isAccionEqualNull() {
		return this.getAccion() == null;
	}

	public boolean isAccionIsEmtpy() {
		return this.getAccion() != null && this.getAccion().trim().isEmpty();
	}

	public boolean isAccionIsActionActa() {
		return this.getAccion() != null && this.getAccion().trim().equals(ConstantesTransmision.ACCION_ACTA);
	}
	
}
