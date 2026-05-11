package pe.gob.onpe.sceorcbackend.model.postgresql.importar.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "tab_jurado_electoral_especial")
public class ImportTabJuradoElectoralEspecial implements Serializable {

	private static final long serialVersionUID = -527814015425070372L;

	@Id
    @Column(name = "n_jurado_electoral_especial_pk")
    private Integer id;

    @Column(name = "c_codigo_centro_computo")
    private String codigoCentroComputo;

    @Column(name = "c_nombre")
    private String nombre;

    @Column(name = "c_id_jee")
    private String idJee;

    @Column(name = "c_direccion")
    private String direccion;

    @Column(name = "c_apellido_paterno_representante")
    private String apellidoPaternoRepresentante;

    @Column(name = "c_apellido_materno_representante")
    private String apellidoMaternoRepresentante;

    @Column(name = "c_nombres_representante")
    private String nombresRepresentante;

    @Column(name = "n_activo")
    private Integer activo;

    @Column(name = "c_aud_usuario_creacion")
    private String usuarioCreacion;

    @Column(name = "d_aud_fecha_creacion")
    private Date fechaCreacion;

    @Column(name = "c_aud_usuario_modificacion")
    private String usuarioModificacion;

    @Column(name = "d_aud_fecha_modificacion")
    @Setter(AccessLevel.NONE)
    private Date fechaModificacion;
    
    @PrePersist
	public void prePersist() {
		this.fechaCreacion = new Date();
	}

	@PreUpdate
	public void preUpdate() {
		this.fechaModificacion = new Date();
	}
    
}