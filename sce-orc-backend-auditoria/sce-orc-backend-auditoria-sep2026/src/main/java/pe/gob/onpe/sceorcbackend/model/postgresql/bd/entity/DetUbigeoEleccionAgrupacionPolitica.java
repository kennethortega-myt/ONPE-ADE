package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;

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
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;


@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "det_ubigeo_eleccion_agrupacion_politica")
public class DetUbigeoEleccionAgrupacionPolitica {

	@Id
	@Column(name = "n_det_ubigeo_eleccion_agrupacion_politica_pk")
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long	id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_agrupacion_politica", referencedColumnName = "n_agrupacion_politica_pk", nullable = false)
	private AgrupacionPolitica agrupacionPolitica;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_det_ubigeo_eleccion", referencedColumnName = "n_det_ubigeo_eleccion_pk", nullable = false)
	private UbigeoEleccion ubigeoEleccion;
	
	@Column(name = "n_posicion")
	private Integer posicion;
	
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


	@Override
	public String toString() {
		return "DetUbigeoEleccionAgrupacionPolitica{" +
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
