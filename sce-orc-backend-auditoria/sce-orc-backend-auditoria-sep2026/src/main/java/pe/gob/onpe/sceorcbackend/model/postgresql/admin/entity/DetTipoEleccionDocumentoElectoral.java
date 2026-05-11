package pe.gob.onpe.sceorcbackend.model.postgresql.admin.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.Eleccion;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.ProcesoElectoral;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.PrePersist;
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
public class DetTipoEleccionDocumentoElectoral {

    @Id
    @Column(name = "n_det_tipo_eleccion_documento_electoral_pk")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_proceso_electoral", referencedColumnName="n_proceso_electoral_pk")
    private ProcesoElectoral procesoElectoral;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_eleccion", referencedColumnName="n_eleccion_pk")
    private Eleccion eleccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_documento_electoral", referencedColumnName="n_documento_electoral_pk")
    private DocumentoElectoral documentoElectoral;
    
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
