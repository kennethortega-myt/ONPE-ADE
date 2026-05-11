package pe.gob.onpe.scebackend.model.orc.entities;

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
import pe.gob.onpe.scebackend.utils.SceConstantes;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "tab_padron_electoral")
public class PadronElectoral implements Serializable {

	private static final long serialVersionUID = 5313044592036862874L;
	
	@Id
	@Column(name = "n_padron_electoral_pk")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "n_mesa", referencedColumnName = "n_mesa_pk", nullable = false)
	private Mesa mesa;
	
	@Column(name = "c_mesa")
	private String codigoMesa;
	
	@Column(name = "n_tipo_documento_identidad")
	private Integer idTipoDocumentoIdentidad;
	
	@Column(name = "c_documento_identidad")
	private String documentoIdentidad;
	
	@Column(name = "c_nombres")
	private String nombres;
	
	@Column(name = "c_apellido_paterno")
	private String apellidoPaterno;
	
	@Column(name = "c_apellido_materno")
	private String apellidoMaterno;
	
	@Column(name = "n_orden")
	private Long orden;
	
	@Column(name = "c_ubigeo")
	private String ubigeo;
	
	@Column(name = "c_ubigeo_reniec")
	private String ubigeoReniec;
	
	@Column(name = "n_sexo")
	private Integer sexo;
	
	@Column(name = "n_vd")
	private Integer vd;
	
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
