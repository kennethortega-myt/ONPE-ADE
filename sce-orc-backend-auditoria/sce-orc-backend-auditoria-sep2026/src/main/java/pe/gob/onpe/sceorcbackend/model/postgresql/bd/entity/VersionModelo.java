package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;

import jakarta.persistence.*;
import lombok.*;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "tab_version_modelo")
public class VersionModelo implements Serializable {

	private static final long serialVersionUID = -4442038762270754984L;

	@Id
	@Column(name = "n_version_modelo_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_tab_version_modelo")
	@SequenceGenerator(name = "generator_tab_version_modelo", sequenceName = "seq_tab_version_modelo_pk", allocationSize = 1)
	private Long id;

	@Column(name = "c_cadena")
	private String cadena;
	
	@Column(name = "c_version")
	private String codversion;

	@Column(name = "d_fecha_version")
	private Date fechaVersion;
	
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
