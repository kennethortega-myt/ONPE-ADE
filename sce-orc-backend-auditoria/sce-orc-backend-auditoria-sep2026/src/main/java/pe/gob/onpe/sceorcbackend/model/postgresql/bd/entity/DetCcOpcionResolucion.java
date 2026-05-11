package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.PrePersist;
import lombok.Data;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;
import java.util.Date;
import java.util.Objects;

@Data
@Entity
@Table(name = "det_cc_opcion_resolucion")
public class DetCcOpcionResolucion {

  @Id
  @Column(name = "n_det_cc_opcion_resolucion_pk")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_det_cc_opcion_resolucion_pk")
  @SequenceGenerator(name = "generator_det_cc_opcion_resolucion_pk", sequenceName = "seq_det_cc_opcion_resolucion_pk", allocationSize = 1)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "n_det_cc_resolucion", referencedColumnName = "n_det_cc_resolucion_pk")
  private DetCcResolucion detCcResolucion;

  @Column(name = "n_det_acta_opcion")
  private Long idDetActaOpcion;

  @Column(name = "n_posicion")
  private Integer posicion;

  @Column(name = "n_votos")
  private Integer votos;

  @Column(name = "c_estado_error_material")
  private String estadoErrorMaterial;

  @Column(name = "c_ilegible")
  private String ilegible;

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
