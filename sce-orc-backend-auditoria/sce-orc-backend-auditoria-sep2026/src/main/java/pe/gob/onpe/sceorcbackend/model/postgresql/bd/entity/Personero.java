package pe.gob.onpe.sceorcbackend.model.postgresql.bd.entity;

import java.util.Date;
import java.util.Objects;

import pe.gob.onpe.sceorcbackend.utils.RedUtils;
import pe.gob.onpe.sceorcbackend.utils.SceConstantes;

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
@Table(name = "tab_personero")
public class Personero {

  @Id
  @Column(name = "n_personero_pk")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "generator_personero")
  @SequenceGenerator(name = "generator_personero", sequenceName = "seq_tab_personero_pk", allocationSize = 1)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "n_mesa", referencedColumnName = "n_mesa_pk", nullable = false)
  private Mesa mesa;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "n_agrupacion_politica", referencedColumnName = "n_agrupacion_politica_pk", nullable = true)
  private AgrupacionPolitica agrupacionPolitica;

  @Column(name = "c_documento_identidad")
  private String documentoIdentidad;

  @Column(name = "c_nombres")
  private String nombres;

  @Column(name = "c_apellido_paterno")
  private String apellidoPaterno;

  @Column(name = "c_apellido_materno")
  private String apellidoMaterno;

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
