package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import jakarta.persistence.*;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "det_catalogo_estructura")
public class ImportOrcDetalleCatalogoEstructura {

    @Id
    @Column(name = "n_det_catalogo_estructura_pk")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_catalogo", referencedColumnName = "n_catalogo_pk")
    private ImportOrcCatalogo catalogo;

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
    @Setter(AccessLevel.NONE)
    private Date fechaModificacion;

    @PrePersist
	public void prePersist() {
		this.fechaCreacion = new Date();
	}

	@PreUpdate
	public void preUpdate() {
		this.fechaModificacion = new Date();
	}
    
    
}
