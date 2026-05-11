package pe.gob.onpe.scebackend.model.orc.entities;

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
@Table(name = "cab_catalogo")
public class OrcCatalogo implements Serializable {

    private static final long serialVersionUID = 2405172041950251807L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_cab_catalogo")
    @SequenceGenerator(name = "generator_cab_catalogo", sequenceName = "seq_cab_catalogo_pk", allocationSize = 1)
    @Column(name = "n_catalogo_pk")
    private Integer id;
    
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_catalogo_padre", referencedColumnName = "n_catalogo_pk", nullable = false)
    private OrcCatalogo catalogoPadre;
    
    @Column(name = "c_maestro")
    private String maestro;

    @Column(name = "n_activo")
    private Integer activo;

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
