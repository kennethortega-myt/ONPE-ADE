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
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "mae_tipo_eleccion")
public class TipoEleccion implements Serializable {

	private static final long serialVersionUID = 7338115000524702519L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_tipo_eleccion")
    @SequenceGenerator(name = "generator_tipo_eleccion", sequenceName = "seq_mae_tipo_eleccion_pk", allocationSize = 1)
    @Column(name = "n_tipo_eleccion_pk")
    private Integer id;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "tipoEleccion", cascade = CascadeType.ALL)
    private Set<DetalleTipoEleccionDocumentoElectoralHistorial> detallesTipoEleccionDocumentalHistorial;

    @Column(name = "n_tipo_eleccion_padre")
    private Integer idPadre;
    
    @Column(name = "c_codigo")
    private String codigo;

    @Column(name = "c_nombre")
    private String nombre;

    @Column(name = "n_activo")
    private Integer activo;

    @Column(name = "n_orden")
    private Integer orden;

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
