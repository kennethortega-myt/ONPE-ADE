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
@Table(name = "mae_local_votacion")
public class LocalVotacion implements Serializable {

	private static final long serialVersionUID = 2405172041950251807L;

	@Id
	@Column(name = "n_local_votacion_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_local_votacion")
    @SequenceGenerator(name = "generator_local_votacion", sequenceName = "seq_mae_local_votacion_pk", allocationSize = 1)
	private Long 	id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_ubigeo", referencedColumnName = "n_ubigeo_pk", nullable = false)
	private Ubigeo ubigeo;
	
	@Column(name = "c_codigo")
	private String 	codigo;
	
	@Column(name = "c_nombre")
	private String 	nombre;
	
	@Column(name = "c_direccion")
	private String 	direccion;
	
	@Column(name = "c_referencia")
	private String 	referencia;
	
	@Column(name = "c_centro_poblado")
	private String  centroPoblado;
	
	@Column(name = "n_cantidad_mesas")
	private Integer cantidadMesas;
	
	@Column(name = "n_cantidad_electores")
	private Integer cantidadElectores;
	
	@Column(name = "n_estado")
	private Integer estado;
	
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
