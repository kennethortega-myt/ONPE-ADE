package pe.gob.onpe.scebackend.model.orc.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "tab_acceso_pc")
public class AccesoPc {
    @Id
    @Column(name = "n_acceso_pc_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_acceso_pc")
    @SequenceGenerator(name = "generator_acceso_pc", sequenceName = "seq_tab_acceso_pc_pk", allocationSize = 1)
    private Long id;

    @Column(name = "c_ip_acceso_pc", length = 50)
    private String ipAccesoPc;

    @Column(name = "c_usuario_acceso_pc", length = 50)
    private String usuarioAccesoPc;

    @Column(name = "d_fecha_acceso_pc")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAccesoPc;

    @Column(name = "n_activo")
    private Integer activo;

    @Column(name = "c_aud_usuario_creacion", length = 50)
    private String usuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "c_aud_usuario_modificacion", length = 50)
    private String usuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;

    @PrePersist
    public void prePersist() {
        if (fechaAccesoPc == null) {
            fechaAccesoPc = new Date();
        }
        if (fechaCreacion == null) {
            fechaCreacion = new Date();
        }
        if (activo == null) {
            activo = 1;
        }
    }
}
