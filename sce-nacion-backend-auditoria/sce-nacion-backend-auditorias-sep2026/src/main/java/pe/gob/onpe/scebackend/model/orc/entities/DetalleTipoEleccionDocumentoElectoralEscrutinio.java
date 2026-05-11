package pe.gob.onpe.scebackend.model.orc.entities;


import lombok.*;
import pe.gob.onpe.scebackend.model.entities.DatosAuditoria;
import pe.gob.onpe.scebackend.utils.SceConstantes;

import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "det_tipo_eleccion_documento_electoral_escrutinio")
public class DetalleTipoEleccionDocumentoElectoralEscrutinio extends DatosAuditoria implements Serializable {

    private static final long serialVersionUID = 2405172041950251807L;

    @Id
    @Column(name = "n_det_tipo_eleccion_documento_electoral_escrutinio_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_seq_det_tipo_eleccion_documento_electoral_escrutinio")
    @SequenceGenerator(name = "generator_seq_det_tipo_eleccion_documento_electoral_escrutinio", sequenceName = "seq_det_tipo_eleccion_documento_electoral_escrutinio_pk", allocationSize = 1)
    private Long	id;

    @Column(name="n_eleccion")
    private Integer eleccion;

    @Column(name = "n_distrito_electoral")
    private Integer distritoElectoral;

    @Column(name = "n_documento_electoral")
    private Integer documentoElectoral;

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
