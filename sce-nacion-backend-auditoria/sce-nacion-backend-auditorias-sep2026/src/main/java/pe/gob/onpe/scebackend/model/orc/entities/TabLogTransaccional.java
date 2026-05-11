package pe.gob.onpe.scebackend.model.orc.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "tab_log_transaccion")
public class TabLogTransaccional implements Serializable {

	private static final long serialVersionUID = 85305835801604354L;

	@Id
	@Column(name = "n_log_transaccion_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_log_transaccion")
	@SequenceGenerator(name = "generator_log_transaccion", sequenceName = "seq_tab_log_transaccion_pk", allocationSize = 1)
	private Long id;

	@Column(name = "c_usuario")
	private String usuario;

	@Column(name = "c_centro_computo")
	private String centroComputo;

	@Column(name = "c_ambito_electoral")
	private String ambitoElectoral;

	@Column(name = "n_accion")
	private Integer accion;
	
	@Column(name = "c_observacion")
	private String observacion;

	@JdbcTypeCode(SqlTypes.JSON)
	@Column(name = "c_data", columnDefinition = "json")
	private String data;

	@Column(name = "n_autorizacion")
	private Integer autorizacion;

	@Column(name = "d_fecha_registro")
	private Date fechaRegistro;

	@PrePersist
	public void setDefaultValues() {
		if (Objects.isNull(fechaRegistro)) {
			fechaRegistro = new Date();
		}
	}

}
