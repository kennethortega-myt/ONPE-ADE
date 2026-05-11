package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
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
@Table(name = "mae_proceso_electoral")
public class ProcesoElectoral {

	@Id
	@Column(name = "n_proceso_electoral_pk")
	private Long 	id;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "procesoElectoral", cascade = CascadeType.ALL)
	private Set<Eleccion> elecciones;
	
	@Column(name = "c_nombre")
	private String 	nombre;
	
	@Column(name = "c_acronimo")
	private String 	acronimo;
	
	@Column(name = "d_fecha_convocatoria")
	private Date  fechaConvocatoria;
	
	@Column(name = "n_tipo_ambito_electoral")
	private Long 	tipoAmbitoElectoral;
	
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
		return "ProcesoElectoral{" +
				"id=" + id +
				", nombre='" + nombre + '\'' +
				", acronimo='" + acronimo + '\'' +
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
