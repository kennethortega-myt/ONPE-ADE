package pe.gob.onpe.scebackend.model.orc.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@Entity
@Table(name = "tab_puesta_cero_pr")
public class PuestaCeroPr implements Serializable {

    private static final long serialVersionUID = 5313044592036862874L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_puesta_cero_pr")
    @SequenceGenerator(
        name = "seq_puesta_cero_pr",
        sequenceName = "seq_tab_puesta_cero_pr_pk",
        allocationSize = 1
    )
    @Column(name = "n_puesta_cero_pr_pk")
    private Long id;

    @Column(name = "c_estado", nullable = false)
    private String estado;

    @Column(name = "n_activo", nullable = false)
    private Integer activo;

    @Column(name = "n_intento", nullable = false)
    private Integer intento;
    
    @JdbcTypeCode(SqlTypes.JSON)
	@Column(name="c_respuesta_pc_stae", columnDefinition = "jsonb", nullable = true)
    private String respuestaPcStae;
    
    @Column(name = "c_aud_usuario_creacion")
    private String usuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    private Date fechaCreacion;

    @Column(name = "c_aud_usuario_modificacion")
    private String usuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    private Date fechaModificacion;
    
    
    
}