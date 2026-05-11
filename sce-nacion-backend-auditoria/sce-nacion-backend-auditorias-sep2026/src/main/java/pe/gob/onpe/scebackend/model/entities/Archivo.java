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
@Table(name = "tab_archivo")
public class Archivo implements Serializable {

	private static final long serialVersionUID = 4824741850841914014L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_tab_archivo")
    @SequenceGenerator(name = "generator_tab_archivo", sequenceName = "seq_tab_archivo_pk", allocationSize = 1)
    @Column(name = "n_archivo_pk")
    private Long id;
    
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "archivo", cascade = CascadeType.ALL)
    private Set<DetalleTipoEleccionDocumentoElectoralHistorial> detallesTipoEleccionDocumentalHistorial;
    
    @Column(name = "c_guid")
    private String guid;

    @Column(name = "c_nombre")
    private String nombre;

    @Column(name = "c_nombre_original")
    private String nombreOriginal;

    @Column(name = "c_formato")
    private String formato;
    
    @Column(name = "c_peso")
    private String peso;

    @Column(name = "c_ruta")
    private String ruta;

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
