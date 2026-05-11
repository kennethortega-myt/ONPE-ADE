package pe.gob.onpe.scebackend.model.orc.entities;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Entity
@Table(name = "det_otro_documento")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetOtroDocumento implements Serializable {

	private static final long serialVersionUID = 2449811328261226287L;

	@Id
    @Column(name = "n_det_otro_documento_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_otro_documento")
    @SequenceGenerator(name = "generator_det_otro_documento", sequenceName = "seq_det_otro_documento_pk", allocationSize = 1)
    private Long id;
	
	@Column(name = "c_id_det_otro_documento_cc")
	private String idCc;

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
