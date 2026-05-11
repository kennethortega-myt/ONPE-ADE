package pe.gob.onpe.scebackend.model.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pe.gob.onpe.scebackend.utils.SceConstantes;

import jakarta.persistence.*;

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
@Table(name = "tab_seccion")
public class Seccion implements Serializable {

	private static final long serialVersionUID = -3922242146922047501L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_seccion")
    @SequenceGenerator(name = "generator_seccion", sequenceName = "seq_tab_seccion_pk", allocationSize = 1)
    @Column(name = "n_seccion_pk")
    private Integer id;

    @Column(name = "c_nombre")
    private String nombre;

    @Column(name = "n_activo")
    private Integer activo;
    
    @Column(name = "n_orientacion")
    private Integer orientacion;

    @Column(name = "c_aud_usuario_creacion")
    private String usuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    private Date fechaCreacion;

    @Column(name = "c_aud_usuario_modificacion")
    private String usuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    private Date fechaModificacion;

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
