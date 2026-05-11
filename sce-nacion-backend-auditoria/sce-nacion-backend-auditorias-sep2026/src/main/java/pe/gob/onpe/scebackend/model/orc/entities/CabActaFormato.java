package pe.gob.onpe.scebackend.model.orc.entities;



import lombok.*;
import pe.gob.onpe.scebackend.utils.SceConstantes;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "cab_acta_formato")
public class CabActaFormato implements Serializable  {

	private static final long serialVersionUID = -3666944596425716256L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_cab_acta_formato")
	@SequenceGenerator(name = "generator_cab_acta_formato", sequenceName = "seq_cab_acta_formato_pk", allocationSize = 1)
	@Column(name = "n_cab_acta_formato_pk")
	private Long id;
	
	@Column(name = "c_id_cab_acta_formato_cc")
	private String idCc;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cabActaFormato", cascade = CascadeType.ALL)
	private Set<DetActaFormato> detActasFormatos;

	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_formato", referencedColumnName = "n_formato_pk")
    private Formato formato;
	
	@Column(name = "n_correlativo")
	private Integer correlativo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_archivo", referencedColumnName = "n_archivo_pk", nullable = true)
	private Archivo archivoFormatoPdf;
	
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
