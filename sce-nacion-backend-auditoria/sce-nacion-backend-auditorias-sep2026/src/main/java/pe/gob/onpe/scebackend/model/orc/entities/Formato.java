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
import pe.gob.onpe.scebackend.utils.SceConstantes;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tab_formato")
public class Formato implements Serializable  {

	private static final long serialVersionUID = 2497390660988610155L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_formato")
    @SequenceGenerator(name = "generator_formato", sequenceName = "seq_tab_formato_pk", allocationSize = 1)
    @Column(name = "n_formato_pk")
    private Long id;
	
	@Column(name = "c_id_tab_formato_cc")
	private String idCc;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_archivo_formato", referencedColumnName = "n_archivo_pk")
	private Archivo archivo;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "formato", cascade = CascadeType.ALL)
	private Set<CabActaFormato> actasFormatos;
	
	@Column(name = "n_correlativo")
	private Integer correlativo;
	
	@Column(name = "n_tipo_formato")
	private Integer tipoFormato;
	
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

	@Override
	public String toString() {
		return "Formato{" +
				"id=" + id +
				", correlativo=" + correlativo +
				", tipoFormato=" + tipoFormato +
				", activo=" + activo +
				", usuarioCreacion='" + usuarioCreacion + '\'' +
				", fechaCreacion=" + fechaCreacion +
				", usuarioModificacion='" + usuarioModificacion + '\'' +
				", fechaModificacion=" + fechaModificacion +
				'}';
	}
}
