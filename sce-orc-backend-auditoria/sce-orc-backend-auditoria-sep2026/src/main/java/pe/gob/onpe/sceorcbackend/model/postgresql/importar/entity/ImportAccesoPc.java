package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import lombok.AccessLevel;
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
@Table(name = "tab_acceso_pc")
public class ImportAccesoPc implements Serializable {
    @Id
    @Column(name = "n_acceso_pc_pk")
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
    @Setter(AccessLevel.NONE)
    private Date fechaModificacion;

    @PrePersist
    public void prePersist() {
        this.fechaCreacion = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        this.fechaModificacion = new Date();
    }
}

