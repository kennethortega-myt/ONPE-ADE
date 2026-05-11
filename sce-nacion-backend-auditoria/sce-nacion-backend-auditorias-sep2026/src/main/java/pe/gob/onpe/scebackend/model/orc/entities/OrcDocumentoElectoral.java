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
@Table(name = "tab_documento_electoral")
public class OrcDocumentoElectoral implements Serializable {

    private static final long serialVersionUID = 2405172041950251807L;

	@Id
    @Column(name = "n_documento_electoral_pk")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_documento_electoral_padre", referencedColumnName = "n_documento_electoral_pk", nullable = true)
	private OrcDocumentoElectoral documentoElectoralPadre;
    
    @Column(name = "c_nombre")
    private String nombre;
    
    @Column(name = "c_abreviatura")
    private String abreviatura;
    
    @Column(name = "n_tipo_imagen")
    private Integer tipoImagen;
 
    @Column(name = "n_escanear_ambas_caras")
    private Integer escanerAmbasCaras;
    
    @Column(name = "n_tamanio_hoja")
    private Integer tamanioHoja;
    
    @Column(name = "n_multipagina")
    private Integer multipagina;
    
    @Column(name = "c_codigo_barra_pixel_superior_x")
    private String codigoBarraPixelTopX;
    
    @Column(name = "c_codigo_barra_pixel_superior_y")
    private String codigoBarraPixelTopY;
    
    @Column(name = "c_codigo_barra_pixel_inferior_x")
    private String codigoBarraPixelBottomX;
    
    @Column(name = "c_codigo_barra_pixel_inferior_y")
    private String codigoBarraPixelBottomY;
    
    @Column(name = "c_codigo_barra_coordenada_relativa_superior_x")
    private String codigoBarraCoordenadaRelativaTopX;
    
    @Column(name = "c_codigo_barra_coordenada_relativa_superior_y")
    private String codigoBarraCoordenadaRelativaTopY;
    
    @Column(name = "c_codigo_barra_coordenada_relativa_inferior_x")
    private String codigoBarraCoordenadaRelativaBottomX;
    
    @Column(name = "c_codigo_barra_coordenada_relativa_inferior_y")
    private String codigoBarraCoordenadaRelativaBottomY;
    
    @Column(name = "c_codigo_barra_ancho")
    private String codigoBarraWidth;
    
    @Column(name = "c_codigo_barra_altura")
    private String codigoBarraHeight;
    
    @Column(name = "n_codigo_barra_orientacion")
    private Integer codigoBarraOrientacion;

    @Column(name="n_configuracion_general")
    private Integer configuracionGeneral;
    
    @Column(name = "n_activo")
    private Integer activo;
    
    @Column(name = "n_visible")
    private Integer visible;

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
