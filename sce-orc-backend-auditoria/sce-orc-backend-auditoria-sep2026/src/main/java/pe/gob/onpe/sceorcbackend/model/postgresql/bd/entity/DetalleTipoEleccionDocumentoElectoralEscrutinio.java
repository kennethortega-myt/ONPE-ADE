package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;


import lombok.*;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

import jakarta.persistence.*;
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
public class DetalleTipoEleccionDocumentoElectoralEscrutinio {

    @Id
    @Column(name = "n_det_tipo_eleccion_documento_electoral_escrutinio_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_seq_det_tipo_eleccion_documento_electoral_escrutinio")
    @SequenceGenerator(name = "generator_seq_det_tipo_eleccion_documento_electoral_escrutinio", sequenceName = "seq_det_tipo_eleccion_documento_electoral_escrutinio_pk", allocationSize = 1)
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long	id;

    @Column(name="n_eleccion")
    private Integer eleccion;

    @Column(name = "n_distrito_electoral")
    private Integer distritoElectoral;

    @Column(name = "n_documento_electoral")
    private Integer documentoElectoral;

    @Column(name = "n_activo")
    private Integer activo;

    @Column(name = "c_aud_usuario_creacion")
    private String 	usuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    private Date fechaCreacion;

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
