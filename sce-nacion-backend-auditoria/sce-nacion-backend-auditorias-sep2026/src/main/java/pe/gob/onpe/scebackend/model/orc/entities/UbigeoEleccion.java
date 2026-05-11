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
import lombok.EqualsAndHashCode;
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
@Table(name = "det_ubigeo_eleccion")
public class UbigeoEleccion implements Serializable {

	private static final long serialVersionUID = 2405172041950251807L;

	@Id
	@Column(name = "n_det_ubigeo_eleccion_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_ubigeo_eleccion")
    @SequenceGenerator(name = "generator_ubigeo_eleccion", sequenceName = "seq_det_ubigeo_eleccion_pk", allocationSize = 1)
	private Long	id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_ubigeo", referencedColumnName = "n_ubigeo_pk", nullable = false)
	@EqualsAndHashCode.Exclude private Ubigeo ubigeo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_eleccion", referencedColumnName = "n_eleccion_pk", nullable = false)
	@EqualsAndHashCode.Exclude private Eleccion eleccion;
	
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
