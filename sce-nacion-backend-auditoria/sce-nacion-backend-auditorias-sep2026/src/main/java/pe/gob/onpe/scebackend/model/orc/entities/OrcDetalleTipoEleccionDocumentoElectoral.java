package pe.gob.onpe.scebackend.model.orc.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pe.gob.onpe.scebackend.utils.SceConstantes;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "det_tipo_eleccion_documento_electoral")
public class OrcDetalleTipoEleccionDocumentoElectoral implements Serializable {

    private static final long serialVersionUID = 2405172041950251807L;

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
    private OrcDocumentoElectoral documentoElectoral;
    
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
