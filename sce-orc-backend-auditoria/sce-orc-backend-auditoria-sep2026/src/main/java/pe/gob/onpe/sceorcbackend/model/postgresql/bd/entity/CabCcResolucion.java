package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.PrePersist;
import lombok.Data;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;
import java.util.Date;
import java.util.Objects;


@Data
@Entity
@Table(name = "cab_cc_resolucion")
public class CabCcResolucion {

  @Id
  @Column(name = "n_cc_resolucion_pk")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_cab_cc_resolucion")
  @SequenceGenerator(name = "generator_cab_cc_resolucion", sequenceName = "seq_cab_cc_resolucion_pk", allocationSize = 1)
  private Long id;

  @Column(name = "n_acta")
  private Long acta;

  @Column(name = "n_det_ubigeo_eleccion")
  private Integer detUbigeoEleccion;

  @Column(name = "n_mesa")
  private Integer mesa;

  @Column(name = "c_estado_cambio")
  private String estadoCambio;

  @Column(name = "n_resolucion")
  private Integer resolucion;

  @Column(name = "c_numero_copia")
  private String numeroCopia;


  @Column(name = "c_digito_chequeo_escrutinio")
  private String digitoChequeoEscrutinio;

  @Column(name = "c_digito_chequeo_instalacion")
  private String digitoChequeoInstalacion;

  @Column(name = "c_digito_chequeo_sufragio")
  private String digitoChequeoSufragio;

  @Column(name = "n_electores_habiles")
  private Integer electoresHabiles;

  @Column(name = "n_electores_extranjeros")
  private Integer electoresExtranjeros;

  @Column(name = "n_cvas")
  private Integer cvas;

  @Column(name = "c_ilegible_cvas")
  private String ilegibleCvas;

  @Column(name = "n_total_votos")
  private Integer totalVotos;

  @Column(name = "c_estado_acta")
  private String estadoActa;

  @Column(name = "c_estado_computo")
  private String estadoCc;

  @Column(name = "c_estado_acta_resolucion")
  private String estadoActaResolucion;

  @Column(name = "c_estado_digitalizacion")
  private String estadoDigitalizacion;

  @Column(name = "c_estado_error_material")
  private String estadoErrorMaterial;

  @Column(name = "c_hora_escrutinio_manual")
  private String horaEscrutinioManual;

  @Column(name = "c_hora_instalacion_manual")
  private String horaInstalacionManual;

  @Column(name = "c_observacion_jne")
  private String observacionJne;

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

  @Override
  public String toString() {
    return "CabCcResolucion{" +
        "id=" + id +
        ", numeroCopia='" + numeroCopia + '\'' +
        ", digitoChequeoEscrutinio='" + digitoChequeoEscrutinio + '\'' +
        ", digitoChequeoInstalacion='" + digitoChequeoInstalacion + '\'' +
        ", estadoActa='" + estadoActa + '\'' +
        ", estadoCc='" + estadoCc + '\'' +
        ", estadoActaResolucion='" + estadoActaResolucion + '\'' +
        ", estadoDigitalizacion='" + estadoDigitalizacion + '\'' +
        ", estadoErrorMaterial='" + estadoErrorMaterial + '\'' +
        '}';
  }

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
