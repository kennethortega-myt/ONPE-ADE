package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;


import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity.rectangle.le.json.DetMmRectanguloPaginaData;

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
@Table(name = "det_mm_rectangulo")
public class DetMmRectangulo implements Serializable {
	
	private static final long serialVersionUID = -3674411438422344817L;

    @Id
    @Column(name = "n_det_mm_rectangulo_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_mm_rectangulo")
    @SequenceGenerator(name = "generator_det_mm_rectangulo", sequenceName = "seq_det_mm_rectangulo_pk", allocationSize = 1)
    private Long id;

    @Column(name="n_mesa") //ante mesa
    private Long mesaId;

    @Column(name="c_tipo") //antes tipo
    private String type;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name="c_paginas", columnDefinition = "jsonb")//antes paginas
    private List<DetMmRectanguloPaginaData> paginas;

}
