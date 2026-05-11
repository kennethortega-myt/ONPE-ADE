package pe.gob.onpe.scebackend.model.orc.entities;


import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;

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
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "mae_ambito_electoral")
public class AmbitoElectoral implements Serializable {

	private static final long serialVersionUID = 2405172041950251807L;

	@Id
	@Column(name = "n_ambito_electoral_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_ambito_electoral")
    @SequenceGenerator(name = "generator_ambito_electoral", sequenceName = "seq_mae_ambito_electoral_pk", allocationSize = 1)
	private Long   	id;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "ambitoElectoral", cascade = CascadeType.ALL)
	private List<Ubigeo> ubigeos;
	
	@Column(name = "c_nombre")
	private String	nombre;
	
	@Column(name = "c_codigo")
	private String  codigo;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_ambito_electoral_padre", referencedColumnName = "n_ambito_electoral_pk", nullable = false)
	private AmbitoElectoral ambitoElectoralPadre;
	
	@Column(name = "n_tipo_ambito_electoral")
	private Integer tipoAmbitoElectoral;
	
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
