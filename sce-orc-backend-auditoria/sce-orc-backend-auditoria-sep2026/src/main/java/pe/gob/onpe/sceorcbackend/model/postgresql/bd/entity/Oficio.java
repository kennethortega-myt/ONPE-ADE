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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "cab_oficio")
public class Oficio implements Serializable {

    private static final long serialVersionUID = -1667539335770643636L;

    @Id
    @Column(name = "n_oficio_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_cab_oficio_pk")
    @SequenceGenerator(name = "seq_cab_oficio_pk", sequenceName = "seq_cab_oficio_pk", allocationSize = 1)
    private Integer id;

    @Column(name = "c_nombre_oficio")
    private String nombreOficio;

    @Column(name = "c_estado_oficio")
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_archivo_pdf", referencedColumnName = "n_archivo_pk")
    private Archivo archivo;

    @Column(name = "n_centro_computo")
    private Integer centroComputo;

    @Builder.Default
    @Column(name = "n_estado", nullable = false)
    private Integer estadoJne = 0;

    @Builder.Default
    @Column(name = "n_activo", nullable = false)
    private Integer activo = 1;

    @Column(name = "c_aud_usuario_creacion")
    private String usuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    private Date fechaCreacion;

    @Column(name = "c_aud_usuario_modificacion")
    private String usuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    private Date fechaModificacion;

    @Column(name = "d_fecha_envio")
    private Date fechaEnvio;

    @Column(name = "d_fecha_respuesta")
    private Date fechaRespuesta;

}
