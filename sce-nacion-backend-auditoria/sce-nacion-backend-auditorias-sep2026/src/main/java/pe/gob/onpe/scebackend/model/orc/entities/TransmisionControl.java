package pe.gob.onpe.scebackend.model.orc.entities;

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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity
@Table(name = "tab_acta_transmision_control")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransmisionControl implements Serializable {

	private static final long serialVersionUID = 5313044592036862874L;

	@Id
	@Column(name = "n_acta_transmision_control_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_mae_acta_transmision_control")
	@SequenceGenerator(name = "generator_mae_acta_transmision_control", sequenceName = "seq_tab_acta_transmision_control_pk", allocationSize = 1)
	private Long id;

	@Column(name = "n_acta")
	private Long idActa;

	@Column(name = "c_codigo_centro_computo")
	private String codigoCc;

	@Column(name = "n_orden")
	private Integer orden;
	
	@Column(name = "n_activo")
    private Integer activo;

    @Column(name = "c_aud_usuario_creacion")
    private String 	audUsuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    private Date audFechaCreacion;

    @Column(name = "c_aud_usuario_modificacion")
    private String	audUsuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    private Date audFechaModificacion;


}
