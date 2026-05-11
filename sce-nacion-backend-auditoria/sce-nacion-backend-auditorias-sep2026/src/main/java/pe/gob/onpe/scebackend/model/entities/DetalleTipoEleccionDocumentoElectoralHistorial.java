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
@Table(name = "det_tipo_eleccion_documento_electoral_historial")
public class DetalleTipoEleccionDocumentoElectoralHistorial implements Serializable {

	private static final long serialVersionUID = 9164676064888761926L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_tipo_eleccion_documento_electoral_historial")
    @SequenceGenerator(name = "generator_det_tipo_eleccion_documento_electoral_historial", sequenceName = "seq_det_tipo_eleccion_documento_electoral_historial_pk", allocationSize = 1)
    @Column(name = "n_det_tipo_eleccion_documento_electoral_historial_pk")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_configuracion_proceso_electoral", referencedColumnName="n_configuracion_proceso_electoral_pk")
    private ConfiguracionProcesoElectoral configuracionProcesoElectoral;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_tipo_eleccion", referencedColumnName="n_tipo_eleccion_pk")
    private TipoEleccion tipoEleccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_documento_electoral", referencedColumnName="n_documento_electoral_pk")
    private DocumentoElectoral documentoElectoral;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_archivo", referencedColumnName = "n_archivo_pk")
    Archivo archivo;
    
    @Column(name = "c_rango_inicial")
    private String rangoInicial;
    
    @Column(name = "c_rango_final")
    private String rangoFinal;
    
    @Column(name = "c_digito_chequeo")
    private String digitoChequeo;
    
    @Column(name = "c_digito_error")
    private String digitoError;
    
    @Column(name = "n_correlativo")
    private Integer correlativo;

    @Column(name = "n_requerido")
    private Integer requerido;

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
