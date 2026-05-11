package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;

import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.rectangle.le.json.DetLeRectanguloPaginaData;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "det_le_rectangulo")
public class DetLeRectangulo implements Serializable {

	private static final long serialVersionUID = -8671994429073918820L;

    @Id
    @Column(name = "n_det_le_rectangulo_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_le_rectangulo")
    @SequenceGenerator(name = "generator_det_le_rectangulo", sequenceName = "seq_det_le_rectangulo_pk", allocationSize = 1)
    private Long id;

    @Column(name="n_mesa")
    private Long mesaId;

    @Column(name="c_tipo")// antes tipo
    private String type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name="c_paginas", columnDefinition = "jsonb") //antes paginas
    private List<DetLeRectanguloPaginaData> paginas;

}
