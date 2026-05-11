package pe.gob.onpe.scebackend.model.orc.entities;



import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import pe.gob.onpe.scebackend.utils.SceConstantes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
@Table(name = "det_parametro")
public class DetParametro implements Serializable {

  private static final long serialVersionUID = -3666944596425716254L;

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_parametro")
  @SequenceGenerator(name = "generator_det_parametro", sequenceName = "seq_det_parametro_pk", allocationSize = 1)
  @Column(name = "n_det_parametro_pk")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "n_parametro", referencedColumnName = "n_parametro_pk")
  private CabParametro parametro;

  @Column(name = "c_nombre")
  private String 	nombre;

  @Column(name = "c_valor")
  private String 	valor;

  @Column(name = "c_descripcion")
  private String 	descripcion;
  
  @Column(name = "n_tipo_dato")
  private Integer tipoDato;

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
