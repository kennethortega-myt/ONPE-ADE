package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "mae_agrupacion_politica")
public class AgrupacionPolitica {

	@Id
    @Column(name = "n_agrupacion_politica_pk")
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long   	id;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "agrupacionPolitica", cascade = CascadeType.ALL)
	private List<DetUbigeoEleccionAgrupacionPolitica> detalle;
	
	@Column(name = "c_codigo")
	private String  codigo;
	
	@Column(name = "c_descripcion")
	private String  descripcion;
	
	@Column(name = "n_tipo_agrupacion_politica")
	private Long	tipoAgrupacionPolitica;
	
	@Column(name = "n_estado")
	private Integer estado;
	
	@Column(name = "c_ubigeo_maximo")
	private String  ubigeoMaximo;
	
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


	public AgrupacionPolitica(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "AgrupacionPolitica{" +
				"id=" + id +
				", codigo='" + codigo + '\'' +
				", descripcion='" + descripcion + '\'' +
				", estado=" + estado +
				", activo=" + activo +
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
