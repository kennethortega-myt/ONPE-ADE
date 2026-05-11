package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;

import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "det_ubigeo_eleccion")
public class UbigeoEleccion {

	@Id
	@Column(name = "n_det_ubigeo_eleccion_pk")
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


	@Override
	public String toString() {
		return "UbigeoEleccion{" +
				"id=" + id +
				'}';
	}

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
