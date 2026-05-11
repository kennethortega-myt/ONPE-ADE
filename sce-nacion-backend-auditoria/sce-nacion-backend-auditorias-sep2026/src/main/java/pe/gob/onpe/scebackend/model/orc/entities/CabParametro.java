package pe.gob.onpe.scebackend.model.orc.entities;



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

import pe.gob.onpe.scebackend.utils.SceConstantes;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "cab_parametro")
public class CabParametro implements Serializable {

  private static final long serialVersionUID = -3666944596425716256L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_cab_parametro")
  @SequenceGenerator(name = "generator_cab_parametro", sequenceName = "seq_cab_parametro_pk", allocationSize = 1)
  @Column(name = "n_parametro_pk")
  private Long id;

  @Column(name = "c_parametro")
  private String 	parametro;
  
  @Column(name = "c_perfiles_autorizados")
  private String 	perfilesAutorizados;

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