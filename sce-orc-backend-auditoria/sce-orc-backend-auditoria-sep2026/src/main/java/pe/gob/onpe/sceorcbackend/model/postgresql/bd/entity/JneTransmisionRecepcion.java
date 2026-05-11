package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;

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
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tab_jne_transmision_recepcion")
public class JneTransmisionRecepcion implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tab_jne_transmision_recepcion_pk")
    @SequenceGenerator(name = "seq_tab_jne_transmision_recepcion_pk", sequenceName = "seq_tab_jne_transmision_recepcion_pk", allocationSize = 1)
    @Column(name = "n_jne_transmision_recepcion_pk")
    private Long id;

    @Column(name = "c_codigo_jne_envio")
    private String codigoJneEnvio;

    @Column(name = "c_trama", columnDefinition = "TEXT")
    private String trama;

    @Column(name = "c_mensaje", columnDefinition = "TEXT")
    private String mensaje;

    @Builder.Default
    @Column(name = "n_enviado", nullable = false)
    private Short enviado = 0;

    @Builder.Default
    @Column(name = "n_intentos", nullable = false)
    private Short intentos = 0;

    @Builder.Default
    @Column(name = "n_estado", nullable = false)
    private Short estado = 0;

    @Builder.Default
    @Column(name = "n_activo", nullable = false)
    private Short activo = 1;

    @Column(name = "c_aud_usuario_creacion", nullable = false, length = 50)
    private String audUsuarioCreacion;

    @Column(name = "d_aud_fecha_creacion", nullable = false)
    private Date audFechaCreacion;

    @Column(name = "c_aud_usuario_modificacion", length = 50)
    private String audUsuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    private Date audFechaModificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_archivo", referencedColumnName = "n_archivo_pk", nullable = true)
    private Archivo archivo;
}
