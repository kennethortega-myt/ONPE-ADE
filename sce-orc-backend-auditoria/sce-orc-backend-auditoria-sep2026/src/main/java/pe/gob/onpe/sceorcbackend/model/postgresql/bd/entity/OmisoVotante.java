package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;

import lombok.*;
import pe.gob.onpe.sceorcbackend.utils.RedUtils;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;
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
@Table(name = "tab_omiso_votante")
public class OmisoVotante implements Serializable {

	private static final long serialVersionUID = -7235094147761638791L;

    @Id
    @Column(name = "n_omiso_votante_pk")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_tab_omiso_votante_pk")
    @SequenceGenerator(name = "generator_tab_omiso_votante_pk", sequenceName = "seq_tab_omiso_votante_pk", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_mesa", referencedColumnName = "n_mesa_pk")
    private Mesa mesa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "n_padron_electoral", referencedColumnName = "n_padron_electoral_pk")
    private PadronElectoral padronElectoral;

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
    
    @Column(name = "c_ip_servidor_cliente_transmision")
    private String ipServer;
    
    @Column(name = "c_nombre_servidor_cliente_transmision")
    private String hostName;
    
    @PrePersist
    public void setDefaultValues() {
      if (Objects.isNull(activo)) {
        activo = SceConstantes.ACTIVO;
      }

      if (Objects.isNull(fechaCreacion)) {
        fechaCreacion = new Date();
      }
      
      if (Objects.isNull(this.ipServer)) {
      	this.ipServer = RedUtils.obtenerIpLocal();
      }
      
      if (Objects.isNull(this.hostName)) {
          this.hostName = RedUtils.obtenerNombreHost();
      }
      
    }

}
