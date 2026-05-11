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
@Table(name = "mae_agrupacion_politica")
public class AgrupacionPolitica implements Serializable {

	private static final long serialVersionUID = -5343784456729697001L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_agrupacion_politica")
	@SequenceGenerator(name = "generator_agrupacion_politica", sequenceName = "seq_mae_agrupacion_politica_pk", allocationSize = 1)
    @Column(name = "n_agrupacion_politica_pk")
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
