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
@Table(name = "det_catalogo_estructura")
public class OrcDetalleCatalogoEstructura implements Serializable {

    private static final long serialVersionUID = 2405172041950251807L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_catalogo_estructura")
    @SequenceGenerator(name = "generator_det_catalogo_estructura", sequenceName = "seq_det_catalogo_estructura_pk", allocationSize = 1)
    @Column(name = "n_det_catalogo_estructura_pk")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "n_catalogo", referencedColumnName = "n_catalogo_pk")
    private OrcCatalogo catalogo;

    @Column(name = "c_columna")
    private String columna;

    @Column(name = "c_nombre")
    private String nombre;

    @Column(name = "n_codigo")
    private Integer codigoI;

    @Column(name = "c_codigo")
    private String codigoS;

    @Column(name = "n_orden")
    private Integer orden;

    @Column(name = "c_tipo")
    private String tipo;

    @Column(name = "c_informacion_adicional")
    private String informacionAdicional;

    @Column(name = "n_obligatorio")
    private Integer obligatorio;

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
