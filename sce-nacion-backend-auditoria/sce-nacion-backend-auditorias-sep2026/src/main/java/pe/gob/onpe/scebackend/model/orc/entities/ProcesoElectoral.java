package pe.gob.onpe.scebackend.model.orc.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
@Table(name = "mae_proceso_electoral")
public class ProcesoElectoral implements Serializable {

	private static final long serialVersionUID = 5313044592036862874L;

	@Id
	@Column(name = "n_proceso_electoral_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_proceso_electoral")
    @SequenceGenerator(name = "generator_proceso_electoral", sequenceName = "seq_mae_proceso_electoral_pk", allocationSize = 1)
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
