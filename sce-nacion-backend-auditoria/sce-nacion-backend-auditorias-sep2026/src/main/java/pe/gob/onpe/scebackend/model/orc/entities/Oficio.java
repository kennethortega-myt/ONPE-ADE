package pe.gob.onpe.scebackend.model.orc.entities;

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

    private static final long serialVersionUID = 6908905512533486942L;

    @Id
    @Column(name = "n_oficio_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_cab_oficio_pk")
    @SequenceGenerator(name = "seq_cab_oficio_pk", sequenceName = "seq_cab_oficio_pk", allocationSize = 1)
    private Integer id;

    @Column(name = "c_id_oficio_cc")
    private String idCc;

    @Column(name = "c_nombre_oficio")
    private String nombreOficio;
    
    @Builder.Default
    @Column(name = "n_estado", nullable = false)
    private Integer estadoJne = 0;

    @Column(name = "c_estado_oficio")
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_archivo_pdf", referencedColumnName = "n_archivo_pk")
    private Archivo archivo;

    @Column(name = "d_fecha_envio")
    private Date fechaEnvio;

    @Column(name = "d_fecha_respuesta")
    private Date fechaRespuesta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_centro_computo", referencedColumnName = "n_centro_computo_pk")
    private CentroComputo centroComputo;
    
    @Column(name = "n_activo")
    private Integer activo;

    @Column(name = "c_aud_usuario_creacion")
    private String usuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    private Date fechaCreacion;

    @Column(name = "c_aud_usuario_modificacion")
    private String usuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    private Date fechaModificacion;

}