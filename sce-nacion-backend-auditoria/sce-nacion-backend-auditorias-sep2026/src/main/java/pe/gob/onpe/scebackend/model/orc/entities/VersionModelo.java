package pe.gob.onpe.scebackend.model.orc.entities;

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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "tab_version_modelo")
public class VersionModelo {

	@Id
    @Column(name = "n_version_modelo_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_tab_version_modelo")
    @SequenceGenerator(name = "generator_tab_version_modelo", sequenceName = "seq_tab_version_modelo_pk", allocationSize = 1)
    private Integer id;

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
            activo = 1;
        }

        if (Objects.isNull(fechaCreacion)) {
            fechaCreacion = new Date();
        }
    }
	
}
