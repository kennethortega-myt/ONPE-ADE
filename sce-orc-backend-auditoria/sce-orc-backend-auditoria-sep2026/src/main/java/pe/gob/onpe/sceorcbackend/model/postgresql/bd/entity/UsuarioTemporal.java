package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "tab_usuario_temporal")
public class UsuarioTemporal {

  @Id
  @Column(name = "n_usuario_temporal_pk")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_usuario_temporal")
  @SequenceGenerator(name = "generator_usuario_temporal", sequenceName = "seq_tab_usuario_temporal_pk", allocationSize = 1)
  //@GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "c_usuario")
  private String usuario;

  @Column(name = "c_perfil")
  private String perfil;

  @Column(name = "n_sesion_activa")
  private Integer sesionActiva;

  @Column(name = "c_aud_usuario_creacion")
  private String 	usuarioCreacion;

  @Column(name = "d_aud_fecha_creacion")
  private Date fechaCreacion;

  @Column(name = "c_aud_usuario_modificacion")
  private String	usuarioModificacion;

  @Column(name = "d_aud_fecha_modificacion")
  private Date	fechaModificacion;
}
