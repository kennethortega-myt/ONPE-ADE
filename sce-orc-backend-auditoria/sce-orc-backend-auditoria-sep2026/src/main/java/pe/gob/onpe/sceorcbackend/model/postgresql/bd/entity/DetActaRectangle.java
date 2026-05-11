package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;

import lombok.*;
import java.io.Serializable;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.rectangle.json.DetActaRectangleVoteData;
import jakarta.persistence.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "det_acta_rectangulo")
public class DetActaRectangle implements Serializable {

	private static final long serialVersionUID = -6871098031209697158L;

	@Id
    @Column(name = "n_det_acta_rectangulo_pk")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_acta_rectangulo")
	@SequenceGenerator(name = "generator_det_acta_rectangulo", sequenceName = "seq_det_acta_rectangulo_pk", allocationSize = 1)
	//@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

    @Column(name="n_seccion")
    private Integer seccion;

    @Column(name="n_acta")
    private Long actaId;

    @Column(name="n_eleccion")
    private Integer eleccionId;

    @Column(name="n_archivo")
    private Long archivo;

    @Column(name="c_tipo")
    private String type;

    @Column(name="b_valido")
    private Boolean valid;

    @Column(name="c_valor_modelo")
    private String totalVotos;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name="c_votos", columnDefinition = "jsonb")
    private DetActaRectangleVoteData values;

}