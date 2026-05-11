package pe.gob.onpe.scebackend.model.orc.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "tab_version")
public class Version implements Serializable {

	private static final long serialVersionUID = -4442038762270754984L;

	@Id
	@Column(name = "n_version_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_version")
    @SequenceGenerator(name = "generator_version", sequenceName = "seq_tab_version_pk", allocationSize = 1)
	private Long    id;
	
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
