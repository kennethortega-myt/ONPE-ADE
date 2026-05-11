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
@Table(name = "mae_centro_computo")
public class CentroComputo implements Serializable {

	private static final long serialVersionUID = 2405172041950251807L;

	@Id
	@Column(name = "n_centro_computo_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_centro_computo")
    @SequenceGenerator(name = "generator_centro_computo", sequenceName = "seq_mae_centro_computo_pk", allocationSize = 1)
	private Long	id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_centro_computo_padre", referencedColumnName = "n_centro_computo_pk", nullable = false)
	private CentroComputo centroComputoPadre;
	
	@Column(name = "c_codigo")
	private String  codigo;
	
	@Column(name = "c_nombre")
	private String	nombre;
	
	@Column(name = "c_protocol_app_backend_cc")
	private String  protocolBackendCc;
	
	@Column(name = "c_ip_app_backend_cc")
	private String	ipBackendCc;
	
	@Column(name = "n_puerto_app_backend_cc")
	private Integer	puertoBackedCc;
	
	@Column(name = "c_apitoken_app_backend_cc")
	private String	apiTokenBackedCc;
	
	@Column(name = "n_estado_configuracion")
	private Integer	estadoConfiguracionCc;
	
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
