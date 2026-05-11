package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;


import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "tab_confirmacion_pc_cc")
public class TabConfirmPc implements Serializable {

    private static final long serialVersionUID = -4994612104454742948L;

    @Id
    @Column(name = "n_tab_confirmacion_pc_cc_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_tab_confirm_pc_cc")
    @SequenceGenerator(name = "generator_tab_confirm_pc_cc", sequenceName = "seq_tab_confirmacion_pc_cc_pk", allocationSize = 1)
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  id;

    @Column(name = "n_procesado")
    private Integer procesado;

    @Column(name = "n_activo")
    private Integer activo;

    @Column(name = "c_aud_usuario_creacion")
    private String 	usuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    private Date  	fechaCreacion;

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
