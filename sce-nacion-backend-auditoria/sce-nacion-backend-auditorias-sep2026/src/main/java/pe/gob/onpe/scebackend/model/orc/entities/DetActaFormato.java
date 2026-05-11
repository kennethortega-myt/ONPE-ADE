package pe.gob.onpe.scebackend.model.orc.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.*;
import pe.gob.onpe.scebackend.utils.SceConstantes;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "det_acta_formato")
public class DetActaFormato implements Serializable  {

	private static final long serialVersionUID = -3666944596425716254L;

	@Id
	@Column(name = "n_det_acta_formato_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_acta_formato")
    @SequenceGenerator(name = "generator_det_acta_formato", sequenceName = "seq_det_acta_formato_pk", allocationSize = 1)
	private Long id;
	
	@Column(name = "c_id_det_acta_formato_cc")
	private String idCc;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_cab_acta_formato", referencedColumnName = "n_cab_acta_formato_pk")
	private CabActaFormato cabActaFormato;
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_acta", referencedColumnName = "n_acta_pk")
    private Acta acta;
	
	@Column(name = "n_activo")
	private Integer activo;
	
	@Column(name = "c_aud_usuario_creacion")
	private String 	usuarioCreacion;
	
	@Column(name = "d_aud_fecha_creacion")
	private Date  	fechaCreacion;
	
	@Column(name = "c_aud_usuario_modificacion")
	private String	usuarioModificacion;
	
	@Column(name = "d_aud_fecha_modificacion")
	private Date	fechaModificacion;
	
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
