package pe.gob.onpe.scebackend.model.orc.entities;

import lombok.*;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "tab_autorizacion")
public class TabAutorizacion implements Serializable {
    private static final long serialVersionUID = 3739084144320042064L;

    @Id
    @Column(name = "n_tab_autorizacion_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_tab_autorizacion")
    @SequenceGenerator(name = "generator_tab_autorizacion", sequenceName = "seq_tab_autorizacion_pk", allocationSize = 1)
    private Long    id;

    @Column(name ="n_numero_autorizacion")
    private Long numeroAutorizacion;

    @Column(name ="c_estado_aprobacion")
    private String estadoAprobacion;

    @Column(name ="n_autorizacion")
    private Integer autorizacion;

    @Column(name ="c_tipo_autorizacion")
    private String tipoAutorizacion;

    @Column(name ="c_detalle")
    private String detalle;

    @Column(name ="n_activo")
    private Integer activo;

    @Column(name ="c_codigo_centro_computo")
    private String codigoCentroComputo;

    @Column(name ="c_tipo_documento")
    private String tipoDocumento;

    @Column(name ="n_id_documento")
    private Long idDocumento;

    @Column(name ="c_aud_usuario_creacion")
    private String usuarioCreacion;

    @Column(name ="d_aud_fecha_creacion")
    private Date fechaCreacion;

    @Column(name ="c_aud_usuario_modificacion")
    private String usuarioModificacion;

    @Column(name ="d_aud_fecha_modificacion")
    private Date fechaModificacion;
}
