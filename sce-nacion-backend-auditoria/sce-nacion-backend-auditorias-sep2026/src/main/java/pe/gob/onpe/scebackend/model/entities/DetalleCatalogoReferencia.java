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
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "det_catalogo_referencia")
public class DetalleCatalogoReferencia extends DatosAuditoria implements Serializable {

	private static final long serialVersionUID = -3584446628307037156L;

	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_catalogo_referencia")
    @SequenceGenerator(name = "generator_det_catalogo_estructura", sequenceName = "seq_det_catalogo_referencia_pk", allocationSize = 1)
    @Column(name = "n_det_catalogo_referencia_pk")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_catalogo", referencedColumnName = "n_catalogo_pk")
    private Catalogo catalogo;

    @Column(name = "c_tabla_referencia")
    private String tablaReferencia;

    @Column(name = "n_activo")
    private Integer activo;

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
