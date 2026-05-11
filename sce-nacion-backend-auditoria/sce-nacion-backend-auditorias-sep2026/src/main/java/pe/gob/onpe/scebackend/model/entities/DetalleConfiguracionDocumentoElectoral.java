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
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "det_configuracion_documento_electoral")
public class DetalleConfiguracionDocumentoElectoral implements Serializable {

	private static final long serialVersionUID = -3020817336661764418L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_configuracion_documento_electoral")
    @SequenceGenerator(name = "generator_det_configuracion_documento_electoral", sequenceName = "seq_det_configuracion_documento_electoral_pk", allocationSize = 1)
    @Column(name = "n_det_configuracion_documento_electoral_pk")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_det_tipo_eleccion_documento_electoral", referencedColumnName = "n_det_tipo_eleccion_documento_electoral_pk")
    private DetalleTipoEleccionDocumentoElectoral detalleTipoEleccionDocumentoElectoral;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_seccion")
    private Seccion seccion;

    @Column(name = "n_tipo_dato")
    private Integer tipoDato;

    @Column(name = "n_habilitado")
    private Integer habilitado;

    @Column(name = "c_pixel_superior_x")
    private BigDecimal pixelTopX;

    @Column(name = "c_pixel_superior_y")
    private BigDecimal pixelTopY;

    @Column(name = "c_pixel_inferior_x")
    private BigDecimal pixelBottomX;

    @Column(name = "c_pixel_inferior_y")
    private BigDecimal pixelBottomY;
    
    @Column(name = "c_coordenada_relativa_superior_x")
    private BigDecimal coordenadaRelativaTopX;

    @Column(name = "c_coordenada_relativa_superior_y")
    private BigDecimal coordenadaRelativaTopY;
    
    @Column(name = "c_coordenada_relativa_inferior_x")
    private BigDecimal coordenadaRelativaBottomX;

    @Column(name = "c_coordenada_relativa_inferior_y")
    private BigDecimal coordenadaRelativaBottomY;

    @Column(name = "c_ancho")
    private BigDecimal width;

    @Column(name = "c_altura")
    private BigDecimal height;

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
