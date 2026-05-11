package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "det_otro_documento")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImportDetOtroDocumento {

    @Id
    @Column(name = "n_det_otro_documento_pk")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_otro_documento", referencedColumnName = "n_otro_documento_pk")
    private ImportCabOtroDocumento cabOtroDocumento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_mesa", referencedColumnName = "n_mesa_pk")
    private ImportMesa mesa;

    @Column(name = "c_tipo_documento")
    private String codTipoDocumento;

    @Column(name = "c_tipo_perdida")
    private String codTipoPerdida;

    @Column(name = "n_activo")
    private Integer activo;

    @Column(name = "c_aud_usuario_creacion")
    private String audUsuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    private Date audFechaCreacion;

    @Column(name = "c_aud_usuario_modificacion")
    private String audUsuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    @Setter(AccessLevel.NONE)
    private Date audFechaModificacion;
    
    @PrePersist
	public void prePersist() {
		this.audFechaCreacion = new Date();
	}

	@PreUpdate
	public void preUpdate() {
		this.audFechaModificacion = new Date();
	}

}
