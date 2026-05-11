package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "det_otro_documento")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetOtroDocumento implements Serializable {

	private static final long serialVersionUID = 7214721003910699977L;

	@Id
    @Column(name = "n_det_otro_documento_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_otro_documento")
    @SequenceGenerator(name = "generator_det_otro_documento", sequenceName = "seq_det_otro_documento_pk", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_otro_documento", referencedColumnName = "n_otro_documento_pk")
    private CabOtroDocumento cabOtroDocumento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_mesa", referencedColumnName = "n_mesa_pk")
    private Mesa mesa;

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
    private Date audFechaModificacion;

}
