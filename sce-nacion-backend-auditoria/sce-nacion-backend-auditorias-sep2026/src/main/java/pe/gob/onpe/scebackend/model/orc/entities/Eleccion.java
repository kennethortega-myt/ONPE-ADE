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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "mae_eleccion")
public class Eleccion implements Serializable {

	private static final long serialVersionUID = 2405172041950251807L;

	@Id
	@Column(name = "n_eleccion_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_eleccion")
    @SequenceGenerator(name = "generator_eleccion", sequenceName = "seq_mae_eleccion_pk", allocationSize = 1)
	private Long	id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_proceso_electoral", referencedColumnName = "n_proceso_electoral_pk", nullable = false)
	private ProcesoElectoral procesoElectoral;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "eleccion", cascade = CascadeType.ALL)
	private Set<UbigeoEleccion> ubigeosElecciones;
	
	@Column(name = "c_codigo")
	private String  codigo;
	
	@Column(name = "c_nombre")
	private String	nombre;
	
	@Column(name = "c_nombre_vista")
	private String	nombreVista;
	
	@Column(name = "n_activo")
	private Integer activo;
	
	@Column(name = "n_principal")
	private Integer principal;
	
	@Column(name = "n_preferencial")
	private Integer preferencial;
	
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
