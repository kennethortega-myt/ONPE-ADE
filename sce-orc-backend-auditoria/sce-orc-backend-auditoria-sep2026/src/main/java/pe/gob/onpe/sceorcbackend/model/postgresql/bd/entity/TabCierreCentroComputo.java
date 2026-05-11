package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "tab_cierre_centro_computo")
public class TabCierreCentroComputo implements Serializable {

    private static final long serialVersionUID = 3739084144320042064L;

    @Id
    @Column(name = "n_cierre_centro_computo_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_tab_cierre_centro_computo")
    @SequenceGenerator(name = "generator_tab_cierre_centro_computo",
            sequenceName = "seq_tab_cierre_centro_computo_pk",
            allocationSize = 1)
    private Long id;

    @Column(name = "n_centro_computo", nullable = false)
    private Integer centroComputo;

    @Column(name = "n_correlativo", nullable = false)
    private Integer correlativo;

    @Column(name = "d_fecha_cierre")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCierre;

    @Column(name = "c_usuario_cierre", nullable = false, length = 50)
    private String usuarioCierre;

    @Column(name = "c_motivo_cierre", length = 350)
    private String motivoCierre;

    @Column(name = "n_reapertura", columnDefinition = "smallint default 0")
    private Integer reapertura = 0;

    @Column(name = "d_fecha_reapertura")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaReapertura;

    @Column(name = "c_usuario_reapertura", nullable = false, length = 50)
    private String usuarioReapertura;

    @Column(name = "n_activo", columnDefinition = "smallint default 1")
    private Integer activo = 1;

    @Column(name = "c_aud_usuario_creacion", nullable = false, length = 50)
    private String usuarioCreacion;

    @Column(name = "d_aud_fecha_creacion", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaCreacion;

    @Column(name = "c_aud_usuario_modificacion", length = 50)
    private String usuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaModificacion;

    @PrePersist
    protected void onCreate() {
        Date now = new Date();
        if (fechaCreacion == null) {
            fechaCreacion = now;
        }
        if (activo == null) activo = 1;
        if (reapertura == null) reapertura = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        fechaModificacion = new Date();
    }
}
