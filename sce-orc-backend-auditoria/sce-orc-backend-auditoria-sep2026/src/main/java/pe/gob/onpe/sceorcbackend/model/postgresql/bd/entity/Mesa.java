package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import pe.gob.onpe.sceorcbackend.utils.RedUtils;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tab_mesa")
public class Mesa implements Serializable {

  private static final long serialVersionUID = -6670025641653069615L;

  @Id
  @Column(name = "n_mesa_pk")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "n_local_votacion", referencedColumnName = "n_local_votacion_pk", nullable = false)
  private LocalVotacion localVotacion;

  @Column(name = "c_mesa")
  private String codigo; // mesa

  @Column(name = "n_cantidad_electores_habiles")
  private Integer cantidadElectoresHabiles;

  @Column(name = "n_cantidad_electores_habiles_extranjeros")
  private Integer cantidadElectoresHabilesExtranjeros;

  @Column(name = "n_discapacidad")
  private Integer discapacidad;

  @Column(name = "n_solucion_tecnologica")
  private Long solucionTecnologica;

  @Column(name = "c_estado_mesa")
  private String estadoMesa;

  @Column(name = "c_estado_digitalizacion_le")
  private String estadoDigitalizacionLe;

  @Column(name = "c_estado_digitalizacion_mm")
  private String estadoDigitalizacionMm;

  @Column(name = "c_usuario_asignado_le")
  private String usuarioAsignadoLe;

  @Column(name = "d_aud_fecha_usuario_asignado_le")
  private Date fechaAsignadoLe;

  @Column(name = "c_usuario_asignado_mm")
  private String usuarioAsignadoMm;

  @Column(name = "d_aud_fecha_usuario_asignado_mm")
  private Date fechaAsignadoMm;

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

  @Column(name = "c_estado_digitalizacion_pr")
  private String estadoDigitalizacionPr;

  @Column(name = "c_usuario_asignado_pr")
  private String usuarioAsignadoPr;

  @Column(name = "d_aud_fecha_usuario_asignado_pr")
  private Date fechaAsignadoPr;

  @Column(name = "c_estado_digitalizacion_me")
  private String estadoDigitalizacionMe;

  @Column(name = "c_usuario_asignado_me")
  private String usuarioAsignadoMe;

  @Column(name = "d_aud_fecha_usuario_asignado_me")
  private Date fechaAsignadoMe;

  @Column(name = "c_usuario_control_mm")
  private String usuarioControlMm;

  @Column(name = "d_aud_fecha_usuario_control_mm")
  private Date fechaUsuarioControlMm;

  @Column(name = "c_usuario_control_le")
  private String usuarioControlLe;

  @Column(name = "d_aud_fecha_usuario_control_le")
  private Date fechaUsuarioControlLe;
  
  @Column(name = "c_ip_servidor_cliente_transmision")
  private String ipServer;

  @Column(name = "c_nombre_servidor_cliente_transmision")
  private String hostName;
  
  @Column(name = "n_transmision")
  private Long idTransmision;
  
  @Column(name = "c_codigo_centro_computo")
  private String centroCc;

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
