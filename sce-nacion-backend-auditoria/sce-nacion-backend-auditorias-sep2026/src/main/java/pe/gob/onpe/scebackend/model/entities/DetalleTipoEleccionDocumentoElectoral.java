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
@Table(name = "det_tipo_eleccion_documento_electoral")
public class DetalleTipoEleccionDocumentoElectoral extends DatosAuditoria implements Serializable {

	private static final long serialVersionUID = -7569792526314179461L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_tipo_eleccion_documento_electoral")
    @SequenceGenerator(name = "generator_det_tipo_eleccion_documento_electoral", sequenceName = "seq_det_tipo_eleccion_documento_electoral_pk", allocationSize = 1)
    @Column(name = "n_det_tipo_eleccion_documento_electoral_pk")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_tipo_eleccion", referencedColumnName = "n_tipo_eleccion_pk")
    private TipoEleccion tipoEleccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_documento_electoral", referencedColumnName = "n_documento_electoral_pk")
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

    @Column(name = "n_requerido")
    private Integer requerido;

    @Column(name = "n_estado")
    private Integer estado;

    @Column(name = "n_activo")
    private Integer activo;

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
